using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Newtonsoft.Json.Linq;
using DocumentFormat.OpenXml.Vml.Office;
using DocumentFormat.OpenXml.Spreadsheet;

using System.Security.Policy;
using iRisCore.Services;
using iRisCore.Models;
using Org.BouncyCastle.Asn1.Ocsp;

namespace iRisCore.Controllers
{
    public class BO_SaldosB2B : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_SaldosB2B(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }

        public IActionResult Bo_Saldosb2b()
        {
            return View();
        }
        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetParticipantesSaldoPblu()
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/saldoPblu";
            string usuarioCreacion = GetUsuarioCreacion();
            var participanteSaldos = new ParticipanteSaldos();

            // Realiza la solicitud usando el token
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, participanteSaldos);

            // Verifica si la solicitud fue exitosa
            if (response.IsSuccessful)
            {
                // Deserializa directamente el contenido de la respuesta
                var participantes = JsonConvert.DeserializeObject<List<ParticipanteSaldos>>(response.Content);
                return Json(participantes);
            }
            else
            {
                return StatusCode((int)response.StatusCode, response.Content);
            }
        }


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ObtenerSaldos([FromBody] ConsultaRequest consulta)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/saldosPbluActual";
            string usuarioCreacion = GetUsuarioCreacion();
           

            var requestBody = new
            {

                pblu = 0,
                idUdn = (int?)null,
                fechaInicio = consulta.FechaInicio.ToString("yyyy-MM-dd"),
                fechaActual = consulta.FechaActual.ToString("yyyy-MM-dd")
            };
             var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
           // dynamic x = JObject.Parse(response.Content);

         
            if (response.IsSuccessful)
            {
                var participantesSaldos = JsonConvert.DeserializeObject<List<ParticipanteSaldo>>(response.Content);

                if (consulta.Descripcion != "Todos")
                {
                    int participanteId = int.Parse(consulta.Descripcion);
                    participantesSaldos = participantesSaldos
                        .Where(p => p.IdParticipante == participanteId)
                        .ToList();
                }

                return Json(participantesSaldos);
            }
            else
            {
                return StatusCode((int)response.StatusCode, response.Content);
            }
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ObtenerSaldosUdn([FromBody] ConsultaRequest consulta)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/participantes/saldosPbluUdn";
            string usuarioCreacion = GetUsuarioCreacion();


            var requestBody = new
            {
                idPblu = consulta.Descripcion,

                fechaInicio = consulta.FechaInicio.ToString("yyyy-MM-dd"),
                fechaActual = consulta.FechaActual.ToString("yyyy-MM-dd")
            };


            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
          //  dynamic x = JObject.Parse(response.Content);

            if (response.IsSuccessful)
            {
                var participantesSaldosUdn = JsonConvert.DeserializeObject<List<ParticipanteSaldoUdn>>(response.Content);
                return Json(participantesSaldosUdn);
            }
            else
            {
                return StatusCode((int)response.StatusCode, response.Content);
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
