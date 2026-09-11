using DocumentFormat.OpenXml.Spreadsheet;
using iRisCore.Tools;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using iRisLog;
using OfficeOpenXml;
using System.Text.Json;
using OfficeOpenXml.Style;

namespace iRisCore.Controllers
{
    public class DashboardCuentasController : Controller
    {
        private readonly IConfiguration _config;
        public DashboardCuentasController(IConfiguration Config)
        {
            _config = Config;
        }
        public IActionResult DashboardCuentas()
        {
            List<ProductoModel> oProducto = Catalogos.GetProducto(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            ViewBag.Productos = oProducto;

            return View();
        }

        [HttpPost]
        public async Task<ActionResult> DashboardCuentas(ConcentradoCuentasProducto model)
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

                ConcentradoCuentasProductoResponse resultado = new ConcentradoCuentasProductoResponse();


                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);


                var request = new RestRequest("/concentradoCuentasProducto", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                resultado.data = datos.data;
                DetalleConcentradoCuentasProducto da = JsonConvert.DeserializeObject<DetalleConcentradoCuentasProducto>(resultado.data);

                return Json(da);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);
            }
        }

        public void DownloadExcel()
        {
           

        }


    }
}
