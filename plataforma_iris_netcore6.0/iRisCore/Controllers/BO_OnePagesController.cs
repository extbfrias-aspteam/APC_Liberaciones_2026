using iRisCore.Models;
using iRisCore.Services;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Org.BouncyCastle.Asn1.Ocsp;
using Org.BouncyCastle.Ocsp;
using RestSharp;
using System.Collections;
using System.Security.Claims;
using static ParticipanteFormulario16;

namespace iRisCore.Controllers
{
    public class BO_OnePagesController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_OnePagesController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }
        public IActionResult BO_OnePages()
        {
            return View();
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetParticipantes()
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/participantes";

            return await EjecutarPeticionAsync<List<OnePages>>(url, endpoint, Method.Get);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetOnePages(OnePageRequest requestOP)
        {
            if (requestOP.idParticipante == 0)
            {
                return await GetParticipantes();
            }

            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/participantes/id";

            return await EjecutarPeticionAsync<List<OnePages>>(url, endpoint, Method.Get, requestOP);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetConceptos()
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/listaConceptos_comerciales";

            return await EjecutarPeticionAsync<List<OnePagesConceptos>>(url, endpoint, Method.Get);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetConceptos(OnePagesConceptos conceptos)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/guardar_Conceptos_comerciales";

            return await EjecutarPeticionAsync<List<OnePagesConceptos>>(url, endpoint, Method.Post, conceptos);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetTiposCobro()
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/lista_tipo_cobro";

            return await EjecutarPeticionAsync<List<OnePagesTiposCobro>>(url, endpoint, Method.Get);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetCondiciones(OnePageRequest2 requestOP)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/lista_conceptos_comerciales_participante";

            return await EjecutarPeticionAsync<List<OnePagesCondicionesRes>>(url, endpoint, Method.Get, requestOP);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetCondicion(OnePagesCondicionesReq condicion)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/guardar_condiciones_comerciales";

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, condicion);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetTarifas(OnePagesTarifasGet req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/lista_bko_tarifas_comerciales_pblu_tarifa";

            return await EjecutarPeticionAsync<List<OnePagesTarifasRes>>(url, endpoint, Method.Get, req);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetTarifa(OnePagesTarifasSet req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/guardar_bko_tarifas_comerciales";

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, req);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetContactos(OnePageRequest2 req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/bkocontactos/" + req.idPblu;

            return await EjecutarPeticionAsync<List<OnePagesContactos>>(url, endpoint, Method.Get, req);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetContacto(OnePagesContactosSet req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/bkocontactos";

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, req);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetContratoProductos()
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/list_bko_contrato_producto";

            return await EjecutarPeticionAsync<List<OnePagesContratoProductos>>(url, endpoint, Method.Get);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetInformacionB2B(OnePagesInformacionB2BSet req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/Bko_informacion";

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, req);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> SetContratoB2B(OnePagesContratoB2BSet req)
        {
            var url = _config["EndPointsCore:apiURLOnePages"];
            var endpoint = "/onePages/Bko_contrato";

            return await EjecutarPeticionAsync<object>(url, endpoint, Method.Post, req);
        }

        private OnePagesResponseFR CrearResultado(int codigo, string mensaje, object? data = null)
        {
            return new OnePagesResponseFR
            {
                Codigo = codigo,
                Mensaje = mensaje,
                Data = data ?? ""
            };
        }

        private async Task<IActionResult> EjecutarPeticionAsync<T>(string baseUrl, string endpoint, Method metodo, object? body = null, IDictionary<string, string>? headers = null)
        {
            var identity = (ClaimsIdentity)User.Identity;
            string user = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            var response = await _tokenService.SendRequestWithToken(user, baseUrl + endpoint, metodo, body);
            var jResult = new OnePagesResponseFR();

            if (!string.IsNullOrEmpty(response.Content))
            {
                try
                {
                    OnePagesResponseWs responseData = JsonConvert.DeserializeObject<OnePagesResponseWs>(response.Content);

                    if (responseData?.responseCode == "OK")
                    {
                        // Si el tipo T es object, no intentamos deserializar
                        if (typeof(T) == typeof(object))
                            jResult = CrearResultado(1, responseData.descripcion, responseData.content);
                        else
                            try
                            {
                                var tipoT = typeof(T);
                                object? data;
                                bool esLista = tipoT.IsGenericType && tipoT.GetGenericTypeDefinition() == typeof(List<>);

                                if (typeof(T) == typeof(object))
                                    data = responseData.content;
                                else if (esLista && responseData.content is Newtonsoft.Json.Linq.JArray)
                                    data = ((JToken)responseData.content).ToObject<T>();
                                else if (esLista && responseData.content is Newtonsoft.Json.Linq.JObject)
                                {
                                    Type tipoElemento = tipoT.GetGenericArguments()[0];

                                    var objeto = responseData.content.ToObject(tipoElemento);
                                    var lista = (IList)Activator.CreateInstance(typeof(List<>).MakeGenericType(tipoElemento));
                                    lista.Add(objeto);
                                    data = lista;
                                }
                                else
                                {
                                    data = ((JToken)responseData.content).ToObject<T>();
                                }

                                jResult = CrearResultado(1, responseData.descripcion, data);
                            }
                            catch (Exception ex)
                            {
                                jResult = CrearResultado(-3, $"Error al convertir el contenido: {ex.Message}");
                            }
                    }
                    else
                        jResult = CrearResultado(0, responseData?.descripcion ?? "Error de negocio no especificado.");
                }
                catch (JsonException ex)
                {
                    jResult = CrearResultado(-2, $"Error al procesar la respuesta del servidor: {ex.Message}");
                }
            }
            else
                jResult = CrearResultado(-1, response.ErrorMessage ?? response.ErrorException?.Message ?? "Error desconocido en la petición.");

            return Json(new { detalle = "", result = jResult });
        }
    }
}
