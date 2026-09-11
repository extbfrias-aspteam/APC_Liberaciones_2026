using Fingers10.ExcelExport.ActionResults;
using iRisCore.ExcelTemplate;
using iRisCore.Session;
using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using Microsoft.AspNetCore.Authorization;

namespace iRisCore.Controllers
{
    public class DashboardTarjetasController : Controller
    {
        private readonly IConfiguration _config;
        public DashboardTarjetasController(IConfiguration Config)
        {
            _config = Config;
        }

        [Authorize]
        [HttpGet]
        public IActionResult DashboardTarjetas()
        {
            try
            {
                var URLCard = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                List<EmpresaModel> lstEmpresa = Catalogos.GetEmpresa(URLCard, userASP, pwdASP);
                ViewBag.Empresas = lstEmpresa;
                return View();
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar al consultar la empresas - DashboardTarjetas" + ex.Message);
                return null;
            }
        }

        [Authorize]
        [HttpPost]
        public async Task<IActionResult> DashBoardTarjetas(int empresaId)
        {
            try
            {
                var URLCard = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                ListadoTarjetaResponse resultado = new ListadoTarjetaResponse();

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);


                var request = new RestRequest("/listadoTarjetas", RestSharp.Method.Get);
                request.AddHeader("Content-Type", "application/json");
                var body = @"";
                request.AddParameter("text/plain", body, ParameterType.RequestBody);
                request.AddParameter("empresaId", empresaId);

                var response = client.ExecuteGet(request);

                dynamic datos = JObject.Parse(response.Content);
                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                resultado.detalle = datos.data;
                List<ListadoTarjetaDetalle> da = JsonConvert.DeserializeObject<List<ListadoTarjetaDetalle>>(resultado.detalle);
                HttpContext.Session.SetObject("inventario", da);
                return Json(da);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al consultar el inventario de la empresa DashboardTarjetas/empresaId=" + empresaId.ToString() + ": " + ex.Message);
                return null;
            }
        }

        public async Task<IActionResult> GenerateExcel()
        {
            try
            {
                var objComplex = HttpContext.Session.GetObject<List<ListadoTarjetaDetalle>>("inventario");
                List<InventarioTarjeta> oInventario = new List<InventarioTarjeta>();
                foreach (var item in objComplex)
                {
                    oInventario.Add(new InventarioTarjeta
                    {
                        PAN = item.pan,
                        Estatus = item.estatus,
                        Manufactura = item.tipoTarjeta
                    });
                }
                return new ExcelResult<InventarioTarjeta>(oInventario, "sheet1", "RptInventario");
            }
            catch (Exception e)
            {
                logiRis.Add("Error al consultar al generar el excel del inventario de tarjetas: " + e.Message);
                return null;
            }
        }
    }
}
