using iRisLog;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using iRisCore.Services;

namespace iRisCore.Controllers
{
    public class BO_ConsultaOperacionesController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_ConsultaOperacionesController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }
        public IActionResult BO_ConsultaOperaciones()
        {
            return View();
        }

        [HttpPost]
        public async Task<JsonResult> GetOperaciones(BO_ConsultaOperacionesRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new BO_ConsultaOperacionesResponse();
            var jResult = new Response();
            // var apiUrl = _config["EndPointsCore:apiURLParticipantes"];
            //var userASP = _config["EndPointsCore:userASP"];
            //  var pwdASP = _config["EndPointsCore:passASP"];

            // var options = new RestClientOptions(apiUrl)
            // {
            // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            //};
            // var client = new RestClient(options);

            // var request = new RestRequest("/participantes/speiIn-speiOut", RestSharp.Method.Post);
            // request.AddHeader("Content-Type", "application/json");
            // request.RequestFormat = DataFormat.Json;
            // request.AddJsonBody(model);
            // var response = await client.ExecutePostAsync(request);


            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/speiIn-speiOut";
            string usuarioCreacion = GetUsuarioCreacion();

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, model);

            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<BO_ConsultaOperacionesResponse>(response.Content);
                if (responseData.EiyuResponseCode == "OK")
                {
                    jResult = new Response { Codigo = 1, Mensaje = responseData.Descripcion, Data = responseData.Content };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new Response { Codigo = 0, Mensaje = responseData.Descripcion, Data = responseData.Content };
                }
            }
            else
            {
                jResult = new Response { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
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
        private string GetUsuarioCreacion()
        {
            var claimsIdentity = User.Identity as ClaimsIdentity;
            var userClaim = claimsIdentity?.FindFirst(ClaimTypes.NameIdentifier);
            return userClaim?.Value ?? "UsuarioDesconocido"; // Valor por defecto en caso de no encontrar el claim
        }
    }
}
