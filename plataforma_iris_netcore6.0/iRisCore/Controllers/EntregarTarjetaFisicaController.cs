using iRisCore.Tools;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using iRisLog;
using Newtonsoft.Json;

namespace iRisCore.Controllers
{
    public class EntregarTarjetaFisicaController : Controller
    {
        private readonly IConfiguration _config;
        private readonly string URLLogistica = "";
        private readonly string userASP = "";
        private readonly string pwdASP = "";

        public EntregarTarjetaFisicaController(IConfiguration Config)
        {
            _config = Config;
            URLLogistica = _config["EndPointsCore:apiURLLogistica"];
            userASP = _config["EndPointsCore:userASP"];
            pwdASP = _config["EndPointsCore:passASP"];
        }

        [HttpGet]
        [Authorize]
        public IActionResult EntregarTarjetaFisica()
        {
            var URLCard = _config["EndPointsCore:apiURLLogistica"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];
            CatalogoLogisticaModel model = Catalogos.GetCatalogoLogistica(URLCard, userASP, pwdASP);
            ViewBag.Marcas = model.marcas;
            return View();
        }


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> EntregarSobreEnSucursal(LogisticaEntregaTarjetaModel model)
        {
            LogisticaModel objLogistica = new LogisticaModel();
            try
            {

                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.header = new RequestHeaderModel
                {
                    idComisionista = 0,
                    idTransaccion = 0,
                    idEmpresa = 0,
                    idResponsabilidad = 0,
                    usuarioClave = returnUser,
                    idUsuario = Int32.Parse(iduser),
                    idClaseCanalAtencion = 0,
                    idPuntoAtención = 0,
                    idCanalAtencion = 1,
                    idUbicacion = 0,
                    idSucursal = 1,
                    ipHost = ipUser
                };
                var idSucursal = HttpContext.Session.GetInt32("sucursalUsuario");
                model.sucursalId = idSucursal;
                model.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];


                var URLLogistica = _config["EndPointsCore:apiURLLogistica"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/vincularTarjetaFisica", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                objLogistica.codigo = datos.codigo;
                objLogistica.mensaje = datos.mensaje;
                objLogistica.data = datos.data;

                //if (objLogistica.codigo != 0)
                //{
                //    return Json(objLogistica);
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

                return Json(objLogistica);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }
    }
}
