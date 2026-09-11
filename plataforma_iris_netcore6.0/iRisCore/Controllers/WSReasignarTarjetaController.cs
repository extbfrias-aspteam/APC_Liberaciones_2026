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
    public class WSReasignarTarjetaController : Controller
    {
        private readonly IConfiguration _config;
        public WSReasignarTarjetaController(IConfiguration Config)
        {
            _config = Config;
        }
        public IActionResult WSReasignarTarjeta()
        {
            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<ActionResult> WSReasignarTarjeta(ReasignarTarjetaModel model)
        {
            ReasignarTarjetaResponse resultado = new ReasignarTarjetaResponse();
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

                var URLCard = _config["EndPointsCore:apiURLCard"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP =  _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/reasignar-tarjeta", RestSharp.Method.Post);
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
                    return Json(resultado);
                }
                dynamic da = JObject.Parse(resultado.data);
                resultado.tarjetaNueva = da.tarjetaNueva;
              
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

                return Json(resultado);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }
    }
}
