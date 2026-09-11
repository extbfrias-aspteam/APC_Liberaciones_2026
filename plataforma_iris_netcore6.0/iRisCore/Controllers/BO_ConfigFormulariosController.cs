using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Configuration;
using iRisCore.Models;
using System.Security.Claims;
using iRisCore.Services;
using Newtonsoft.Json.Linq;
using System.Security.Policy;

namespace iRisCore.Controllers
{
    public class BO_ConfigFormulariosController : Controller


    {

        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;
        public BO_ConfigFormulariosController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;
        }
  
        [HttpGet]
        [Authorize]
        public async Task<IActionResult> BO_ConfigFormularios()
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];

            var url = URLCard + "/firmantes";
        
            string usuarioCreacion = GetUsuarioCreacion();
            var firmanteResponse1 = new Firmante1();

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, firmanteResponse1);
                  //  dynamic x = JObject.Parse(response.Content);
               if (response.IsSuccessful)
                {
                var firmantesResponse = JsonConvert.DeserializeObject<FirmanteResponse1>(response.Content);
                var firmantes = firmantesResponse.Content;
                return View(firmantes);
               }
               else
               {
                    return View(new List<Firmante1>());

               }


        

        }
        [HttpGet]
        [Authorize]
        public async Task<IActionResult> ObtenerFirmantes()
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/firmantes";
            string usuarioCreacion = GetUsuarioCreacion();
            var firmanteResponse1 = new Firmante1();

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, firmanteResponse1);
          //  dynamic x = JObject.Parse(response.Content);

            if (response.IsSuccessful)
            {
                var firmantesResponse = JsonConvert.DeserializeObject<FirmanteResponse1>(response.Content);
                var firmantes = firmantesResponse.Content;

                // Retorna los firmantes en formato JSON para que DataTables pueda usarlo
                return Json(new { data = firmantes });
            }
            else
            {
                return Json(new { data = new List<Firmante1>() });
            }
        }


        [HttpPut]
        [Authorize]
        public async Task<IActionResult> ActualizarFirmante([FromBody] FirmanteUpdateRequest request)
        {
           

            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/firmantes";
            string usuarioCreacion = GetUsuarioCreacion();       
                       
            var requestBody = new
            {
                idFirmante = request.IdFirmante,
                nombreFirmante = request.NombreFirmante,
                usuario = usuarioCreacion,
                cargoPuesto = request.Puesto,
                signatario = request.Sigmatario
            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Put, requestBody);
            dynamic x = JObject.Parse(response.Content);
                   

            if (response.IsSuccessful)
            {
                return Json(new { success = true, message = "Firmante actualizado correctamente." });
            }
            else
            {
                return Json(new { success = false, message = "Error en la solicitud: " + response.ErrorMessage });
            }
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> AgregarFirmante2([FromBody] FirmanteUpdateRequest request)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/firmantes";
            string usuarioCreacion = GetUsuarioCreacion();



            var requestBody = new
            {
             
                nombreFirmante = request.NombreFirmante,
                usuario = usuarioCreacion,
                cargoPuesto = request.Puesto,
                signatario = request.Sigmatario
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
            dynamic x = JObject.Parse(response.Content);



            if (response.IsSuccessful)
            {
                return Json(new { success = true, message = "Firmante agregado correctamente." });
            }
            else
            {
                return Json(new { success = false, message = "Error en la solicitud: " + response.ErrorMessage });
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
