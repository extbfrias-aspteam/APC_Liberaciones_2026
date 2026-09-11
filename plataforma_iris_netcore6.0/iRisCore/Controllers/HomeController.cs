using iRisCore.Access;
using iRisCore.Models;
using iRisCore.Tools;
using iRisModel;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System.Configuration;
using System.Diagnostics;
using System.Net;
using System.Security.Claims;

namespace iRisCore.Controllers
{

    public class HomeController : Controller
    {

        private readonly IConfiguration _config;
        public HomeController(IConfiguration config)
        {
            _config = config;
        }

        [Authorize]
        public async Task<IActionResult> Index()
        {
            return View();
        }

        [Authorize]
        public async Task<IActionResult> SegundoFactAut()
        {
            return View();
        }

        [Authorize]
        [HttpPost]
        public async Task<IActionResult> DashBoardCuentas(string tipoProducto)
        {
            var idProducto = _config["Miscelanius:ProductoId"].Split("|");
            string valProducto = "";
            if (tipoProducto == "facil")
            {
                valProducto = idProducto[1];
            }
            else
            {
                valProducto = idProducto[0];
            }

            DashboardCuentasDetalle obj = Dashboards.GetDashboardCuentas(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"], valProducto);
            return Json(obj);
        }

        [Authorize]
        [HttpGet]
        public async Task<IActionResult> DashBoardTarjetas()
        {
            ListadoTarjetaResponse objTar = Dashboards.GetDashboardEstatusTarjeta(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"], _config["Miscelanius:EmpresaId"]);
            return Json(objTar);
        }

        [AllowAnonymous]
        public async Task<IActionResult> Salir()
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                if (identity != null && identity.IsAuthenticated)
                {
                    string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                    string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                    string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
                    string idApp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Sid)?.Value;
                    // Cierra sesión del lado de backend
                    await Authentication.LogOutASPAsync(returnUser, iduser, ipUser, _config["EndPointsCore:apiURLLogin"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"], idApp);
                }
                // Elimina los datos de la sesión
                HttpContext.Session.Clear();
                // Elimina la cookie de autenticación
                await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
                // Eliminar todas las cookies
                foreach (var cookie in Request.Cookies.Keys)
                    Response.Cookies.Delete(cookie);
            }
            catch (Exception e)
            {
                // Si algo sale mal, igual redirige al login
            }

            return RedirectToAction("Login", "Account");
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }

        [Authorize]
        [HttpGet]
        public IActionResult Ping()
        {
            // Tocar la sesión para mantenerla viva
            HttpContext.Session.SetString("Ping", DateTime.Now.ToString());

            // Responder con un código 200 OK para indicar que la sesión y la cookie están vivas
            return Ok();
        }
    }
}