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
    public class ConsultaPagosServiciosController : Controller
    {
        private readonly IConfiguration _config;
        public ConsultaPagosServiciosController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        [Authorize]
        public IActionResult ConsultaPagosServicios()
        {
            List<ListadoProveedoresPagoServicio> oProveedores = Catalogos.GetProveedores(_config["EndPointsCore:apiURLBusquedasV2"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            ViewBag.Proveedores = oProveedores.OrderBy(x=>x.proveedor).ToList();
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> ConsultaPagosServicios(ConsultaPagoServicioModel model, string tipoBusqueda, string valor)
        {
            ResponsePagoServicioModel resultado = new ResponsePagoServicioModel();
            try
            {

                if (tipoBusqueda == "numAut")
                {
                    model.numeroAutorizacion = valor;
                    model.numeroReferencia = null;
                }
                else {
                    model.numeroAutorizacion = null;
                    model.numeroReferencia = valor;
                }

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

                var request = new RestRequest("/consultaPagoServicios", RestSharp.Method.Post);
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

                List<ListadoPagoServicioModel> da = JsonConvert.DeserializeObject<List<ListadoPagoServicioModel>>(resultado.data);




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
