using iRisCore.Access;
using iRisCore.Services;
using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp;
using RestSharp.Authenticators;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;

using System.Security.Principal;
using System.Text.Json;
using System.Text;
using System.Security.Policy;
using DocumentFormat.OpenXml.EMMA;

namespace iRisCore.Controllers
{
    public class ListasNegrasController : Controller
    {

        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;

        public ListasNegrasController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
            // Inicializar los métodos estáticos con TokenService
            Authentication.Initialize(_config, _tokenService);
        }
        public IActionResult ListasNegras()
        {
            string ip = HttpContext.Connection.RemoteIpAddress?.ToString();

            string hostName = ip;
            try
            {
                var entry = System.Net.Dns.GetHostEntry(ip);
                hostName = entry.HostName;
            }
            catch
            {
                // Si no puede resolver el nombre, deja sólo la IP
            }
            ViewBag.NombreEquipo = hostName;
            return View();
        }
       
        [HttpPost]
       
        public async Task<ActionResult> LoginModal(string usuario, string contrasena, int vista)
        {
            var key = _config.GetValue<string>("Authentication:KeyAES256");
            var iv = _config.GetValue<string>("Authentication:ivAES256");
            var identity = (ClaimsIdentity)User.Identity;

            string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();

            string ClientIPAddr = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
            string aplicativo = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Sid)?.Value;

            DecryptAndEncrypt de = new DecryptAndEncrypt();
            string cifrar = de.Encrypt(contrasena, key, iv);
           // string? ClientIPAddr = HttpContext.Connection.RemoteIpAddress?.ToString();

            //-- Valida si existen sesiones activas del usuario a loguearse ---
            var oSesiones = SessionActive.GetSesionesActiva(
                _config.GetValue<string>("EndPointsCore:apiURLBusquedas"),
                _config.GetValue<string>("EndPointsCore:userASP"),
                _config.GetValue<string>("EndPointsCore:passASP"),
                Int32.Parse(aplicativo),
                usuario.ToUpper()
            );

