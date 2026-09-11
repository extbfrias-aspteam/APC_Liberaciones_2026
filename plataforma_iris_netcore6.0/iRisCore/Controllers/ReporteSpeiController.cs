using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Newtonsoft.Json.Linq;

namespace iRisCore.Controllers
{
    public class ReporteSpeiController : Controller
    {

        private readonly IConfiguration _config;
        public ReporteSpeiController(IConfiguration Config)
        {
            _config = Config;
        }


        [HttpGet]
        [Authorize]
        public IActionResult ReporteSpei()
        {
            return View();
        }

        [Authorize]
        [HttpPost]
        public async Task<ActionResult> ReporteSpei(ConsultaSpeiModel model)
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLSpei = _config["EndPointsCore:apiURLSPEI"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLSpei)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/consultaSpei", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                ConsultaSpeiResponse result = JsonConvert.DeserializeObject<ConsultaSpeiResponse>(response.Content);


                if (result.codigo != 200)
                {
                    return Json(result);
                }

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
                    uri = client.BuildUri(request),
                };

                logiRis.Add("Usuario:" + returnUser + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog));
                logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response.Content);

                return Json(result);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }




        [Authorize]
        [HttpPost]
        public async Task<ActionResult> BalanceB2B(string udn)
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLSpei = _config["EndPointsCore:apiURLSaldoUDN"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLSpei)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/eyu_saldo_udn_es?udn=" + udn, RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody("");
                var response = await client.ExecutePostAsync(request);

                SaldoUDNModel result = JsonConvert.DeserializeObject<SaldoUDNModel>(response.Content);          

                //if (result.codigo != 200)
                //{
                //    return Json(result);
                //}

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
                    uri = client.BuildUri(request),
                };

                logiRis.Add("Usuario:" + returnUser + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog));
                logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response.Content);

                return Json(result);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetParticipantesSaldoPblu()
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var options = new RestClientOptions(URLCard);
            var client = new RestClient(options);
            var request = new RestRequest("/participantes/saldoPblu", RestSharp.Method.Get);
            var response = await client.ExecuteAsync(request);
            if (response.IsSuccessful)
            {
                var participantes = JsonConvert.DeserializeObject<List<ParticipanteSaldos>>(response.Content);
                return Json(participantes);
            }
            else
            {
                return StatusCode((int)response.StatusCode, response.Content);
            }
        }
    }
}
