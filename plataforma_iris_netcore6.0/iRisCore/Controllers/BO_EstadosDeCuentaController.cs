using iRisLog;
using iRisModel;
using System.Security.Claims;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Org.BouncyCastle.Asn1.Ocsp;
using Newtonsoft.Json;
using RestSharp;
using RestSharp.Authenticators;
using iRisCore.Models;
using System.Security.Principal;
using System.Net.Http.Headers;
using iRisCore.Services;
using DocumentFormat.OpenXml.Drawing.Diagrams;
using System.Diagnostics;
using Newtonsoft.Json.Linq;
using Microsoft.DotNet.MSIdentity.Shared;

namespace iRisCore.Controllers
{
    public class BO_EstadosDeCuentaController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;

        public BO_EstadosDeCuentaController(IConfiguration config, TokenService tokenService)
        {
            _config = config;
            _tokenService = tokenService;
        }
        public IActionResult BO_EstadosDeCuenta()
        {
            return View();
        }

        [HttpGet]
        public async Task<IActionResult> GetParticipantes()
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLParticipantes"] + "/participantes/list";
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /participantes/list", Data = "" };

            try
            {

                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Get);

                if (!response.IsSuccessStatusCode)
                {
                    logiRis.Add($"Usuario: {returnUser} - Error al obtener participantes: {response.StatusCode}");
                    return Json(new { codigo = -1, mensaje = "Error al obtener los participantes", data = "" });
                }

