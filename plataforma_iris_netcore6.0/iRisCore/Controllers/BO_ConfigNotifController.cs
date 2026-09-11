using DocumentFormat.OpenXml.EMMA;
using iRisCore.Services;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System.Collections.Generic;
using System.Dynamic;
using System.Net;
using System.Security.Claims;
using System.Security.Principal;

namespace iRisCore.Controllers
{
    public class BO_ConfigNotifController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_ConfigNotifController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }
        public IActionResult BO_ConfigNotif()
        {
            return View();
        }

        [HttpGet]
        public async Task<JsonResult> GetParticipantes()
        {
            List<MyDataModel> participantes = new List<MyDataModel>();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new MyDataResponse2();
            var jResult = new ResponseConfigNotif();
            // var apiUrl = _config["EndPointsCore:apiURLParticipantes"];
            //var userASP = _config["EndPointsCore:userASP"];
            // var pwdASP = _config["EndPointsCore:passASP"];

            // var options = new RestClientOptions(apiUrl)
            // {
            // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            //};
            // var client = new RestClient(options);

            // var request = new RestRequest("/participantes/list", Method.Get);
            // request.AddHeader("Content-Type", "application/json");
            //request.RequestFormat = DataFormat.Json;

            // var response = client.ExecuteGet(request);

            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/list";
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
          
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData.EiyuResponseCode == "OK")
                {
                    List<MyDataModel> participantes_ = responseData.Content.ToObject<List<MyDataModel>>();
                    participantes = participantes_.Where(d => d.activo).ToList();

                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = participantes };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "error en /participantes/list", Data = "" };
                }
            }
            else
            {
                jResult = new ResponseConfigNotif { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

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

            return Json(new { detalle = "", result = jResult });
        }

        [HttpGet]
        public async Task<JsonResult> GetUdnsByParticipante(int id)
        {
            List<Udns> udns = new List<Udns>();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new MyDataResponse2();
            var jResult = new ResponseConfigNotif();
            // var apiUrl = _config["EndPointsCore:apiURLParticipantes"];
            // var userASP = _config["EndPointsCore:userASP"];
            // var pwdASP = _config["EndPointsCore:passASP"];

            // var options = new RestClientOptions(apiUrl)
            //  {
            //  Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            // };
            //  var client = new RestClient(options);

            // var request = new RestRequest("/participantes/udn/" + id, Method.Get);
            //  request.AddHeader("Content-Type", "application/json");
            //  request.RequestFormat = DataFormat.Json;

            // var response = client.ExecuteGet(request);
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/udn/" + id;
            string usuarioCreacion = GetUsuarioCreacion();
            var requestBody = new
            {

            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData.EiyuResponseCode == "OK")
                {
                    List<Udns> udns_ = responseData.Content.ToObject<List<Udns>>();
                    udns = udns_.Where(d => d.Activo).ToList();

                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = responseData.Descripcion, Data = udns };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = responseData.Descripcion, Data = "" };
                }
            }
            else
            {
                jResult = new ResponseConfigNotif { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

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

            return Json(new { detalle = "", result = jResult });
        }

        [HttpGet]
        public async Task<JsonResult>  GetConfigNotif()
        {
            List<ConfigNotif> configNotif = new List<ConfigNotif>();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new MyDataResponse2();
            var jResult = new ResponseConfigNotif();
            // var apiUrl = _config["EndPointsCore:apiURLNotificaciones"];
            // var userASP = _config["EndPointsCore:userASP"];
            // var pwdASP = _config["EndPointsCore:passASP"];

            // var options = new RestClientOptions(apiUrl)
            //{
            //     Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            // };
            //  var client = new RestClient(options);

            //var request = new RestRequest("/notificacion/consultaNotificacion", Method.Get);
            // request.AddHeader("Content-Type", "application/json");
            //  request.RequestFormat = DataFormat.Json;

            //var response = client.ExecuteGet(request);
            var URLCard = _config["EndPointsCore:apiURLNotificaciones"];
            var url = URLCard + "/notificacion/consultaNotificacion";
            string usuarioCreacion = GetUsuarioCreacion();
            
            var requestBody = new
            {

              
            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                configNotif = responseData.Content.ToObject<List<ConfigNotif>>();
                foreach (var notif in configNotif)
                {
                    notif.FchInicial = QuitarHora(notif.FchInicial);
                    notif.FchFinal = QuitarHora(notif.FchFinal);
                }

                if (responseData.EiyuResponseCode == "OK")
                {
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = responseData.Descripcion, Data = configNotif };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = responseData.Descripcion, Data = configNotif };
                }
            }
            else
            {
                jResult = new ResponseConfigNotif { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

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

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<JsonResult> SetConfigNotifAsync(NewConfigNotif model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new MyDataResponse2();
            var jResult = new ResponseConfigNotif();
            //  var apiUrl = _config["EndPointsCore:apiURLNotificaciones"];
            //  var userASP = _config["EndPointsCore:userASP"];
            //  var pwdASP = _config["EndPointsCore:passASP"];

            //  var options = new RestClientOptions(apiUrl)
            //   {
            //   Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            // };
            //   var client = new RestClient(options);

            //   var request = new RestRequest("/notificacion/altaNotificacion", RestSharp.Method.Post);
            //  request.AddHeader("Content-Type", "application/json");
            //   request.RequestFormat = DataFormat.Json;
            //  request.AddJsonBody(model);
            //  var response = await client.ExecutePostAsync(request);
            var URLCard = _config["EndPointsCore:apiURLNotificaciones"];
            var url = URLCard + "/notificacion/altaNotificacion";
            string usuarioCreacion = GetUsuarioCreacion();

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, model);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);
                if (responseData.EiyuResponseCode == "OK")
                {
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = responseData.Descripcion, Data = responseData.Content };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = responseData.Descripcion, Data = responseData.Content };
                }
            }
            else
            {
                jResult = new ResponseConfigNotif { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

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
            return Json(new { detalle = "", result = jResult });
        }

        private static string QuitarHora(string fechaConHora)
        {
            DateTime fecha = DateTime.Parse(fechaConHora);

            string fechaSinHora = fecha.ToString("yyyy/MM/dd");

            return fechaSinHora;
        }
        private string GetUsuarioCreacion()
        {
            var claimsIdentity = User.Identity as ClaimsIdentity;
            var userClaim = claimsIdentity?.FindFirst(ClaimTypes.NameIdentifier);
            return userClaim?.Value ?? "UsuarioDesconocido"; // Valor por defecto en caso de no encontrar el claim
        }
    }
}
