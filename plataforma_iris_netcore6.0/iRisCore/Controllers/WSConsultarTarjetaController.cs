using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System.Globalization;
using System.Security.Claims;

namespace iRisCore.Controllers
{
    public class WSConsultarTarjetaController : Controller
    {
        private readonly IConfiguration _config;
        public WSConsultarTarjetaController(IConfiguration Config)
        {
            _config = Config;
        }

        [Authorize]
        [HttpGet]
        public IActionResult WSConsultarTarjeta()
        {            
            return View();
        }

        [Authorize]
        [HttpPost]
        public async Task<ActionResult> WSConsultarTarjeta(ConsultarTarjetaModel model)
        {
            ConsultarTarjetaResponse resultado = new ConsultarTarjetaResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;


                model.tipoTarjeta = model.tipoTarjeta;
                model.numeroTarjeta = model.numeroTarjeta;

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

                var request = new RestRequest("/consultar-tarjeta", RestSharp.Method.Post);
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

                Datas da = JsonConvert.DeserializeObject<Datas>(resultado.data);
                resultado.CuentaASP = da.Cuenta;
                resultado.Cacao = da.CuentaCacao;
                resultado.TipoTarjeta = da.TipoManufactura;
                resultado.NumeroTarjeta = da.Tarjeta;
                resultado.Nombre = da.Nombre + " " + da.PrimerApellido;
                resultado.CtaClabe = da.CLABE;
                resultado.Vigencia = da.FechaVigencia.ToString("d", CultureInfo.CreateSpecificCulture("en-US"));
                resultado.Estatus = da.DescripcionStatus;

                foreach (var item in da.SaldoActual)
                {
                    if (item.ClaveTipoCuenta.Trim() == "CCLC") {  //Cuenta de Autorización $$$ virtual
                        resultado.Saldo = string.Format("{0:C}", item.Saldo);
                    }
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
