using iRisCore.Services;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using DocumentFormat.OpenXml.Spreadsheet;
using iRisCore.Access;

namespace iRisCore.Controllers
{
    public class BO_MonitorAlertasController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        private readonly Permissions _permissions;

        public BO_MonitorAlertasController(IConfiguration Config, TokenService tokenService, Permissions permissions)
        {
            _config = Config;
            _tokenService = tokenService;
            _permissions = permissions;
        }
        public IActionResult BO_MonitorAlertas()
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            string plataforma = identity.FindFirst(ClaimTypes.Sid)?.Value;
            ViewBag.CanAuthorize = _permissions.ValidarPermiso(user, int.Parse(plataforma), "RES_ALE_MMP");
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
            var jResult = new AlertasResponseFr();

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


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> GetAlertas(AlertaRequest body)
        {
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/alertas-proceso";

            return await EjecutarPeticionAsync<List<Alertas>>(url, endpoint, Method.Post, body);
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> SetAlerta(ResolverAlertaRequest body)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/general/resolver-operacion-retenida";
            body.usuario = user;

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, body);
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> SetAlertaAdm(ResolverAlertaRequest body)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var url = _config["EndPointsCore:apiUrlFraude"];
            var endpoint = "/eiyu-prevencion/autorizar/resolver-operacion-retenida";
            body.usuario = user;

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, body);
        }

        private AlertasResponseFr CrearResultado(int codigo, string mensaje, object? data = null)
        {
            return new AlertasResponseFr
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
            var jResult = new AlertasResponseFr();

            if (!string.IsNullOrEmpty(response.Content))
            {
                try
                {
                    AlertasResponseWs responseData = JsonConvert.DeserializeObject<AlertasResponseWs>(response.Content);

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
