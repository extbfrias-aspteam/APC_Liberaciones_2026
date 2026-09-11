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
using iRisCore.Tools;
using System.Security.Principal;

namespace iRisCore.Controllers
{
    [Authorize]
    public class ConsultaMAController : Controller
    {
        private readonly IConfiguration _config;
        public ConsultaMAController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        public IActionResult ConsultaMA()
        {
            List<ProductoModel> oProducto = Catalogos.GetProducto(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            ViewBag.Productos = oProducto;

            return View();
        }

        [HttpPost]
        public async Task<ActionResult> ConsultaMA(QueryModel query)
        {
            ConsultarTarjetaResponse resultado = new ConsultarTarjetaResponse();
            try
            {

                QueryModel oBusqueda = new QueryModel();
                oBusqueda.claveProducto = query.claveProducto;
                switch (query.tipoBusqueda)
                {
                    case "RFC":
                        oBusqueda.rfc = query.valorBusqueda;
                        break;
                    case "CURP":
                        oBusqueda.curp = query.valorBusqueda;
                        break;
                    case "ASP":
                        oBusqueda.cuentaASP = query.valorBusqueda;
                        break;
                    case "CLABE":
                        oBusqueda.cuentaCLABE = query.valorBusqueda;
                        break;
                    case "PAN":
                        oBusqueda.numeroTarjeta = query.valorBusqueda;
                        break;
                    case "Tel":
                        oBusqueda.telefono = query.valorBusqueda;
                        break;
                    case "correo":
                        oBusqueda.correo = query.valorBusqueda;
                        break;

                }

                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var URLCard = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/cuentahabiente", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(oBusqueda);
                var response = await client.ExecutePostAsync(request);
                dynamic datos = JObject.Parse(response.Content);

                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                if (resultado.codigo != 0)
                {
                    return Json(resultado);
                }
                resultado.data = datos.data;

                ConsultaCuentaModel da = JsonConvert.DeserializeObject<ConsultaCuentaModel>(resultado.data);
                da.codigo = resultado.codigo;
                da.mensaje = resultado.mensaje;

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


        [HttpPost]
        public async Task<ActionResult> ActualizaRFC(ActualizaRfcRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
            var responseData = new ActualizaCurpRfcResponse();
            var jResult = new Response();
            var apiUrl = _config["EndPointsCore:apiURLBusquedas"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];

            model.usuarioId = userId;
            model.ip = userIp;

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/actualizarSolicitante", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);
            var response = await client.ExecutePostAsync(request);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<ActualizaCurpRfcResponse>(response.Content);
                if (responseData.codigo == "0")
                {
                    jResult = new Response { Codigo = 1, Mensaje = responseData.mensaje, Data = responseData.data };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new Response { Codigo = 0, Mensaje = responseData.mensaje, Data = responseData.data };
                }
            }
            else
            {
                jResult = new Response { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

            try
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

                logiRis.Add("Usuario:" + returnUser + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog));
                logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response.Content);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }


        [HttpPost]
        public async Task<ActionResult> ActualizaCURP(ActualizaCurpRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
            var responseData = new ActualizaCurpRfcResponse();
            var jResult = new Response();
            var apiUrl = _config["EndPointsCore:apiURLBusquedas"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];


            model.usuarioId = userId;
            model.ip = userIp;

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/actualizarSolicitante", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);
            var response = await client.ExecutePostAsync(request);
            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<ActualizaCurpRfcResponse>(response.Content);
                if (responseData.codigo == "0")
                {
                    jResult = new Response { Codigo = 1, Mensaje = responseData.mensaje, Data = responseData.data };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new Response { Codigo = 0, Mensaje = responseData.mensaje, Data = responseData.data };
                }
            }
            else
            {
                jResult = new Response { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

            try
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

                logiRis.Add("Usuario:" + returnUser + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog));
                logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response.Content);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]

        public async Task<ActionResult> ActualizaTelefono(ActualizaTelefonoRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;

            var responseData = new ActualizaTelefonoResponse();
            var jResult = new Response();
            var apiUrl = _config["EndPointsCore:apiURLBusquedas"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];

            // Set values from claims
            model.usuarioId = int.Parse(userId);
            model.ip = userIp;

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/actualizarSolicitanteTelefono", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);

            try
            {
                var response = await client.ExecutePostAsync(request);

                if (response.IsSuccessful)
                {
                    responseData = JsonConvert.DeserializeObject<ActualizaTelefonoResponse>(response.Content);
                    jResult = new Response
                    {
                        Codigo = responseData.codigo == 0 ? 0 : 1,
                        Mensaje = responseData.mensaje,
                        Data = responseData.data
                    };
                }
                else
                {
                    jResult = new Response
                    {
                        Codigo = -1,
                        Mensaje = response.ErrorException?.Message ?? "Error en la solicitud",
                        Data = ""
                    };
                }

                // Logging
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
                    uri = client.BuildUri(request)
                };

                logiRis.Add($"Usuario:{returnUser}-Request:{request.Resource}{JsonConvert.SerializeObject(requestToLog)}");
                logiRis.Add($"Usuario:{returnUser}-Response:{request.Resource}{response.Content}");
            }
            catch (Exception ex)
            {
                jResult = new Response
                {
                    Codigo = -1,
                    Mensaje = ex.Message,
                    Data = ""
                };
                logiRis.Add($"Error al actualizar teléfono - Usuario:{returnUser} - Error:{ex.Message}");
            }

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<ActionResult> ActualizarCorreoSolicitante(ActualizarCorreoRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;

            var responseData = new ActualizarCorreoResponse();
            var jResult = new Response();
            var apiUrl = _config["EndPointsCore:apiURLBusquedas"]; // Asegúrate de configurar esta clave
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];

            // Asignar valores desde los claims
            model.usuarioId = int.Parse(userId);
            model.ip = userIp;

            var options = new RestClientOptions(apiUrl)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/actualizarSolicitanteCorreo", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);

            try
            {
                var response = await client.ExecutePostAsync(request);
                if (response.IsSuccessful)
                {
                    responseData = JsonConvert.DeserializeObject<ActualizarCorreoResponse>(response.Content);
                    if (responseData.codigo == 0) // Código 0 indica éxito en tu ejemplo
                    {
                        jResult = new Response
                        {
                            Codigo = 0,
                            Mensaje = responseData.mensaje,
                            Data = responseData.data
                        };
                        return Json(new { detalle = "", result = jResult });
                    }
                    else
                    {
                        jResult = new Response
                        {
                            Codigo = 1,
                            Mensaje = responseData.mensaje,
                            Data = responseData.data
                        };
                    }
                }
                else
                {
                    jResult = new Response
                    {
                        Codigo = -1,
                        Mensaje = response.ErrorException?.Message ?? "Error al procesar la solicitud",
                        Data = ""
                    };
                }

                // Registro en log
                var requestToLog = new
                {
                    recurso = request.Resource,
                    parametros = request.Parameters.Select(parameter => new
                    {
                        nombre = parameter.Name,
                        valor = parameter.Value,
                        tipo = parameter.Type.ToString()
                    }),
                    metodo = request.Method.ToString(),
                    uri = client.BuildUri(request),
                };

                logiRis.Add($"Usuario:{returnUser}-Solicitud:{request.Resource}{JsonConvert.SerializeObject(requestToLog)}");
                logiRis.Add($"Usuario:{returnUser}-Respuesta:{request.Resource}{response.Content}");
            }
            catch (Exception ex)
            {
                jResult = new Response
                {
                    Codigo = -1,
                    Mensaje = $"Error interno al actualizar el correo: {ex.Message}",
                    Data = ""
                };
                logiRis.Add($"Error al actualizar correo - Usuario:{returnUser} - Error:{ex.Message}");
            }

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<ActionResult> ActualizarDomicilioSolicitante([FromBody] ActualizarDomicilioRequest model)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string userId = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;
            string userIp = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;

            try
            {
                // Validación del modelo
                if (model == null)
                {
                    return BadRequest(new { codigo = -1, mensaje = "El modelo de datos es nulo" });
                }

                // Validación de campos requeridos
             //   if (string.IsNullOrEmpty(model.solicitanteId) || string.IsNullOrEmpty(model.domicilio) ))
                  //  string.IsNullOrEmpty(model.codigoPostal) || string.IsNullOrEmpty(model.coloniaNombre
              //  {
              //      return BadRequest(new { codigo = -1, mensaje = "Faltan campos requeridos en la solicitud" });
             //   }

                var apiUrl = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(apiUrl)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };

                var client = new RestClient(options);
                var request = new RestRequest("/actualizarSolicitanteDomicilio", Method.Post);
               
                request.AddJsonBody(new
                {
                    usuarioId = userId,
                    solicitanteId = model.solicitanteId,
                    domicilio = model.domicilio,

                    ip = userIp,
                    catDomicilio1 = model.catDomicilio1,
                    descripcionDomicilio1 = model.descripcionDomicilio1,

                    catDomicilio2 = model.catDomicilio2,
                    descripcionDomicilio2 = model.descripcionDomicilio2,

                    catDomicilio3 = model.catDomicilio3,
                    descripcionDomicilio3 = model.descripcionDomicilio3,

                    catDomicilio4 = model.catDomicilio4,
                    descripcionDomicilio4 = model.descripcionDomicilio4,

                    catDomicilio5 = model.catDomicilio5,
                    descripcionDomicilio5 = model.descripcionDomicilio5,

                    observaciones = model.observaciones,

                    coloniaId = model.coloniaId,

                    colonia = new
                    {
                        cp = model.colonia.cp,
                        colonia = model.colonia.colonia,
                        localidad = model.colonia.localidad,
                        municipio = model.colonia.municipio,
                        estado = model.colonia.estado
                    }
                });



                var response = await client.ExecutePostAsync(request);

                // Manejar respuesta del servicio externo
                if (!response.IsSuccessful)
                {
                    return StatusCode((int)response.StatusCode, new
                    {
                        codigo = -1,
                        mensaje = $"Error en servicio externo: {response.ErrorMessage}"
                    });
                }

                // Devolver directamente la respuesta del servicio externo
                return Content(response.Content, "application/json");
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    codigo = -1,
                    mensaje = $"Error interno: {ex.Message}"
                });
            }
        }
        [HttpPost]
        public async Task<ActionResult> ConsultarCodigoPostal([FromBody] ConsultarCPRequest model)
        {
            try
            {
                if (model == null || string.IsNullOrEmpty(model.cp))
                {
                    return Ok(new { codigo = 1, mensaje = "El código postal es requerido", data = "" });
                }

                var apiUrl = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];
                var options = new RestClientOptions(apiUrl)
                { Authenticator = new HttpBasicAuthenticator(userASP, pwdASP) };

                var client = new RestClient(options);
                var request = new RestRequest("/consultarCP", Method.Post);
                request.AddJsonBody(new { cp = model.cp });
                var response = await client.ExecutePostAsync(request);

                if (!response.IsSuccessful)
                {
                    return Ok(new { codigo = 1, mensaje = response.ErrorMessage ?? "Error en servicio externo", data = "" });
                }

                var responseData = JsonConvert.DeserializeObject<ConsultarCPResponse>(response.Content);

                if (responseData.codigo != 0 || string.IsNullOrEmpty(responseData.data))
                {
                    return Ok(new { codigo = responseData.codigo, mensaje = responseData.mensaje, data = "" });
                }

                var colonias = JsonConvert.DeserializeObject<List<ColoniaCP>>(responseData.data);

                if (colonias == null || colonias.Count == 0)
                {
                    return Ok(new { codigo = 1, mensaje = "No hay colonias para este CP", data = "" });
                }

                return Ok(new { codigo = responseData.codigo, mensaje = responseData.mensaje, data = colonias });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { codigo = -1, mensaje = $"Error interno: {ex.Message}", data = "" });
            }
        }

        [Authorize]
        [HttpGet]
        public async Task<ActionResult> ConsultaTipoDomicilio()
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                var URLSpei = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLSpei)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/domiciliosCatalog/getDomiciliosCatalog", Method.Get);
                request.AddHeader("Content-Type", "application/json");

                var response = await client.ExecuteAsync(request);

                if (!response.IsSuccessful)
                {
                    return Json(new
                    {
                        codigo = (int)response.StatusCode,
                        mensaje = response.ErrorMessage ?? "Error en la solicitud",
                        data = new List<ConsultaTipoDireccionDetalle>()
                    });
                }

                // Primero deserializar la respuesta completa
                var apiResponse = JsonConvert.DeserializeObject<ConsultaTipoDireccionResponse>(response.Content);

                // Luego deserializar solo el campo data que contiene el array
                var items = JsonConvert.DeserializeObject<List<ConsultaTipoDireccionDetalle>>(apiResponse.data);

                var result = new
                {
                    codigo = apiResponse.codigo,
                    mensaje = apiResponse.mensaje,
                    data = items
                };

                // Logging
                logiRis.Add($"Usuario:{returnUser}-Response:{request.Resource}{response.Content}");

                return Json(result);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al consultar tipos de dirección: " + ex.Message);
                return Json(new
                {
                    codigo = 500,
                    mensaje = "Error interno del servidor",
                    data = new List<ConsultaTipoDireccionDetalle>()
                });
            }
        }
        [Authorize]
        [HttpGet]
        public async Task<ActionResult> ConsultaDomicilioSolicitante(string solicitanteId)
        {
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                var URLSpei = _config["EndPointsCore:apiURLBusquedas"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLSpei)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest($"/getDomicilioSolicitante?solicitanteId={solicitanteId}", Method.Get);
                request.AddHeader("Content-Type", "application/json");

                var response = await client.ExecuteAsync(request);

                if (!response.IsSuccessful)
                {
                    return Json(new
                    {
                        codigo = (int)response.StatusCode,
                        mensaje = response.ErrorMessage ?? "Error en la solicitud",
                        data = (object)null
                    });
                }

                // Deserializar la respuesta inicial
                var apiResponse = JsonConvert.DeserializeObject<DomicilioResponseBase>(response.Content);

                // Verificar si el servicio respondió con error
                if (apiResponse.codigo != 0)
                {
                    return Json(new
                    {
                        codigo = apiResponse.codigo,
                        mensaje = apiResponse.mensaje,
                        data = (object)null
                    });
                }

                // Deserializar el contenido del mensaje
                var domicilioData = JsonConvert.DeserializeObject<DomicilioData>(apiResponse.mensaje);

                var result = new
                {
                    codigo = apiResponse.codigo,
                    mensaje = "Éxito",
                    data = new
                    {
                        domicilioData.usuarioId,
                        domicilioData.domicilio,
                        domicilioData.coloniaId,
                        domicilioData.observaciones,
                        domicilioData.catDomicilio1,
                        domicilioData.catDomicilio2,
                        domicilioData.catDomicilio3,
                        domicilioData.catDomicilio4,
                        domicilioData.catDomicilio5,
                        domicilioData.descripcionDomicilio1,
                        domicilioData.descripcionDomicilio2,
                        domicilioData.descripcionDomicilio3,
                        domicilioData.descripcionDomicilio4,
                        domicilioData.descripcionDomicilio5,
                        colonia = domicilioData.colonia
                    }
                };

                // Logging
                logiRis.Add($"Usuario:{returnUser}-ConsultaDomicilioSolicitante-Response:{request.Resource}{response.Content}");

                return Json(result);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al consultar domicilio del solicitante: " + ex.Message);
                return Json(new
                {
                    codigo = 500,
                    mensaje = "Error interno del servidor",
                    data = (object)null
                });
            }
        }

    }
}