            if (oSesiones != null && oSesiones.codigo == 0)
            {
                // Cierra sesiones activas
                SesionesModel objSess = new SesionesModel
                {
                    header = new RequestHeaderModel
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
                        ipHost = ipUser
                    },
                    idAplicativo = Int32.Parse(aplicativo),
                    usuario = usuario.ToUpper()
                };
                SessionActive.CerrarSesionesActivas(
                    _config.GetValue<string>("EndPointsCore:apiURLBusquedas"),
                    _config.GetValue<string>("EndPointsCore:userASP"),
                    _config.GetValue<string>("EndPointsCore:passASP"),
                    objSess
                );
            }


            var url = _config.GetValue<string>("EndPointsCore:apiURLLogin") + "/login";
            var oResponse = Authentication.LoginASP(usuario.ToUpper(), cifrar, ClientIPAddr, aplicativo, url);
             
            if ((oResponse == null) || (oResponse.Codigo != 0))
            {
                string message = oResponse?.Mensaje ?? "Error al autenticar usuario";
                logiRis.Add("Usuario:" + usuario + "- Fail " + message, "SEG");
                TempData["ErrorMsg"] = message;
                return Json(new { success = false, message = message, redirect = true });
            }

            logiRis.Add("Usuario:" + usuario + "- Success Authentication", "SEG");

            // Puedes actualizar tokens/session aquí si lo necesitas
            await RegistrarBitacoraAsync(usuario.ToUpper(), vista, "exitoso", ipUser);
            return Json(new
            {
                success = true,
                usuario = usuario.ToUpper()
            });
           
        }


        [HttpPost]
        public async Task<ActionResult> Listas([FromBody] ListasNegrasRequest model)
        {
            var resultado = new ListasNegrasResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.header = new RequestHeaderModelListasNegras
                {
                    idEmpresa = 2,
                    idResponsabilidad = 0,
                    usuarioClave = returnUser,
                    idUsuario = iduser,
                    idClaseCanalAtencion = 0,
                   idCanalAtencion = 1,
                    idPuntoAtencion = 0,
                   idUbicacion = 0,
                    idSucursal = 2,
                   idComisionista = 0,
                    idTransaccion = 780,
                    ipHost = ipUser
                };

                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/listas";
                string usuarioCreacion = GetUsuarioCreacion();


                //var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                //var userASP = _config["EndPointsCore:userASP"];
                //var pwdASP = _config["EndPointsCore:passASP"];

                // var options = new RestClientOptions(URLCard)
                // {
                // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                //};
                // var client = new RestClient(options);

                // var request = new RestRequest("/iris-listas-negras/listas", RestSharp.Method.Post);
                //  request.AddHeader("Content-Type", "application/json");
                //  request.RequestFormat = DataFormat.Json;
                // request.AddJsonBody(model);
                // var response = await client.ExecutePostAsync(request);

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, model);

                resultado = JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                await RegistrarBitacoraAsync(usuarioCreacion, 1, resultado.mensaje, ipUser);


                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                    
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = response.ErrorMessage ?? response.Content;
                }
                return Json(new
                {
                    success = resultado.codigo == 0,
                    code = resultado.codigo,
                    message = resultado.mensaje
                });
            }
            catch (System.Exception ex)
            {
                return Json(new
                {
                    success = false,
                    code = -1,
                    message = "Error procesando solicitud: " + ex.Message
                });
            }
        }


        [HttpGet]
        [Authorize]
        public async Task<ActionResult> ObtenerCatalogosListasNegras()
        {
            try
            {
                //var apiUrl = _config["EndPointsCore:apiUrlListasNegras"]; // Cambia a tu config
                // var userASP = _config["EndPointsCore:userASP"];
                //var pwdASP = _config["EndPointsCore:passASP"];

                // var options = new RestClientOptions(apiUrl);

                // var client = new RestClient(options);
                // var request = new RestRequest("/iris-listas-negras/catalogos", Method.Get);

                //var response = await client.ExecuteAsync(request);
                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/catalogos";
                string usuarioCreacion = GetUsuarioCreacion();


                var requestBody = new
                {
                                     
                };
                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);


                if (response.IsSuccessful)
                {
                    var catalogoResponse = Newtonsoft.Json.JsonConvert.DeserializeObject<CatalogoListasNegrasResponse>(response.Content);
                    return Json(new { success = true, catalogos = catalogoResponse.data });
                }
                else
                {
                    return Json(new { success = false, message = "Error consultando catálogo: " + (response.ErrorMessage ?? response.Content) });
                }
            }
            catch (Exception ex)
            {
                return Json(new { success = false, message = "Excepción: " + ex.Message });
            }
        }


        [HttpGet]
        [Authorize]
        public async Task<ActionResult> ObtenerListasNegras()
        {
            try
            {

                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/listas";
                string usuarioCreacion = GetUsuarioCreacion();


                var requestBody = new
                {

                };
                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);




                // var apiUrl = _config["EndPointsCore:apiUrlListasNegras"]; // tu config
                // var userASP = _config["EndPointsCore:userASP"];
                //var pwdASP = _config["EndPointsCore:passASP"];

                // var options = new RestClientOptions(apiUrl)
                // {
                // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                // };

                // var client = new RestClient(options);
                // var request = new RestRequest("/iris-listas-negras/listas", Method.Get);
                // var response = await client.ExecuteAsync(request);

                if (response.IsSuccessful)
                {
                    var listasResponse = Newtonsoft.Json.JsonConvert.DeserializeObject<ListaNegraResponse>(response.Content);
                    return Json(new { success = true, data = listasResponse.data });
                }
                else
                {
                    return Json(new { success = false, message = "Error consultando listas: " + (response.ErrorMessage ?? response.Content) });
                }
            }
            catch (Exception ex)
            {
                return Json(new { success = false, message = "Excepción: " + ex.Message });
            }
        }

        [HttpGet]
        public async Task<ActionResult> ValidarPermisoUsuario(string usuario)
        {
            try
            {

                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/usuarios/"+ usuario;
                string usuarioCreacion = GetUsuarioCreacion();


                // var apiUrl = _config["EndPointsCore:apiUrlListasNegras"]; // Cambia a tu config
                //  var userASP = _config["EndPointsCore:userASP"];
                //  var pwdASP = _config["EndPointsCore:passASP"];

                //   var options = new RestClientOptions(apiUrl)
                //  {
                //  Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                //  };
                //  var client = new RestClient(options);

                // El usuario ya viene como parámetro en la ruta
                // var request = new RestRequest($"/iris-listas-negras/usuarios/{usuario}", Method.Get);

                //  var response = await client.ExecuteAsync(request);
                var requestBody = new
                {

                };
                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);

                if (response.IsSuccessful)
                {
                    var permiso = Newtonsoft.Json.JsonConvert.DeserializeObject<PermisoUsuarioListasNegrasResponse>(response.Content);
                    return Json(new { success = true, tienePermiso = permiso.data?.tienePermiso ?? false, codigo = permiso.codigo, mensaje = permiso.mensaje });
                }
                else
                {
                    return Json(new { success = false, message = "Error consultando permiso usuario: " + (response.ErrorMessage ?? response.Content) });
                }
            }
            catch (Exception ex)
            {
                return Json(new { success = false, message = "Excepción: " + ex.Message });
            }
        }
        [HttpPatch]
        public async Task<ActionResult> BajaPorAutorizacion(int id, [FromBody] ListasNegrasBajaPatchRequest model)
        {
            var resultado = new ListasNegrasResponse();
            try
            {
                // Completa el header según tu lógica de sesión (aquí es ejemplo fijo)
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
                string idUsuario = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value ?? "0";

                model.header = new PatchHeader
                {
                    numCuenta = "",  // pon el valor real si lo tienes
                    idComisionista = 0,
                    idTransaccion = 0,
                    idEmpresa = 2,
                    idResponsabilidad = 0,
                    usuarioClave = returnUser,
                    idUsuario = int.Parse(idUsuario),
                    idClaseCanalAtencion = 0,
                    idCanalAtencion = 1,
                    idPuntoAtencion = 0,
                    idUbicacion = 0,
                    idSucursal = 1,
                    ipHost = ipUser,
                    latitud = 0,
                    longitud = 0,
                    vistaId = 1
                };


                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/listas/" + id;
                string usuarioCreacion = GetUsuarioCreacion();




                // var apiUrl = _config["EndPointsCore:apiUrlListasNegras"];
                //var userASP = _config["EndPointsCore:userASP"];
                // var pwdASP = _config["EndPointsCore:passASP"];
                // var options = new RestClientOptions(apiUrl)
                // {
                // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                //};
                //  var client = new RestClient(options);
                //var request = new RestRequest($"/iris-listas-negras/listas/{id}", Method.Patch);

                // request.AddHeader("Content-Type", "application/json");
                // request.AddJsonBody(model);

                //  var response = await client.ExecuteAsync(request);

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Patch, model);
                resultado = JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                await RegistrarBitacoraAsync(usuarioCreacion, 4, resultado.mensaje, ipUser);
                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = Newtonsoft.Json.JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = response.ErrorMessage ?? response.Content;
                }
                return Json(new
                {
                    success = resultado.codigo == 0,
                    code = resultado.codigo,
                    message = resultado.mensaje
                });
            }
            catch (Exception ex)
            {
                return Json(new { success = false, code = -1, message = "Error: " + ex.Message });
            }
        }

        [HttpPatch]
        public async Task<ActionResult> BajaAutorizadaCodigo(int id, [FromBody] ListasNegrasBajaAutorizadaRequest model)
        {
            var resultado = new ListasNegrasResponse();
            try
            {
                // Completa el header según tu lógica de sesión (aquí es ejemplo fijo)
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
                string idUsuario = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value ?? "0";

                model.header = new PatchHeader
                {
                    numCuenta = "",  // pon el valor real si lo tienes
                    idComisionista = 0,
                    idTransaccion = 0,
                    idEmpresa = 2,
                    idResponsabilidad = 0,
                    usuarioClave = returnUser,
                    idUsuario = int.Parse(idUsuario),
                    idClaseCanalAtencion = 0,
                    idCanalAtencion = 1,
                    idPuntoAtencion = 0,
                    idUbicacion = 0,
                    idSucursal = 1,
                    ipHost = ipUser,
                    latitud = 0,
                    longitud = 0,
                    vistaId = 1
                };

                var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
                var url = URLCard + "/iris-listas-negras/listas/" + id;
                string usuarioCreacion = GetUsuarioCreacion();

                // Aquí PATCH contra HTTP externo
                //var apiUrl = _config["EndPointsCore:apiUrlListasNegras"];
                //var userASP = _config["EndPointsCore:userASP"];
                //var pwdASP = _config["EndPointsCore:passASP"];
                // var options = new RestClientOptions(apiUrl)
                //{
                // Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                //};
                // var client = new RestClient(options);
                // var request = new RestRequest($"/iris-listas-negras/listas/{id}", Method.Patch);

                // request.AddHeader("Content-Type", "application/json");
                // request.AddJsonBody(model);

                // var response = await client.ExecuteAsync(request);
                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Patch, model);
                resultado = JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                await RegistrarBitacoraAsync(usuarioCreacion, 3, resultado.mensaje, ipUser);
                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = Newtonsoft.Json.JsonConvert.DeserializeObject<ListasNegrasResponse>(response.Content);
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = response.ErrorMessage ?? response.Content;
                }
                return Json(new
                {
                    success = resultado.codigo == 0,
                    code = resultado.codigo,
                    message = resultado.mensaje
                });
            }
            catch (Exception ex)
            {
                return Json(new { success = false, code = -1, message = "Error: " + ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> RegistrarBitacora([FromBody] BitacoraRequestLN model)
        {
            await RegistrarBitacoraAsync(model.usuario, model.accionId, model.resultado, model.ip);
            return Json(new { success = true, message = "Bitácora registrada" });
        }

       
        private async Task RegistrarBitacoraAsync(string usuario, int accionId, string resultado, string ip)
        {
            string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
            var URLCard = _config["EndPointsCore:apiUrlListasNegras"];
            var url = URLCard + "/bitacoras/acciones" ;
            string usuarioCreacion = GetUsuarioCreacion();
            using (var client = new HttpClient())
            {
                var payload = new
                {
                    usuario = usuarioCreacion,
                    accionId = accionId,
                    resultado = resultado,
                    ip = ipUser
                };
              

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, payload);
            }
        }

        private string GetUsuarioCreacion()
        {
            var claimsIdentity = User.Identity as ClaimsIdentity;
            var userClaim = claimsIdentity?.FindFirst(ClaimTypes.NameIdentifier);
            return userClaim?.Value ?? "UsuarioDesconocido"; // Valor por defecto en caso de no encontrar el claim
        }

    }
}
