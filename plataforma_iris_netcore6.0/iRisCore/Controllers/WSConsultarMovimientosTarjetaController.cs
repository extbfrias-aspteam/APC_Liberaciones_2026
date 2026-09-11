using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Globalization;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using iRisCore.Session;
using Microsoft.AspNetCore.StaticFiles;
using Fingers10.ExcelExport.ActionResults;
using iRisCore.ExcelTemplate;
using DocumentFormat.OpenXml.Office.Word;

namespace iRisCore.Controllers
{
    [Authorize]
    public class WSConsultarMovimientosTarjetaController : Controller
    {

        private readonly IConfiguration _config;
        public WSConsultarMovimientosTarjetaController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        public IActionResult WSConsultarMovimientosTarjeta()
        {
            return View();
        }


        public async Task<IActionResult> GenerateExcel()
        {
            var objComplex = HttpContext.Session.GetObject<Datas>("movimientos");
            List<MovtoReport> oMovimientos = new List<MovtoReport>();
            foreach (Movtos item in objComplex.Movimientos)
            {
                oMovimientos.Add(new MovtoReport { 
                    Autorizacion = item.Autorizacion,
                    ClaveMovimiento = item.ClaveMovimiento,
                    ConceptoMovimiento = item.ConceptoMovimiento,
                    DescripcionComercio = item.DescripcionComercio,
                    DescripcionPais = item.DescripcionPais,
                    DescripcionPostOperacion = item.DescripcionPostOperacion,
                    EstatusPostOperacion = item.EstatusPostOperacion,
                    FechaMovimiento = item.FechaMovimiento,
                    IDMovimiento = item.IDMovimiento,
                    ImporteMovimiento = item.ImporteMovimiento,
                    ImporteOrigenMovimiento = item.ImporteOrigenMovimiento,
                    MCC = item.MCC,
                    MonedaMovimiento = item.MonedaMovimiento,
                    MonedaOrigenMovimiento= item.MonedaOrigenMovimiento,
                    NumeroAfiliacionComercio = item.NumeroAfiliacionComercio,
                    Observaciones = item.Observaciones,
                    PaisComercio = item.PaisComercio,
                    Referencia = item.Referencia,
                    SaldoDespuesMovimiento = item.SaldoDespuesMovimiento,
                    TipoMovimientoCargoAbono = item.TipoMovimientoCargoAbono
                }) ;
            }
            return new ExcelResult<MovtoReport>(oMovimientos, "sheet1", "RptMovimientos");
        }


        [HttpPost]
        public async Task<ActionResult> WSConsultarMovimientosTarjeta(ConsultaMovimientoTarjetaModel model, string tipoMedio)
        {
            ConsultarTarjetaResponse resultado = new ConsultarTarjetaResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.token = "";
                model.maxMovimientos = 0;
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

                if (tipoMedio == "T")
                {
                    model.tipoMedioAcceso = "";
                    model.medioAcceso = "";
                }
                else
                {
                    model.numeroTarjeta = "";
                }

                var URLCard = _config["EndPointsCore:apiURLCard"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP =  _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/consultar-movimientos-tarjeta", RestSharp.Method.Post);
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

                HttpContext.Session.SetObject("movimientos",da);

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
