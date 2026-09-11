using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;

namespace iRisCore.Controllers
{
    public class ResetearTokenDockController : Controller
    {
        private readonly IConfiguration _config;
        public ResetearTokenDockController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        [Authorize]
        public IActionResult ResetearTokenDock()
        {
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> ResetearToken()
        {
            try
            {
                RequestLoginDockModel model = new RequestLoginDockModel();

                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLCard = _config["EndPointsCore:apiURLCard"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                model.NombreUsuario = _config["Miscelanius:userDock"];
                model.Password = _config["Miscelanius:pwdDock"];


                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/logInDock", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                ResponseSimpleModel da = JsonConvert.DeserializeObject<ResponseSimpleModel>(response.Content);


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

                return Json(da);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);
            }
        }
    }
}
