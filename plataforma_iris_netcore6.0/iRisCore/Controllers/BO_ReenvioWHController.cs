using iRisLog;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using iRisModel;
using DocumentFormat.OpenXml.EMMA;
using iRisCore.Models;
using Org.BouncyCastle.Asn1.Ocsp;
using iRisCore.Access;
using DocumentFormat.OpenXml.Spreadsheet;
using iRisCore.Services;
using System.Security.Policy;

namespace iRisCore.Controllers
{
    public class BO_ReenvioWHController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_ReenvioWHController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }
        public IActionResult BO_ReenvioWH()
        {
            return View();
        }

        [HttpPost]
        public async Task<JsonResult> GetListWH(NotificacionesWHRequest requestWH)
        {
            List<NotificacionesWH> notificacionesWH = new List<NotificacionesWH>();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new NotificacionesWHResponse();
            var jResult = new ResponseWH();
            //  var apiUrl = _config["EndPointsCore:apiURLNotificaciones"];
            //  var userASP = _config["EndPointsCore:userASP"];
            //   var pwdASP = _config["EndPointsCore:passASP"];

            //  var options = new RestClientOptions(apiUrl)
            // {
            //  Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            //  };
            //  var client = new RestClient(options);


            var firma = Authentication.GetSign(_config.GetValue<string>("EndPointsCore:apiURLSign"), returnUser, JsonConvert.SerializeObject(requestWH));


            //  var request = new RestRequest("/notificacionWH/consultaNotificacion", Method.Post);
            // request.AddHeader("Content-Type", "application/json");
            //  request.AddHeader("signature", firma);
            // request.RequestFormat = DataFormat.Json;
            //request.AddJsonBody(requestWH);




             var URLCard = _config["EndPointsCore:apiURLNotificaciones"];
            var url = URLCard + "/notificacionWH/consultaNotificacion";
             string usuarioCreacion = GetUsuarioCreacion();

             var response = await _tokenService.SendRequestWithTokenFirma(firma,usuarioCreacion, url, RestSharp.Method.Post, requestWH);


            //  var response = await client.ExecutePostAsync(request);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<NotificacionesWHResponse>(response.Content);

                if (responseData.EiyuResponseCode == "OK")
                {
                    notificacionesWH = responseData.Content.ToObject<List<NotificacionesWH>>();

                    jResult = new ResponseWH { Codigo = 1, Mensaje = responseData.Descripcion, Data = notificacionesWH };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseWH { Codigo = 0, Mensaje = responseData.Descripcion, Data = "" };
                }
            }
            else
            {
                jResult = new ResponseWH { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

          
            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<JsonResult> ReenviarWH([FromBody] ReenvioWHRequest[] requestWH)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            //var responseData = new ReenvioWHResponse();

            var jResult = new ResponseWH();
            var apiUrl = _config["EndPointsCore:apiURLParticipantes"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var results = new List<ReenvioWHResponseItem>();
            jResult = new ResponseWH { Codigo = 1, Mensaje = "Todas las solicitudes de reenvío se enviaron correctamente", Data = "" };

            foreach (var requestItem in requestWH)
            {
                   
                var firma = Authentication.GetSign(_config.GetValue<string>("EndPointsCore:apiURLSign"), returnUser, JsonConvert.SerializeObject(requestItem));


                //var request = new RestRequest("/participantes/notificar/cambio-estado", Method.Post);
                //request.AddHeader("Content-Type", "application/json");
                //request.AddHeader("signature", firma);
                //request.RequestFormat = DataFormat.Json;
                //request.AddJsonBody(requestItem);

                //var response = await client.ExecutePostAsync(request);


                var URLCard = _config["EndPointsCore:apiURLParticipantes"];
                var url = URLCard + "/participantes/notificar/cambio-estado";
                string usuarioCreacion = GetUsuarioCreacion();

                var response = await _tokenService.SendRequestWithTokenFirma(firma, usuarioCreacion, url, RestSharp.Method.Post, requestItem);





                var responseData = new ReenvioWHResponseItem
                {
                    idPblu = requestItem.idPblu,
                    cveRastreo = requestItem.cveRastreo,
                    tipoOperacion = requestItem.tipoOperacion
                };

                if (response.IsSuccessful)
                {
                    var deserializedResponse = JsonConvert.DeserializeObject<ReenvioWHResponse>(response.Content);

                    responseData.Response = deserializedResponse;
                }

                if (!response.IsSuccessful || responseData.Response.EiyuResponseCode == "ERROR")
                {
                    jResult.Codigo = 0;
                    jResult.Mensaje = "Una o más solicitudes de reenvío tuvieron errores.";
                    responseData.Response = new ReenvioWHResponse
                    {
                        EiyuResponseCode = "ERROR",
                        Content = response.ErrorException?.Message ?? response.Content ?? "Unknown error"
                    };
                }

                results.Add(responseData);


                try
                {
                    var requestToLog = new
                    {
                        url = url,
                        method = "GET",
                        usuario = usuarioCreacion,
                    };

                    logiRis.Add("Usuario:" + returnUser + "-Request:" + url + JsonConvert.SerializeObject(requestToLog));
                    logiRis.Add("Usuario:" + returnUser + "-Response:" + url + response.Content);
                }
                catch (Exception ex)
                {
                    logiRis.Add("Error al registrar en el log-" + ex.Message);
                    return Json(null);
                }
            }

            if (jResult.Codigo == 0)
            {
                jResult.Data = results;
            }

            return Json(new { detalle = "", result = jResult });
        }

        private string GetUsuarioCreacion()
        {
            var claimsIdentity = User.Identity as ClaimsIdentity;
            var userClaim = claimsIdentity?.FindFirst(ClaimTypes.NameIdentifier);
            return userClaim?.Value ?? "UsuarioDesconocido"; // Valor por defecto en caso de no encontrar el claim
        }
    }
}
