using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using iRisCore.Tools;

namespace iRisCore.Controllers
{
    public class RecargaMovilController : Controller
    {

        private readonly IConfiguration _config;
        public RecargaMovilController(IConfiguration Config)
        {
            _config = Config;
        }
        public IActionResult RecargaMovil()
        {
            List<ListadoCompanias> oCompania = Catalogos.GetCompanias(_config["EndPointsCore:apiURLBusquedasV2"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            ViewBag.Companias = oCompania;     

            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> RecargaMovil(RecargaMovilModel model)
        {
            ResponseRecargaMovilModel resultado = new ResponseRecargaMovilModel();
            try
            {

                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLCard = _config["EndPointsCore:apiURLBusquedasV2"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/recargaMovil", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                resultado.data = datos.data;

                if (resultado.codigo != 0)
                {
                    return Json(new { detalle = "", result = resultado });
                }

                List<ListadoRecargasMovilModel> da = JsonConvert.DeserializeObject<List<ListadoRecargasMovilModel>>(resultado.data);

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

                return Json(new { detalle = da, result = resultado });
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

    }
}
