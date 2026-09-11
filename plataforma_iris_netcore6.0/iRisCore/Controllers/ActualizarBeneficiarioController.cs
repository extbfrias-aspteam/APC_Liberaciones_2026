using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;

namespace iRisCore.Controllers
{
    public class ActualizarBeneficiarioController : Controller
    {
        private readonly IConfiguration _config;
        public ActualizarBeneficiarioController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        [Authorize]
        public IActionResult ActualizarBeneficiario()
        {
            List<ProductoModel> oProducto = Catalogos.GetProducto(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);

            ParentescoModel oParentesco = Catalogos.GetParentesco(_config["EndPointsCore:apiURLBusquedasV2"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]); 

            ViewBag.Productos = oProducto;
            ViewBag.Parentesco = oParentesco.data;
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> ConsultaTitular(QueryModel query)
        {
            ConsultarTarjetaResponse resultado = new ConsultarTarjetaResponse();
            try
            {

                QueryModel oBusqueda = new QueryModel();
                oBusqueda.claveProducto = query.claveProducto;
                switch (query.tipoBusqueda)
                {                   
                    case "ASP":
                        oBusqueda.cuentaASP = query.valorBusqueda;
                        break;
                    case "TEL":
                        oBusqueda.telefono = query.valorBusqueda;
                        break;
                }

                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLCard = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/cuentahabiente", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(oBusqueda);
                var response = await client.ExecutePostAsync(request);
                dynamic datos = JObject.Parse(response.Content);

                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                if (resultado.codigo != 0)
                {
                    return Json(resultado);
                }
                resultado.data = datos.data;

                ConsultaCuentaModel da = JsonConvert.DeserializeObject<ConsultaCuentaModel>(resultado.data);
                da.codigo = resultado.codigo;
                da.mensaje = resultado.mensaje;

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

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> ActualizarBeneficiario(ActualizaBeneficiarioModel model)
        {
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

                var request = new RestRequest("/actualiza-beneficiario", RestSharp.Method.Put);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePutAsync(request);             
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