                var responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData?.EiyuResponseCode == "OK")
                {
                    var participantes = responseData.Content.ToObject<List<MyDataModel>>();
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = participantes };
                }

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                logiRis.Add($"Usuario: {returnUser} - Error en GetParticipantes: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }
        [HttpPost]
        public async Task<IActionResult> GetMeses(MesEstadosDeCuentaModel mes)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLEstadosDeCuenta"] + "/mes";
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /mes", Data = "" };

            try
            {

                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, mes);


                if (!response.IsSuccessStatusCode)
                {
                    logiRis.Add($"Usuario: {returnUser} - Error al obtener los meses de los estados de cuenta: {response.StatusCode}");
                    return Json(new { codigo = -1, mensaje = "Error al obtener los meses de los estados de cuenta", data = "" });
                }

                var responseData = JsonConvert.DeserializeObject<MyDataMesResponse>(response.Content);

                if (responseData?.EiyuResponseCode == "OK")
                {
                    //// Obtener el objeto "content" como un diccionario
                    var contentDict = responseData.Content.ToObject<Dictionary<string, string>>();

                    // Mapear a la clase ListaMesesEstadosDeCuentaModel
                    var mesesModel = new ListaMesesEstadosDeCuentaModel
                    {
                        Enero = contentDict.GetValueOrDefault("01"),
                        Febrero = contentDict.GetValueOrDefault("02"),
                        Marzo = contentDict.GetValueOrDefault("03"),
                        Abril = contentDict.GetValueOrDefault("04"),
                        Mayo = contentDict.GetValueOrDefault("05"),
                        Junio = contentDict.GetValueOrDefault("06"),
                        Julio = contentDict.GetValueOrDefault("07"),
                        Agosto = contentDict.GetValueOrDefault("08"),
                        Septiembre = contentDict.GetValueOrDefault("09"),
                        Octubre = contentDict.GetValueOrDefault("10"),
                        Noviembre = contentDict.GetValueOrDefault("11"),
                        Diciembre = contentDict.GetValueOrDefault("12")
                    };
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = mesesModel };
                }

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                logiRis.Add($"Usuario: {returnUser} - Error en GetMeses: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<ActionResult> GetAnioCreacionParticipante(IdPBluEstadosDeCuentaModel idPblu)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLEstadosDeCuenta"] + "/anio-creacion";
            Debug.WriteLine(url);
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /anio-creacion", Data = "" };

            try
            {

                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, idPblu);
                // dynamic datos = JObject.Parse(response.Content);


                if (!response.IsSuccessStatusCode)
                {
                    logiRis.Add($"Usuario: {returnUser} - Error al obtener los años de los estados de cuenta: {response.StatusCode}");
                    return Json(new { codigo = -1, mensaje = "Error al obtener los años de los estados de cuenta", data = "" });
                }

                var responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData?.EiyuResponseCode == "EMPTY")
                {
                    var res = responseData.Descripcion;
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = res, Data = Json(null) };
                    return Json(new { detalle = "", result = jResult });
                }
                if (responseData?.EiyuResponseCode == "OK")
                {
                    var anios = responseData.Content.anio.ToObject<List<Object>>();
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = anios };
                }
                Debug.WriteLine(response);

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                Debug.WriteLine(ex);

                logiRis.Add($"Usuario: {returnUser} - Error en GetAnioCreacionParticipante: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }
        [HttpPost]
        public async Task<IActionResult> GetDescargaPDF([FromBody] List<ExistePDFModel> data)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLEstadosDeCuenta"] + "/download-zip";
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /download-zip", Data = "" };

            try
            {
                var response = await _tokenService.SendRequestWithToken(
            returnUser, url, RestSharp.Method.Post, JsonConvert.SerializeObject(data)
        );

                if (!response.IsSuccessStatusCode)
                {
                    return BadRequest("Error al descargar el archivo.");
                }

                var fileBytes = response.RawBytes; // Capturar el archivo como bytes
                if (fileBytes == null || fileBytes.Length == 0)
                {
                    return BadRequest("Archivo ZIP vacío o no encontrado.");
                }

                // Devolver el archivo ZIP
                return File(fileBytes, "application/zip", "descarga.zip");

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                logiRis.Add($"Usuario: {returnUser} - Error en GetDescargaPDF: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }
        [HttpPost]
        public async Task<IActionResult> ExistePDF(ExistePDFModel res)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLEstadosDeCuenta"] + "/existe_pdf";
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /existe_pdf", Data = "" };

            try
            {
                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, res);

                if (!response.IsSuccessStatusCode)
                {
                    logiRis.Add($"Usuario: {returnUser} - Error al validar PDF: {response.StatusCode}");
                    return Json(new { codigo = -1, mensaje = "Error al validar PDF", data = "" });
                }

                var responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData?.EiyuResponseCode == "OK")
                {
                    var pdf = responseData.Content;
                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = pdf };
                }

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                logiRis.Add($"Usuario: {returnUser} - Error en ExistePDF: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }

        [HttpPost]
        public async Task<IActionResult> VisualizarPDF(ExistePDFModel res)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var url = _config["EndPointsCore:apiURLEstadosDeCuenta"] + "/visualizar_pdf";
            var jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "Error en /visualizar_pdf", Data = "" };

            try
            {
                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, res);

                if (!response.IsSuccessStatusCode)
                {
                    logiRis.Add($"Usuario: {returnUser} - Error al visualizar PDF: {response.StatusCode}");
                    return Json(new { codigo = -1, mensaje = "Error al visualizar PDF", data = "" });
                }


                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = response };

                LogRequestResponse(returnUser, url, response);
            }
            catch (Exception ex)
            {
                logiRis.Add($"Usuario: {returnUser} - Error en VisualizarPDF: {ex.Message}");
                return Json(null);
            }

            return Json(new { detalle = "", result = jResult });
        }
        private void LogRequestResponse(string user, string url, RestResponse response)
        {
            try
            {
                var logEntry = new
                {
                    Usuario = user,
                    Url = url,
                    StatusCode = response.StatusCode,
                    Response = response.Content
                };
                logiRis.Add(JsonConvert.SerializeObject(logEntry));
            }
            catch (Exception ex)
            {
                logiRis.Add($"Error al registrar log: {ex.Message}");
            }
        }
    }
}
