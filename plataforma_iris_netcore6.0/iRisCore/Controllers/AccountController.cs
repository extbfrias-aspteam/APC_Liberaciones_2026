using DocumentFormat.OpenXml.EMMA;
using iRisCore.Access;
using iRisCore.Models;
using iRisCore.Services;
using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Configuration;
using System.Diagnostics;
using System.Security.Claims;
using System.Security.Principal;

namespace iRisCore.Controllers
{
    public class AccountController : Controller
    {
        private IConfiguration _configuration;
        private readonly TokenService _tokenService;

        public AccountController(IConfiguration iConfig, TokenService tokenService)
        {
            _configuration = iConfig;
            _tokenService = tokenService;
            // Inicializar los métodos estáticos con TokenService
            Authentication.Initialize(_configuration, _tokenService);
        }

        public IActionResult Login()
        {

            List<ModelPlataformas> oPlataforma = new List<ModelPlataformas>();

            var aplicativo = _configuration.GetValue<string>("Miscelanius:Plataformas");
            string[] plataformas = aplicativo.Split('|');

            oPlataforma.Add(new ModelPlataformas { IdPlataforma = plataformas[0], NombrePlataforma = plataformas[1] });
            oPlataforma.Add(new ModelPlataformas { IdPlataforma = plataformas[2], NombrePlataforma = plataformas[3] });

            ViewBag.Plataforma = oPlataforma;

            ClaimsPrincipal c = HttpContext.User;
            if (c.Identity != null)
            {
                if (c.Identity.IsAuthenticated)
                    return RedirectToAction("Index", "Home");
            }


            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Login(string usuario, string contrasena, string plataforma)
        {
            try
            {
                logiRis.Add("Usuario:" + usuario + "- Initial Proceess Log In", "SEG");

                if (!ModelState.IsValid)
                {
                    logiRis.Add("Usuario:" + usuario + "-Credenciales Inválidas. Consulte al administrador del sistema.", "SEG");
                    TempData["ErrorMsg"] = "Credenciales Inválidas. Consulte al administrador del sistema.";
                    return RedirectToAction("Login", "Account", null);
                }
                var key = _configuration.GetValue<string>("Authentication:KeyAES256");
                var iv = _configuration.GetValue<string>("Authentication:ivAES256");

                DecryptAndEncrypt de = new DecryptAndEncrypt();
                string cifrar = de.Encrypt(contrasena, key, iv);
                string? ClientIPAddr = HttpContext.Connection.RemoteIpAddress?.ToString();

                //ClientIPAddr = "192.168.151.123";







                HttpContext.Session.SetInt32("plataformaUsuario", Int32.Parse(plataforma));

                //var aplicativo = _configuration.GetValue<string>("Miscelanius:ApplicationID");
                var aplicativo = plataforma;

                //-- Valida si existen sesiones activas del usuario a loguearse ---
                var oSesiones = SessionActive.GetSesionesActiva(_configuration.GetValue<string>("EndPointsCore:apiURLBusquedas"), _configuration.GetValue<string>("EndPointsCore:userASP"), _configuration.GetValue<string>("EndPointsCore:passASP"), Int32.Parse(aplicativo), usuario.ToUpper());

                if (oSesiones != null)
                {
                    if (oSesiones.codigo == 0)  //-- Existen sesiones activas, se inicia el proceso para cerrarlas
                    {
                        SesionesModel objSess = new SesionesModel();

                        objSess.header = new RequestHeaderModel
                        {
                            idComisionista = 0,
                            idTransaccion = 0,
                            idEmpresa = 0,
                            idResponsabilidad = 0,
                            usuarioClave = usuario.ToUpper(),
                            idUsuario = 0,
                            idClaseCanalAtencion = 0,
                            idPuntoAtención = 0,
                            idCanalAtencion = 1,
                            idUbicacion = 0,
                            idSucursal = 1,
                            ipHost = ClientIPAddr
                        };
                        objSess.idAplicativo = Int32.Parse(aplicativo);
                        objSess.usuario = usuario.ToUpper();

                        SessionActive.CerrarSesionesActivas(_configuration.GetValue<string>("EndPointsCore:apiURLBusquedas"), _configuration.GetValue<string>("EndPointsCore:userASP"), _configuration.GetValue<string>("EndPointsCore:passASP"), objSess);

                    }
                }

                var url = _configuration.GetValue<string>("EndPointsCore:apiURLLogin") + "/login";
                var oResponse = Authentication.LoginASP(usuario.ToUpper(), cifrar, ClientIPAddr, aplicativo, url);

                if ((oResponse == null) || (oResponse.Codigo != 0))
                {
                    string message = oResponse.Mensaje;
                    logiRis.Add("Usuario:" + usuario + "- Fail " + message, "SEG");
                    TempData["ErrorMsg"] = message;
                    return RedirectToAction("Login", "Account", null);

                }
                logiRis.Add("Usuario:" + usuario + "- Success Authentication", "SEG");

                string iduser = oResponse.Data.IdUsuario;
                string numEmpleado = oResponse.Data.NumEmpleado;
                string token = oResponse.Data.Token;
                string refreshToken = oResponse.Data.RefreshToken;

                // Almacenar el token en la sesión
                HttpContext.Session.SetString("AccessToken", token);
                HttpContext.Session.SetString("RefreshToken", refreshToken);
                HttpContext.Session.SetString("Platform", plataforma);
                HttpContext.Session.SetString("IdUser", iduser);
                HttpContext.Session.SetString("NumEmploy", numEmpleado);
                HttpContext.Session.SetString("UserName", usuario);

                List<Claim> c = new List<Claim>()
                {
                    new Claim(ClaimTypes.NameIdentifier, usuario),
                    new Claim(ClaimTypes.UserData, iduser),
                    new Claim(ClaimTypes.Uri, ClientIPAddr),
                    new Claim(ClaimTypes.SerialNumber, numEmpleado),
                    new Claim(ClaimTypes.Sid , plataforma)
                };
                ClaimsIdentity ci = new(c, CookieAuthenticationDefaults.AuthenticationScheme);
                await HttpContext.SignInAsync(CookieAuthenticationDefaults.AuthenticationScheme, new ClaimsPrincipal(ci));
                if (plataforma == "59")
                {
                    logiRis.Add("Usuario:" + usuario + "- End Proceess Log In", "SEG");

                    //-- Valida 2fact activo 
                    var sendToken = Authentication.Verify2Fact(_configuration.GetValue<string>("EndPointsCore:apiURLSegWS"), usuario);
                    if (sendToken)
                    {
                        //-- Envía el token al usuario para terminar de loguearse ---
                        var smsToken = await Authentication.SendTokenSMSAsync(_configuration.GetValue<string>("EndPointsCore:apiURLTokenSMS"), usuario, contrasena, numEmpleado, iduser, ClientIPAddr, _configuration.GetValue<string>("EndPointsCore:userASP"), _configuration.GetValue<string>("EndPointsCore:passASP"));

                        if (smsToken == -1)
                        {
                            TempData["ErrorMsg"] = "Error al enviar el token. Consulte al administrador del sistema.";
                            await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);

                            foreach (var cookie in Request.Cookies.Keys)
                                Response.Cookies.Delete(cookie);
                        }
                        else
                            return RedirectToAction("SegundoFactAut", "Home");

                    }

                    return RedirectToAction("Login", "Account");
                }
                else
                {
                    return RedirectToAction("Index", "HomeBO");
                }
            }
            catch (Exception ex)
            {
                logiRis.Add("Usuario:" + usuario + "- Error:" + ex.Message, "SEG");
                return RedirectToAction("Login", "Account", null);
            }
        }

