using iRisCore.Tools;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using iRisCore.Services;
using System.Security.Policy;
using DocumentFormat.OpenXml.EMMA;
using iRisCore.Models;

namespace iRisCore.Controllers
{
    public class GestionReferidosController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public GestionReferidosController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;

        }

        [HttpGet]
        [Authorize]
        public IActionResult GestionReferidos()
        {
            return View();
        }


        [HttpPost]
        [Authorize]
        public async Task<ActionResult> Agregar([FromBody] ReferenciadorRequest model)
        {
            ReferenciadorResponse resultado = new ReferenciadorResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
                model.header = new ReferenciadorHeader
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


                 var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                 var url = URLCard + "/gestion-referidos/referenciadores";
                string usuarioCreacion = GetUsuarioCreacion();

                 var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, model);

                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = JsonConvert.DeserializeObject<ReferenciadorResponse>(response.Content);
                }
                else
                {
                   
                    resultado.codigo = 1;
                    resultado.mensaje = $" {response.ErrorMessage ?? response.Content}";
                 
                }

                var requestToLog = new
                {
                    resource = url,
                    parameters = new[] {
        new { name = "model", value = model, type = "body" }
    },
                    method = "POST", // O RestSharp.Method.Post.ToString()
                    uri = url,
                    body = JsonConvert.SerializeObject(model)
                };

                return Json(resultado); 
            }
            catch (Exception ex)
            {
     
                resultado.codigo = 2;
                resultado.mensaje = "Ocurrió un error inesperado al procesar la solicitud.";
                return StatusCode(500, resultado); 
            }
        }

        [HttpPost]
        [Authorize]
        public async Task<JsonResult> GestionReferidosConsultaCuenta([FromBody] ReferenciadorBuscarCuenta model)
        {
            var respuesta = new ReferenciadorResponse();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
            string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            var viewModel = new AltaCodigoViewModel();


            try
            {


                var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                var url = URLCard + "/gestion-referidos/cuentas/"+model.cuenta;
                string usuarioCreacion = GetUsuarioCreacion();

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, viewModel);

                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    // Deserialize al modelo proporcionado, ajústalo según la respuesta de tu API externa si cambia
                    var result = JsonConvert.DeserializeObject<ReferenciadorResponseBuscarCuenta>(response.Content);

                    // Si la API externa responde como esperas con 'codigo', 'mensaje', y 'data':
                    if (result != null && result.data != null)
                    {
                        // Respondemos con el mismo formato para el JS
                        return Json(new
                        {
                            codigo = result.codigo,
                            mensaje = result.mensaje,
                            nombre = result.data.nombre,
                            direccion = result.data.direccion,
                            curp = result.data.curp
                        });
                    }
                    else
                    {
                        // Sin data, pero exitoso
                        return Json(new
                        {
                            codigo = 1,
                            mensaje = $" {result.mensaje }",
                            nombre = "",
                            direccion = "",
                            curp = ""
                        });
                    }
                }
                else
                {
                    return Json(new
                    {
                        codigo = 2,
                        mensaje = $" {response.ErrorMessage ?? response.Content}",
                        nombre = "",
                        direccion = "",
                        curp = ""
                    });
                }
            }
            catch (Exception ex)
            {
                return Json(new
                {
                    codigo = 1,
                    mensaje = "" + ex.Message,
                    nombre = "",
                    direccion = "",
                    curp = ""
                });
            }
        }


        
        [HttpGet]
        public async Task<ActionResult> ObtenerListasAltaCodigo()
        {
            var viewModel = new AltaCodigoViewModel();
            try
            {
               
                var URLCard = _config["EndPointsCore:apiUrlReferidos"];


                var url = URLCard + "/gestion-referidos/referenciadores";
                string usuarioCreacion = GetUsuarioCreacion();

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, viewModel);



                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    var refResponse = JsonConvert.DeserializeObject<ReferenciadorListResponse>(response.Content);
                    if (refResponse.codigo == 0 && refResponse.data != null)
                    {
                        viewModel.Referenciadores = refResponse.data;
                    }
                }



                var url2 = URLCard + "/gestion-referidos/productos";
          

                var responseProductos = await _tokenService.SendRequestWithToken(usuarioCreacion, url2, RestSharp.Method.Get, viewModel);

                if (responseProductos.IsSuccessful && !string.IsNullOrEmpty(responseProductos.Content))
                {
                    var prodResponse = JsonConvert.DeserializeObject<ProductoListResponse>(responseProductos.Content);
                    if (prodResponse.codigo == 0 && prodResponse.data != null)
                    {
                        viewModel.Productos = prodResponse.data;
                    }
                }
            }
            catch (Exception ex)
            {
                // Loguear el error
                // _logger.LogError(ex, "Error al obtener listas para Alta Código.");
                // Puedes devolver un JSON con un mensaje de error si no quieres que el UI se rompa
                return Json(new { success = false, message = "Error al cargar los datos: " + ex.Message });
            }
            return Json(viewModel); // Retorna el ViewModel con ambas listas
        }
        [HttpPost]
        [Authorize]
        public async Task<ActionResult> CrearCodigo([FromBody] AltaCodigoRequest model)
        {
            var resultado = new ReferenciadorResponse(); // Define esta clase según tu modelo (con int codigo, string mensaje, ...)

            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.header = new ReferenciadorHeader
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

                var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                var url = URLCard + "/gestion-referidos/codigos";
                string usuarioCreacion = GetUsuarioCreacion();
                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, model);

                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    try
                    {
                        resultado = JsonConvert.DeserializeObject<ReferenciadorResponse>(response.Content);
                    }
                    catch (Exception dex)
                    {
                        return Json(new ReferenciadorResponse
                        {
                            codigo = 1,
                            mensaje = " " + dex.Message + " || Json: " + response.Content
                        });
                    }
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = $" {(response.ErrorMessage ?? response.Content)}";
                }

                return Json(resultado); // SIEMPRE retorna el objeto resultado como JSON
            }
            catch (Exception ex)
            {
                // JAMÁS uses return StatusCode(500, resultado);
                return Json(new ReferenciadorResponse
                {
                    codigo = 1,
                    mensaje = "Error inesperado al crear el código: " + ex.Message
                });
            }
        }
        [HttpPost]
        [Authorize]
        public async Task<ActionResult> DesactivarReferenciador([FromBody] ReferenciadorDesactivarRequest model)
        {
            var resultado = new ReferenciadorDesactivarResponse();
           
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.header = new ReferenciadorHeader
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
                model.compensacion = "SI";
                // var apiBaseUrl = _config["EndPointsCore:apiUrlReferidos"];
                //  var userASP = _config["EndPointsCore:userASPRef"];
                //   var pwdASP = _config["EndPointsCore:passASPRef"];
                //   var options = new RestClientOptions(apiBaseUrl)
                //  {
                //      Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                //   };

                //  var client = new RestClient(options);
                // var request = new RestRequest("/referenciador/desactivar", RestSharp.Method.Post);
                // request.AddHeader("Content-Type", "application/json");
                // request.RequestFormat = DataFormat.Json;
                // request.AddJsonBody(model);

                // var response = await client.ExecutePostAsync(request);
                var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                var url = URLCard + "/gestion-referidos/referenciadores";
                string usuarioCreacion = GetUsuarioCreacion();

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Put, model);


                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = JsonConvert.DeserializeObject<ReferenciadorDesactivarResponse>(response.Content);
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = $" {response.ErrorMessage ?? response.Content}";
                }

                return Json(resultado);
            }
            catch (Exception ex)
            {
                resultado.codigo = 1;
                resultado.mensaje = "Ocurrió un error inesperado al procesar la solicitud.";
                return StatusCode(500, resultado);
            }
        }
        [HttpGet]
        public async Task<ActionResult> ObtenerCodigos()
        {
            try
            {
                var model = new ReferenciadorDesactivarRequest();
                var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                var url = URLCard + "/gestion-referidos/codigos";
                string usuarioCreacion = GetUsuarioCreacion();
                //var url2 = "http://localhost:3001/misejemplosGet";

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, model);

                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    try
                    {
                        string cleanJson = response.Content;

                        if (cleanJson.StartsWith("\"") && cleanJson.EndsWith("\""))
                        {
                            // Aquí se deserializa el string doblemente serializado
                            cleanJson = JsonConvert.DeserializeObject<string>(cleanJson);
                        }

                        var codeList = JsonConvert.DeserializeObject<CodigoListResponse>(cleanJson);

                        if (codeList != null && codeList.codigo == 0 && codeList.data != null)
                            return Json(new { success = true, codigos = codeList.data });
                        else
                            return Json(new { success = false, message = "", detalle = cleanJson });
                    }
                    catch (Exception exD)
                    {
                        return Json(new { success = false, message = " " + exD.Message, detalle = response.Content });
                    }
                }
                else
                {
                    return Json(new { success = false, message = " " + (response.ErrorMessage ?? response.Content) });
                }
            }
            catch (System.Exception ex)
            {
                return Json(new { success = false, message = " " + ex.Message });
            }
        }



        [HttpPost]
        [Authorize]
        public async Task<ActionResult> ModificarEstatusCodigo([FromBody] CodigoModificarEstatusRequest model)
        {
            var resultado = new CodigoModificarEstatusResponse();

            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
                string ipUser = HttpContext.Connection.RemoteIpAddress?.ToString();
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.header = new ReferenciadorHeader
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

                // var apiBaseUrl = _config["EndPointsCore:apiUrlReferidos"];
                // var userASP = _config["EndPointsCore:userASPRef"];
                // var pwdASP = _config["EndPointsCore:passASPRef"];
                // var options = new RestClientOptions(apiBaseUrl)
                // {
                //     Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                // };
                //var client = new RestClient(options);

                //var request = new RestRequest("/codigo/modificar-estatus", RestSharp.Method.Post);
                //request.AddHeader("Content-Type", "application/json");
                //request.RequestFormat = DataFormat.Json;
                //request.AddJsonBody(model);

                //var response = await client.ExecutePostAsync(request);

                var URLCard = _config["EndPointsCore:apiUrlReferidos"];
                var url = URLCard + "/gestion-referidos/codigos";
                string usuarioCreacion = GetUsuarioCreacion();

                var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Put, model);


                if (response.IsSuccessful && !string.IsNullOrEmpty(response.Content))
                {
                    resultado = JsonConvert.DeserializeObject<CodigoModificarEstatusResponse>(response.Content);
                }
                else
                {
                    resultado.codigo = 1;
                    resultado.mensaje = $" {response.ErrorMessage ?? response.Content}";
                }

                return Json(resultado);
            }
            catch (System.Exception ex)
            {
                resultado.codigo = 1;
                resultado.mensaje = " " + ex.Message;
                return StatusCode(500, resultado);
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

