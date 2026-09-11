using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using iRisCore.Services;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System.Security.Claims;
using RestSharp.Authenticators;
using RestSharp;
using iRisCore.Access;


namespace iRisCore.Controllers
{
    public class ConsultaDuplicadosController : Controller
    {
        private readonly IConfiguration _config;

        public ConsultaDuplicadosController(IConfiguration config, TokenService tokenService)
        {
            _config = config;
        }

        public IActionResult ConsultaDuplicados()
        {
            return View();
        }

        [HttpPost]
        public async Task<JsonResult> GetDuplicados(DuplicadosModel model)
        {
            List<Duplicados> operPendientes = new List<Duplicados>();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                var responseData = new DuplicadosResponse();
                var jResult = new ResponseDuplicados();
                var apiUrl = _config["EndPointsCore:apiDuplicados"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(apiUrl)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/rest/consultaDuplicado", Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);
                dynamic datos = JObject.Parse(response.Content);

                if (response.IsSuccessful)
                {
                    responseData = JsonConvert.DeserializeObject<DuplicadosResponse>(response.Content);

                    if (datos.codigo == "0")
                    {
                        if (responseData.data != null)
                        {
                            operPendientes = responseData.data.ToObject<List<Duplicados>>();
                        }

                        jResult = new ResponseDuplicados { codigo = 1, mensaje = responseData.mensaje, data = operPendientes };
                        return Json(new { detalle = "", result = jResult });
                    }
                    else
                    {
                        jResult = new ResponseDuplicados { codigo = 0, mensaje = responseData.mensaje, data = "" };
                        return Json(new { detalle = "", result = jResult });
                    }
                }
                else
                {
                    jResult = new ResponseDuplicados { codigo = -1, mensaje = response.ErrorException.Message, data = "" };
                    return Json(new { detalle = "", result = jResult });
                }
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);
            }
        }
    }
}
