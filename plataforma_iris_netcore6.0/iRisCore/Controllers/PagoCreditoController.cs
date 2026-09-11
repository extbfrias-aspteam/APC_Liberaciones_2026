using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;

namespace iRisCore.Controllers
{
    public class PagoCreditoController : Controller
    {
        private readonly IConfiguration _config;
        public PagoCreditoController(IConfiguration Config)
        {
            _config = Config;
        }

        public IActionResult PagoCredito()
        {
            var URLCard = _config["EndPointsCore:apiURLLogistica"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];
            CatalogoLogisticaModel model = Catalogos.GetCatalogoLogistica(URLCard, userASP, pwdASP);

            ViewBag.Sucursales = model.sucursales;
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> PagoCredito(EstadoMovimientoCredModel model)
        {
            ResponseEstadoMovimientoCredModel resultado = new ResponseEstadoMovimientoCredModel();
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

                var request = new RestRequest("/consultaEdoMovimientoCredito", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);               

                ResponseEstadoMovimientoCredModel da = JsonConvert.DeserializeObject<ResponseEstadoMovimientoCredModel>(response.Content);

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
