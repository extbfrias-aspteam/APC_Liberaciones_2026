using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Globalization;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using iRisCore.Tools;
using System.Security.Principal;



namespace iRisCore.Controllers
{
    [Authorize]
    public class EnvioOtpPinController : Controller
    {

        private readonly IConfiguration _config;

        public EnvioOtpPinController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        public IActionResult EnvioOtpPin()
        {
            //  return View();
            return View();
        }

        [HttpPost]
        [Route("EnviarOtpPin/GeneraPinOTP")]
        public async Task<ActionResult> GeneraPinOTP([FromBody] GeneraPinOTPRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;

            var responseData = new GeneraPinOTPResponse();
            var jResult = new Response();
            var apiUrl = _config["EndPointsCore:generaPinOTP"];
            var userASP = _config["EndPointsCore:userASPPago"];
            var pwdASP = _config["EndPointsCore:passASPPago"];

            // Validación básica del modelo
            if (model == null || string.IsNullOrEmpty(model.clave) || string.IsNullOrEmpty(model.valor))
            {
                return Json(new
                {
                    detalle = "",
                    result = new Response
                    {
                        Codigo = -300,
                        Mensaje = "Faltan datos obligatorios",
                        Data = null
                    }
                });
            }
            model.usuarioId = int.TryParse(userId, out int uid) ? uid : 0;

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/generaPinOTP", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);

            try
            {
                var response = await client.ExecutePostAsync(request);

                // Siempre intentar deserializar la respuesta
                if (!string.IsNullOrEmpty(response.Content))
                {
                    responseData = JsonConvert.DeserializeObject<GeneraPinOTPResponse>(response.Content);
                }

                // Construir respuesta basada en el código del backend
                jResult = new Response
                {
                    Codigo = responseData?.codigo ?? -200, // Usar -200 si no viene código
                    Mensaje = responseData?.mensaje ?? "Error desconocido",
                    Data = responseData?.data
                };

                // Logging
                var requestToLog = new
                {
                    resource = request.Resource,
                    parameters = request.Parameters.Select(parameter => new
                    {
                        name = parameter.Name,
                        value = parameter.Value,
                        type = parameter.Type.ToString()
                    }),
                    method = request.Method.ToString(),
                    uri = client.BuildUri(request)
                };

                logiRis.Add($"Usuario:{returnUser}-Request:{request.Resource}{JsonConvert.SerializeObject(requestToLog)}");
                logiRis.Add($"Usuario:{returnUser}-Response:{request.Resource}{response.Content}");
            }
            catch (Exception ex)
            {
                jResult = new Response
                {
                    Codigo = -200,
                    Mensaje = $"Error interno: {ex.Message}",
                    Data = null
                };
                logiRis.Add($"Error al generar Pin OTP - Usuario:{returnUser} - Error:{ex.Message}");
            }

            return Json(new { detalle = "", result = jResult });
        }
    }
}
