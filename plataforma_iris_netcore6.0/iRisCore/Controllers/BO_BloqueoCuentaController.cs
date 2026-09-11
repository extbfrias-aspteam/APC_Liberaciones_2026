using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Globalization;
using System.Security.Claims;
using iRisCore.Models;
using DocumentFormat.OpenXml.Spreadsheet;
using System.Text.Json;
using System.IO;
using iRisCore.Services;
using DocumentFormat.OpenXml.Wordprocessing;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication;

namespace iRisCore.Controllers
{

    public class BO_BloqueoCuentaController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        private readonly IHttpContextAccessor _httpContextAccessor;

        public BO_BloqueoCuentaController(IConfiguration config, TokenService tokenService, IHttpContextAccessor httpContextAccessor)
        {
            _config = config;
            _tokenService = tokenService;
            _httpContextAccessor = httpContextAccessor;
        }

        public IActionResult BO_BloqueoCuenta()
        {
            return View();
        }

        [HttpPost]
        public async Task<ActionResult> BloqueoCuentaIndv(BloqueoCuentaIndModel model)
        {
            BloqueoCuentaIndResponse resultado = new BloqueoCuentaIndResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.causa = model.causa;
                model.estatusCuenta = model.estatusCuenta;
                model.usuarioCreacion = returnUser;

                var url = _config["EndPointsCore:apiURLParticipantes"];
                url = url + "/participantes/bloqueo-spei";

                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, model);
                dynamic datos = JObject.Parse(response.Content);

                if (datos.eiyuResponseCode == "OK")
                {
                    resultado.eiyuResponseCode = datos.eiyuResponseCode;
                    return Json(resultado);
                }
                else if (datos.eiyuResponseCode == "ERROR")
                {
                    resultado.eiyuResponseCode = datos.eiyuResponseCode;
                    resultado.mensaje = datos.content;
                    return Json(resultado);
                }
                else
                {
                    resultado.mensaje = "Error en el servicio";
                    return Json(resultado);
                }
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

        [HttpPost]
        public async Task<IActionResult> BloqueoCuentaMas(IList<IFormFile> files, string tipoSpei, int estatus)
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                string? filePath = "";

                string[] valor;
                try
                {


                    long size = files.Sum(f => f.Length);
                    foreach (var formFile in files)
                    {
                        if (formFile.Length > 0)
                        {
                            var path = Environment.CurrentDirectory + "/BloqueoMasSpei/";

                            if (!Directory.Exists(path))
                            {
                                Directory.CreateDirectory(path);
                            }
                            filePath = path + Path.GetFileName(formFile.FileName);

                            using (var stream = System.IO.File.Create(filePath))
                            {
                                await formFile.CopyToAsync(stream);
                            }
                        }
                    }

                    var jsonData = new
                    {
                        estatusCuenta = estatus,
                        tipoSpei = tipoSpei,
                        usuarioCreacion = returnUser
                    };

                    var jsonString = System.Text.Json.JsonSerializer.Serialize(jsonData);
                    var jsonFilePath = Path.Combine(Environment.CurrentDirectory, "data.json");
                    await System.IO.File.WriteAllTextAsync(jsonFilePath, jsonString);

                    try
                    {
                        //JWT
                        var apiUrl = _config["EndPointsCore:apiURLParticipantes"];
                        apiUrl = apiUrl + "/participantes/bloqueo-spei-masivo";
                        // Obtener el token de acceso de la sesión
                        var token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
                        var client = new RestClient(apiUrl);

                        // Crear la solicitud con el token de acceso en el encabezado Authorization
                        var request = new RestRequest(apiUrl, RestSharp.Method.Post);
                        if (!string.IsNullOrEmpty(token))
                        {
                            request.AddHeader("Authorization", $"Bearer {token}");
                        }

                        request.AlwaysMultipartFormData = true;

                        request.AddFile("archivo", filePath, "text/plain");
                        request.AddFile("request", jsonFilePath, "application/json");

                        // Enviar la solicitud
                        var response = await client.ExecuteAsync(request);

                        // Si la respuesta es 401 (token expirado), intentar renovar el token
                        if (response.StatusCode == System.Net.HttpStatusCode.Unauthorized)
                        {
                            var refreshToken = _httpContextAccessor.HttpContext.Session.GetString("RefreshToken");
                            var plataforma = _httpContextAccessor.HttpContext.Session.GetString("Platform");
                            var idUsuario = _httpContextAccessor.HttpContext.Session.GetString("IdUser");
                            var numempleado = _httpContextAccessor.HttpContext.Session.GetString("NumEmploy");
                            var usuario = _httpContextAccessor.HttpContext.Session.GetString("UserName");

                            RefreshTokenRequest renewTokenBody = new RefreshTokenRequest()
                            {
                                token = token,
                                refreshToken = refreshToken,
                                user = new UserToken()
                                {
                                    idAplicativo = plataforma,
                                    idUsuario = idUsuario,
                                    numEmpleado = numempleado,
                                    usuario = usuario
                                }
                            };
                            var refreshSuccess = await _tokenService.RenewToken(renewTokenBody);

                            if (refreshSuccess)
                            {
                                // Obtener el nuevo token de la sesión y reintentar la solicitud
                                token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
                                request.AddOrUpdateHeader("Authorization", $"Bearer {token}");
                                response = await client.ExecuteAsync(request);
                            }
                            else
                            {
                                // Si no se puede renovar el token, cerrar sesión y borrar cookies
                                await HandleTokenExpiration();
                            }
                        }

                        if (returnUser.Length > 0)
                        {
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

                            logiRis.Add("Usuario:" + returnUser + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                            logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response.Content, "SEG");
                        }

                        //request.AddParameter("Request", JsonConvert.SerializeObject(model));
                        //RestResponse response = await client.ExecuteAsync(request);
                        dynamic datos = JObject.Parse(response.Content);

                        string eiyuResponseCode = datos.eiyuResponseCode;
                        string mensaje = "Respuesta";

                        List<ClabesNoAceptadas> clabesNoAceptadas = datos.content.clabesNoAceptadas.ToObject<List<ClabesNoAceptadas>>();

                        return Ok(new { codigo = eiyuResponseCode, mensaje = mensaje, clabesNoAceptadas = clabesNoAceptadas });
                    }
                    finally
                    {
                        // Eliminar el archivo JSON
                        if (System.IO.File.Exists(jsonFilePath))
                        {
                            System.IO.File.Delete(jsonFilePath);
                        }
                        if (System.IO.File.Exists(filePath))
                        {
                            System.IO.File.Delete(filePath);
                        }
                    }
                }
                catch (IOException ex)
                {
                    logiRis.Add("Cargar archivo de clabes: Error en carga de archivo de clabes");
                    return Ok(new { codigo = "-1", mensaje = "Cargar archivo de clabes: Error al cargar el archivo"});
                }

            }
            catch (Exception ex)
            {
                logiRis.Add("Estructura incorrecta del archivo");
                return Ok(new { codigo = "-2", mensaje = "Estructura incorrecta del archivo" });
            }

        }

        // Método para cerrar la sesión y eliminar cookies cuando el token no se puede renovar
        private async Task HandleTokenExpiration()
        {
            // Cerrar la sesión de autenticación
            await _httpContextAccessor.HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);

            // Eliminar todas las cookies
            foreach (var cookieKey in _httpContextAccessor.HttpContext.Request.Cookies.Keys)
            {
                _httpContextAccessor.HttpContext.Response.Cookies.Delete(cookieKey);
            }

            // Redirigir al usuario a la página de login
            _httpContextAccessor.HttpContext.Response.Redirect("/Account/Login");
        }
    }
}