        [HttpPost]
        public async Task<IActionResult> VerifyTokenSMS(string tokenSMS)
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string usuario = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string numEmpleado = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.SerialNumber)?.Value;

                //Llama al endpoint que verifica que el token ingresado sea igual al token enviado
                var smsToken = await Authentication.VerifyTokenSMSAsync(_configuration.GetValue<string>("EndPointsCore:apiURLTokenSMS"), usuario, numEmpleado, tokenSMS);

                if (smsToken > 0)
                {
                    return RedirectToAction("Index", "Home");
                }
                else
                {
                    TempData["ErrorMsg"] = smsToken == -1 ? "Error al válidar token. Consulte al administrador del sistema." : "Token no válido.";
                    await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);

                    foreach (var cookie in Request.Cookies.Keys)
                    {
                        Response.Cookies.Delete(cookie);
                    }

                    return RedirectToAction("Login", "Account");
                }
            }
            catch (Exception ex)
            {
                logiRis.Add("Error:" + ex.Message, "SEG");
                return RedirectToAction("Login", "Account", null);
            }
        }

        [HttpGet]
        public async Task<IActionResult> ExpiredTokenSMS()
        {
            try
            {
                TempData["ErrorMsg"] = "El tiempo para ingresar el token ha expirado.";
                await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);

                foreach (var cookie in Request.Cookies.Keys)
                {
                    Response.Cookies.Delete(cookie);
                }

                return Json(new { success = true, redirectUrl = Url.Action("Login", "Account") });
            }
            catch (Exception ex)
            {
                logiRis.Add("Error:" + ex.Message, "SEG");
                return Json(new { success = false, redirectUrl = Url.Action("Login", "Account") });
            }
        }
    }
}

