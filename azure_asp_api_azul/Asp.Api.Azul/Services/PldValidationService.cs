using Asp.Api.Azul.Entities.DataBase;
using Asp.Api.Azul.Infrastructure.Utils;
using Asp.Api.Azul.Models.Entities;
using Asp.Api.Azul.Repositorys.PldBitacoraRepository;
using Microsoft.Extensions.Caching.Memory;
using System.Net.Http.Headers;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Asp.Api.Azul.Services
{
    public class PldValidationService : IPldValidationService
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly IMemoryCache _memoryCache;
        private readonly LoggerHelper _logger;
        private readonly IConfiguration _configuration;
        private readonly IPldBitacoraRepository _pldBitacoraRepository;

        private const string TokenCacheKey = "PldValidationToken";
        private const string DefaultCanal = "Asp Azul";

        private static readonly JsonSerializerOptions _jsonOptions = new()
        {
            Encoder = System.Text.Encodings.Web.JavaScriptEncoder.UnsafeRelaxedJsonEscaping,
            DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingNull
        };

        public PldValidationService(
            IHttpClientFactory httpClientFactory,
            IMemoryCache memoryCache,
            LoggerHelper logger,
            IConfiguration configuration,
            IPldBitacoraRepository pldBitacoraRepository)
        {
            _httpClientFactory = httpClientFactory;
            _memoryCache = memoryCache;
            _logger = logger;
            _configuration = configuration;
            _pldBitacoraRepository = pldBitacoraRepository;
        }

        private async Task<string?> GetTokenAsync()
        {
            if (_memoryCache.TryGetValue(TokenCacheKey, out string cachedToken))
            {
                return cachedToken;
            }

            _logger.LogInformation("Solicitando nuevo token para PLD Validation");

            var client = _httpClientFactory.CreateClient("PldValidationClient");

            var requestBody = new
            {
                username = _configuration["PldValidation:Username"],
                password = _configuration["PldValidation:Password"]
            };

            var content = new StringContent(JsonSerializer.Serialize(requestBody, _jsonOptions), System.Text.Encoding.UTF8, "application/json");
            var authUrl = _configuration["PldValidation:AuthUrl"];

            var response = await client.PostAsync(authUrl, content);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogInformation($"Error al obtener token PLD: {response.StatusCode}");
                return null;
            }

            var token = await response.Content.ReadAsStringAsync();

            if (string.IsNullOrEmpty(token))
            {
                _logger.LogInformation("Token de PLD vacío.");
                return null;
            }

            // Expiración de 50 minutos para asegurar que al menos faltan 10 minutos para caducar (de los 60 min totales de vida).
            var cacheOptions = new MemoryCacheEntryOptions
            {
                AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(50)
            };

            _memoryCache.Set(TokenCacheKey, token, cacheOptions);

            return token;
        }

        private string? CleanString(string input) => string.IsNullOrWhiteSpace(input) ? null : input.Trim();

        private async Task ExecuteValidationRequest(object requestBody, BitacoraPld bitacora)
        {
            bool matchFound = false;
            try
            {
                var body = JsonSerializer.Serialize(requestBody, _jsonOptions);
                _logger.LogInformation(body);
                bitacora.request_body = body;

                var token = await GetTokenAsync();
                if (string.IsNullOrEmpty(token))
                {
                    bitacora.error_message = "No se pudo obtener token PLD";
                    await _pldBitacoraRepository.Insert(bitacora);
                    _logger.LogInformation("No se pudo obtener token PLD, se omite validación.");
                    return;
                }

                var client = _httpClientFactory.CreateClient("PldValidationClient");

                using var content = new StringContent(body, System.Text.Encoding.UTF8, "application/json");
                var validateUrl = _configuration["PldValidation:ValidateUrl"];

                using var request = new HttpRequestMessage(HttpMethod.Post, validateUrl)
                {
                    Content = content
                };
                request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);

                _logger.LogInformation("Inicia consumo de PLD");
                var response = await client.SendAsync(request);

                if (!response.IsSuccessStatusCode)
                {
                    bitacora.error_message = $"Fallo consumo PLD HTTP Status: {response.StatusCode}";
                    await _pldBitacoraRepository.Insert(bitacora);
                    _logger.LogInformation(bitacora.error_message);
                    return;
                }

                var jsonResponse = await response.Content.ReadAsStringAsync();
                _logger.LogInformation($"Resultado PLD :: {jsonResponse}");

                var result = JsonSerializer.Deserialize<PldValidationResponse>(jsonResponse, _jsonOptions);

                if (result == null || result.codigo != "OK")
                {
                    _logger.LogInformation($"Error de PLD Validation, Código devuelto: {result?.codigo ?? "nulo"}");
                    return;
                }

                if (result.contenido != null && result.contenido.match)
                {
                    matchFound = true;
                    _logger.LogInformation($"Error de PLD Validation (Coincidencia encontrada), Match: true, MatchType: {result.contenido.matchType}, Score: {result.contenido.score}, ClientePldId: {result.contenido.clientePldId}");
                    throw new ErrorGenerico("Error al crear la referencia");
                }

                _logger.LogInformation("Validación PLD exitosa.");
            }
            catch (Exception ex) when (!matchFound)
            {
                bitacora.error_message = ex.Message;
                await _pldBitacoraRepository.Insert(bitacora);
                _logger.LogInformation($"Error en validación PLD (no bloqueante): {ex.Message}");
            }
        }

        public async Task ValidatePersonAsync(string nombre, string apellidoPaterno, string apellidoMaterno, string rfc, string curp, string personalidad, int usuarioSolicitante)
        {
            var requestBody = new
            {
                nombre = CleanString(nombre),
                apellidoPaterno = CleanString(apellidoPaterno),
                apellidoMaterno = CleanString(apellidoMaterno),
                rfc = CleanString(rfc),
                curp = CleanString(curp),
                personalidad = personalidad,
                canal = DefaultCanal,
                usuarioSolicitante = usuarioSolicitante
            };

            var bitacora = new BitacoraPld
            {
                rfc = CleanString(rfc),
                curp = CleanString(curp),
                id_pblu = usuarioSolicitante
            };

            await ExecuteValidationRequest(requestBody, bitacora);
        }

        public async Task ValidateCompanyAsync(string razonSocial, string rfc, string personalidad, int usuarioSolicitante)
        {
            var requestBody = new
            {
                nombre = CleanString(razonSocial),
                rfc = CleanString(rfc),
                personalidad = personalidad,
                canal = DefaultCanal,
                usuarioSolicitante = usuarioSolicitante
            };

            var bitacora = new BitacoraPld
            {
                rfc = CleanString(rfc),
                id_pblu = usuarioSolicitante
            };

            await ExecuteValidationRequest(requestBody, bitacora);
        }

        private class PldValidationResponse
        {
            [JsonPropertyName("codigo")]
            public string codigo { get; set; }

            [JsonPropertyName("contenido")]
            public PldValidationContent contenido { get; set; }
        }

        private class PldValidationContent
        {
            [JsonPropertyName("match")]
            public bool match { get; set; }

            [JsonPropertyName("matchType")]
            public string matchType { get; set; }

            [JsonPropertyName("score")]
            public decimal? score { get; set; }

            [JsonPropertyName("clientePldId")]
            public long? clientePldId { get; set; }
        }
    }
}