using DocumentFormat.OpenXml.EMMA;
using DocumentFormat.OpenXml.Spreadsheet;
using DocumentFormat.OpenXml.Wordprocessing;
using iRisCore.Access;
using iRisCore.Models;
using iRisCore.Services;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Org.BouncyCastle.Asn1.Ocsp;
using RestSharp;
using System.Globalization;
using System.Security.Claims;
using System.Security.Policy;
using System.Security.Principal;
using System.Text.Json.Nodes;

namespace iRisCore.Controllers
{
    public class BO_DefinicionReglasController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        private readonly Permissions _permissions;
        public BO_DefinicionReglasController(IConfiguration Config, TokenService tokenService, Permissions permissions)
        {
            _config = Config;
            _tokenService = tokenService;
            _permissions = permissions;

        }
        public IActionResult BO_DefinicionReglas()
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            string plataforma = identity.FindFirst(ClaimTypes.Sid)?.Value;
            ViewBag.CanAuthorize = _permissions.ValidarPermiso(user, int.Parse(plataforma), "AUT_REG_MMP");
            return View();
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetParticipantes()
        {
            var url = _config["EndPointsCore:apiURLParticipantes"];
            var endpoint = "/participantes/list";
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            
            var response = await _tokenService.SendRequestWithToken(returnUser, url + endpoint, Method.Get);
            var jResult = new ReglasResponseFr();

            if (!string.IsNullOrEmpty(response.Content))
            {
                try
                {
                    var responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                    if (responseData?.EiyuResponseCode == "OK")
                        try
                        {
                            var data = ((JToken)responseData.Content).ToObject<List<MyDataModel>>();
                            jResult = CrearResultado(1, responseData.Descripcion, data.Where(x => x.activo));
                        }
                        catch (Exception ex)
                        {
                            jResult = CrearResultado(-3, $"Error al convertir el contenido: {ex.Message}");
                        }
                    else
                        jResult = CrearResultado(0, responseData?.Descripcion ?? "Error de negocio no especificado");
                }
                catch (JsonException ex)
                {
                    jResult = CrearResultado(-2, $"Error al procesar la respuesta del servidor: {ex.Message}");
                }
            }
            else
                jResult = CrearResultado(-1, response.ErrorMessage ?? response.ErrorException?.Message ?? "Error desconocido en la petición");

            return Json(new { detalle = "", result = jResult });
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetReglas()
        {
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/definicion-reglas";

            return await EjecutarPeticionAsync<List<Reglas>>(url, endpoint, Method.Get);
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> GetConfigs(ConfigsRequest body)
        {
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/reglas-particpante-v1";

            var response = await EjecutarPeticionAsync<List<ConfReglas>>(url, endpoint, Method.Post, body);
            if (response is JsonResult jsonResult)
            {
                JObject json = JObject.FromObject(jsonResult.Value);
                JToken result = json["result"];
                List<ConfReglas> list = result["Data"].ToObject<List<ConfReglas>>();
                int codigo = result["Codigo"].Value<int>();
                string mensaje = result["Mensaje"].Value<string>();

                if (codigo == 1)
                {
                    var autorizadosRecientes = list
                    .Where(x => x.estatus == "Aprobado")
                    .GroupBy(x => x.idParticipante)
                    .Select(g => g
                        .OrderByDescending(x => DateTime.Parse(x.fechaCreacion, CultureInfo.InvariantCulture))
                        .First()
                    );

                    var pendientes = list
                        .Where(x => x.estatus == "Pendiente");

                    var resultadoFinal = autorizadosRecientes
                        .Concat(pendientes)
                        .OrderByDescending(x => DateTime.Parse(x.fechaCreacion, CultureInfo.InvariantCulture))
                        .ToList();

                    var resultFiltered = CrearResultado(codigo, mensaje, resultadoFinal);

                    return Json(new { detalle = "", result = resultFiltered });
                }
            }

            return response;
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> GetAuths(ConfigsRequest body)
        {
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/reglas-particpante-v1";


            return await EjecutarPeticionAsync<List<ConfReglas>>(url, endpoint, Method.Post, body);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetConfig(ConfReglasRequest body)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/reglas-particpante";
            body.usuario = user;

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, body);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetAuthConfig(AuthConfRequest body)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/autorizar/autorizar-limite";
            body.usuario = user;

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, body);
        }

        private ReglasResponseFr CrearResultado(int codigo, string mensaje, object? data = null)
        {
            return new ReglasResponseFr
            {
                Codigo = codigo,
                Mensaje = mensaje,
                Data = data ?? ""
            };
        }

        private async Task<IActionResult> EjecutarPeticionAsync<T>(string baseUrl, string endpoint, Method metodo, object? body = null, IDictionary<string, string>? headers = null)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var response = await _tokenService.SendRequestWithToken(user, baseUrl + endpoint, metodo, body);
            var jResult = new ReglasResponseFr();

            if (!string.IsNullOrEmpty(response.Content))
            {
                try
                {
                    ReglasResponseWs responseData = JsonConvert.DeserializeObject<ReglasResponseWs>(response.Content);

                    if (responseData?.code == "OK")
                    {
                        // Si el tipo T es object, no intentamos deserializar
                        if (typeof(T) == typeof(object))
                            jResult = CrearResultado(1, responseData.mensaje, responseData.content);
                        else
                            try
                            {
                                object? data = ((JToken)responseData.content).ToObject<T>();
                                jResult = CrearResultado(1, responseData.mensaje, data);
                            }
                            catch (Exception ex)
                            {
                                jResult = CrearResultado(-3, $"Error al convertir el contenido: {ex.Message}");
                            }
                    }
                    else
                        jResult = CrearResultado(0, responseData?.mensaje ?? "Error de negocio no especificado.");
                }
                catch (JsonException ex)
                {
                    jResult = CrearResultado(-2, $"Error al procesar la respuesta del servidor: {ex.Message}");
                }
            }
            else
                jResult = CrearResultado(-1, response.ErrorMessage ?? response.ErrorException?.Message ?? "Error desconocido en la petición.");

            return Json(new { detalle = "", result = jResult });
        }
    }
}
