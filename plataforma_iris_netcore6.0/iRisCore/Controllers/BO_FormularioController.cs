using iRisCore.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using RestSharp;
using System;
using System.Threading.Tasks;
using iText.Kernel.Pdf;
using iText.Layout;
using iText.Layout.Element;
using System.IO;
using iText.Layout.Properties;
using iText.IO.Image;
using iText.Kernel.Font;
using iText.IO.Font.Constants;
using iText.Layout.Borders;
using iText.Kernel.Events;
using System.Collections.Generic;
using System.Linq;
using iRisCore.Models.Interfaces;
using iRisCore.Models.Formularios;
using Microsoft.VisualBasic;
using static iRisCore.Controllers.BO_FormularioController;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System.Net.Http;
using Org.BouncyCastle.Asn1.Ocsp;
using iRisLog;
using iRisModel;
using RestSharp.Authenticators;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using iRisCore.Services;
using DocumentFormat.OpenXml.Drawing.Charts;
using static ParticipanteFormulario16;


namespace iRisCore.Controllers
{
    public class BO_FormularioController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;


        public BO_FormularioController(IConfiguration Config, TokenService tokenService)
        {
            _config = Config;
            _tokenService = tokenService;

        }
        public IActionResult Bo_Formulario(string opcion)
        {
            ViewBag.OpcionSeleccionada = opcion;

          

            return View(); 
        }
        #region formuario1
        ///filtro formulario1
        [HttpPost]
        [Authorize]
        public async Task<IActionResult> parIndirectosFiltro([FromBody] ConsultaRequestID consulta)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = URLCard + "/contratosPI/filtro";
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
                fechaInicio = consulta.FechaInicio.ToString("dd-MM-yyyy"),
                fechaFinal = consulta.FechaActual.ToString("dd-MM-yyyy")
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
           
            
            
            if (response.IsSuccessful)
            {
                try
                {
                    // Debug: Verificar el contenido de la respuesta
                    var responseData = response.Content;
                    Console.WriteLine("Response Content: " + responseData);

                    // Intentar deserializar la respuesta
                    var formulario = JsonConvert.DeserializeObject<FormlarioModal>(responseData);
                    var participantes = formulario?.Content;

                    // Verificar si los participantes no son nulos
                    if (participantes != null)
                    {
                        return Json(new { data = participantes });
                    }
                    else
                    {
                        return Json(new { data = new List<PaticipantePi>() });
                    }
                }
                catch (JsonSerializationException ex)
                {
                    Console.WriteLine("Error de deserialización: " + ex.Message);
                    return Json(new { data = new List<PaticipantePi>() });
                }
            }
            else
            {
                return Json(new { data = new List<PaticipantePi>() });
            }
        }



        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ParticipanteId(int id)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = $"{URLCard}/contratosPI/modal";  // Agregar el id en la URL
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
                idParticipantes = id,
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
            if (response.IsSuccessful)
            {



                var paticipanteModalResponse = JsonConvert.DeserializeObject<FormlarioParticpanteModal>(response.Content);
                var paticipanteModal = paticipanteModalResponse.Content;

                // Asegúrate de retornar los datos en formato JSON adecuado
                return Json(new { data = paticipanteModal });
            }
            else
            {
                return Json(new { data = new List<ParticipanteModal>() });
            }
        }



        [HttpPost]
        [Authorize]
        public async Task<FormlarioParticpanteModal> ParticipanteCId(int id)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = $"{URLCard}/contratosPI/modal";  // Agregar el id en la URL
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
                idParticipantes = id,
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
            if (response.IsSuccessful)
            {



                var paticipanteModalResponse = JsonConvert.DeserializeObject<FormlarioParticpanteModal>(response.Content);
                var paticipanteModal = paticipanteModalResponse.Content;

                // Asegúrate de retornar los datos en formato JSON adecuado
                return paticipanteModalResponse;
            }
            else
            {
                return null;
            }
        }
        /// modal id
        /// actualizar 

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> ActualizarId(int id)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];
            var url = $"{URLCard}/contratosPI/actualizacion";  // URL para actualización
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
                idParticipantes = id,
                estatus = 1
            };

            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
            if (response.IsSuccessful)
            {
                var participanteModalResponse = JsonConvert.DeserializeObject<FormlarioParticpanteModal>(response.Content);

                return Json(new
                {
                    message = "Actualización realizada correctamente",
                    success = true
                });
            }
            else
            {
                return Json(new
                {
                    message = "Error al actualizar el participante",
                    success = false
                });
            }
        }


        [HttpGet]
        public async Task<ActionResult> GeneraPdfFormulario1(int idParticipante)
        {
            // Obtén el participante usando el id
            FormlarioParticpanteModal participante = await ParticipanteCId(idParticipante);

            // Crea una instancia del generador de PDF
            Formulario_1_1p pdfGenerator = new Formulario_1_1p();

            // Genera el PDF usando los datos del participante
            byte[] pdfBytes = pdfGenerator.GeneraPdfFormulario1(participante);

            // Retorna el archivo PDF como respuesta
            return File(pdfBytes, "application/pdf", "Apendice_1_1.pdf");
        }


        [HttpGet]
        public async Task<ActionResult> GenerateWord(int idParticipante)
        {

            FormlarioParticpanteModal participante = await ParticipanteCId(idParticipante);
            Formulario_1_1wp wordGenerator = new Formulario_1_1wp();
            byte[] wordBytes = wordGenerator.GeneraWordFormulario1(participante);

            // Retornar el archivo Word como respuesta
            return File(wordBytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Apendice_1_1.docx");
        }

        #endregion

        #region formulario 2
        /// formulario 2
        [HttpPost]
        [Authorize]
        public async Task<IActionResult> parIndirectosCuentasFiltro([FromBody] ConsultaRequestTrimestre consulta)
        {
           

            int anioActual = DateTime.Now.Year;

            var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
            var url = $"{URLCard}/formulario1Dos/cuentasAsignadas";  
            string usuarioCreacion = GetUsuarioCreacion();


            var requestBody = new
            {
                anio = anioActual,
                periodo = consulta.Trimestre

            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);

            if (response.IsSuccessful)
            {
                try
                {
                    // Debug: Verificar el contenido de la respuesta
                    var responseData = response.Content;
                    Console.WriteLine("Response Content: " + responseData);

                    // Intentar deserializar la respuesta
                    var formulario = JsonConvert.DeserializeObject<FormlarioResponse>(responseData);
                    var participantes = formulario?.Content;

                    // Verificar si los participantes no son nulos
                    if (participantes != null)
                    {
                        return Json(new { data = participantes });
                    }
                    else
                    {
                        return Json(new { data = new List<ParticipantesCuentas>() });
                    }
                }
                catch (JsonSerializationException ex)
                {
                    Console.WriteLine("Error de deserialización: " + ex.Message);
                    return Json(new { data = new List<ParticipantesCuentas>() });
                }
            }
            else
            {
                    return Json(new { data = new List<ParticipantesCuentas>() });
            }
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> parIndirtoGeneraGrupo([FromBody] GrupoRequestGrup request)
        {
           
            int anioActual = DateTime.Now.Year;       
           
            int trimestre = request.Trimestre;
            List<ParticipanteGrupo> participantes = request.Participantes;

            var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
            var url = $"{URLCard}/formulario1Dos";  
            string usuarioCreacion = GetUsuarioCreacion();


            var requestBody = new
            {
                anio = anioActual,      
                periodo = trimestre,    
                usuarioCreacion = usuarioCreacion, 
                grupo = participantes    
            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);
            if (response.IsSuccessful)
            {
                // Procesar la respuesta exitosa del endpoint
                var content = response.Content; // Contenido JSON de la respuesta
                return Content(content, "application/json");
            }
            else
            {
                // Retornar el contenido en caso de error
                var content = response.Content;
                return Content(content, "application/json");
            }
        }



        [HttpGet]
        public async Task<ActionResult> GeneraPdfFormulario2(int idParticipante)
        {
            // Obtén el participante usando el id
            FormlarioParticpanteModal participante = await ParticipanteCId(idParticipante);

            // Crea una instancia del generador de PDF
            Formulario_1_1p pdfGenerator = new Formulario_1_1p();

            // Genera el PDF usando los datos del participante
            byte[] pdfBytes = pdfGenerator.GeneraPdfFormulario1(participante);

            // Retorna el archivo PDF como respuesta
            return File(pdfBytes, "application/pdf", "documento.pdf");
        }

        [HttpPost]
        public async Task<ApiResponse22> ConsultArreglo(ConsultaRequestTrimestre request)
        {
        

            int anioActual = DateTime.Now.Year;

            int trimestre = request.Trimestre;
         
            var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
            var url = $"{URLCard}/formulario1Dos/listarCuentasAsignadas";
            string usuarioCreacion = GetUsuarioCreacion();


            var requestBody = new
            {
                anio = anioActual,
                periodo = trimestre,
              
            };


            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);

            if (response.IsSuccessful)
            {
                var participantesIndirectas = JsonConvert.DeserializeObject<ApiResponse22>(response.Content);
                return participantesIndirectas;
            }
            else
            {
                throw new Exception("Error en la consulta de participantes.");
            }
        }

        [HttpPost]
        public async Task<ActionResult> GenerarArchivo([FromBody] ConsultaRequestTrimestreArchivo request)
        {
            var consultaTrimestre = new ConsultaRequestTrimestre
            {
                Trimestre = request.Trimestre
            };

            // Obtener los datos desde ConsultArreglo
            ApiResponse22 participantesCuentasFirmas = await ConsultArreglo(consultaTrimestre);

            if (participantesCuentasFirmas == null || participantesCuentasFirmas.Content == null)
            {
                return BadRequest(new { message = "Error al generar el archivo. No se encontraron participantes." });
            }

        
            // Verificar si se debe generar un Word o un PDF
            if (request.Tipo == "word")
            {
                Formulario_1_2wp wordGenerator = new Formulario_1_2wp();
                byte[] wordBytes = wordGenerator.GeneraWordFormulario2(participantesCuentasFirmas.Content);

                return File(wordBytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Apendice1_2.docx");
            }
            else if (request.Tipo == "pdf")
            {
                Formulario_1_2p pdfGenerator = new Formulario_1_2p();
                byte[] pdfBytes = pdfGenerator.GeneraPdfFormulario2(participantesCuentasFirmas.Content);

                return File(pdfBytes, "application/pdf", "Apendice1_2.pdf");

            }


            return Json(new { message = "Archivo generado exitosamente." });
        }

        /// formulario 2

        #endregion



        #region formulario 3
        /// formulario 3
        [HttpPost]
        [Authorize]
        public async Task<IActionResult> parIndirectosCuentasFiltroBajas([FromBody] ConsultaRequestTrimestre consulta)
        {


            int anioActual = DateTime.Now.Year;


             var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
             var url = $"{URLCard}/formulario1Dos_v2/cuentasBajas";
             string usuarioCreacion = GetUsuarioCreacion();
       
            var requestBody = new
            {
                anio = anioActual,
                periodo = consulta.Trimestre
            };
          
          var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
      
            if (response.IsSuccessful)
            {
                try
                {
                    // Debug: Verificar el contenido de la respuesta
                    var responseData = response.Content;
                  

                    // Intentar deserializar la respuesta
                    var formulario = JsonConvert.DeserializeObject<FormlarioResponseBajas>(responseData);
                    var participantes = formulario?.Content;

                    // Verificar si los participantes no son nulos
                    if (participantes != null)
                    {
                        return Json(new { data = participantes });
                    }
                    else
                    {
                        return Json(new { data = new List<ParticipantesCuentaBajas>() });
                    }
                }
                catch (JsonSerializationException ex)
                {
                    Console.WriteLine("Error de deserialización: " + ex.Message);
                    return Json(new { data = new List<ParticipantesCuentaBajas>() });
                }
            }
            else
            {
                return Json(new { data = new List<ParticipantesCuentaBajas>() });
            }
        }



        [HttpPost]
        [Authorize]
        public async Task<IActionResult> parIndirtoGeneraGrupoBajas([FromBody] GrupoRequestGrup request)
        {

            int anioActual = DateTime.Now.Year;

            int trimestre = request.Trimestre;
            List<ParticipanteGrupo> participantes = request.Participantes;

           var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
             var url = $"{URLCard}/formulario1Dos_v2";
            string usuarioCreacion = GetUsuarioCreacion();

            var requestBody = new
            {
                anio = anioActual,
                periodo = trimestre,
                usuarioCreacion = usuarioCreacion,
                grupo = participantes
            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Post, requestBody);

            if (response.IsSuccessful)
            {
                // Procesar la respuesta exitosa del endpoint
                var content = response.Content; // Contenido JSON de la respuesta
                return Content(content, "application/json");
            }
            else
            {
                // Retornar el contenido en caso de error
                var content = response.Content;
                return Content(content, "application/json");
            }
        }
        [HttpPost]
        public async Task<ActionResult> GenerarArchivo3([FromBody] ConsultaRequestTrimestreArchivo request)
        {
            var consultaTrimestre = new ConsultaRequestTrimestre
            {
                Trimestre = request.Trimestre
            };

            // Obtener los datos desde ConsultArreglo3 Para el formulario3
            ApiResponseFormulario3 participantesCuentasFirmas = await ConsultArreglo3(consultaTrimestre);

            if (participantesCuentasFirmas == null || participantesCuentasFirmas.Content == null)
            {
                return BadRequest(new { message = "Error al generar el archivo. No se encontraron participantes." });
            }


            // Verificar si se debe generar un Word o un PDF para el formulario 3
            if (request.Tipo == "word")
            {
                Formulario_1_3wp wordGenerator = new Formulario_1_3wp();
                byte[] wordBytes = wordGenerator.GeneraWordFormulario3(participantesCuentasFirmas.Content);

                return File(wordBytes, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Apendice1_3.docx");
            }
            else if (request.Tipo == "pdf")
            {
                Formulario_1_3p pdfGenerator = new Formulario_1_3p();
                byte[] pdfBytes = pdfGenerator.GeneraPdfFormulario3(participantesCuentasFirmas.Content);

                return File(pdfBytes, "application/pdf", "Apendice1_3.pdf");

            }


            return Json(new { message = "Archivo generado exitosamente." });
        }


        [HttpPost]
        public async Task<ApiResponseFormulario3> ConsultArreglo3(ConsultaRequestTrimestre request)
        {


            int anioActual = DateTime.Now.Year;

            int trimestre = request.Trimestre;

           var URLCard = _config["EndPointsCore:apiParticipantesForm12"];
           var url = $"{URLCard}/formulario1Dos_v2/listarCuentasBajas";
            string usuarioCreacion = GetUsuarioCreacion();


            var requestBody = new
            {
                anio = anioActual,
                periodo = trimestre,
              
            };
            var response = await _tokenService.SendRequestWithToken(usuarioCreacion, url, RestSharp.Method.Get, requestBody);
            if (response.IsSuccessful)
            {
                var participantesIndirectas = JsonConvert.DeserializeObject<ApiResponseFormulario3>(response.Content);
                return participantesIndirectas;
            }
            else
            {
                throw new Exception("Error en la consulta de participantes.");
            }
        }

        #endregion


     
        private void InsertarBitacora(BitacoraRequest bitacoraRequest)
        {
            var URLCard = _config["EndPointsCore:apiURLParticipantes"];


            var options = new RestClientOptions(URLCard);

            var client = new RestClient(options);
            var request = new RestRequest("/formularios/bitacora", Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(bitacoraRequest);

            var response = client.Execute(request);

            if (!response.IsSuccessful)
            {
                // Manejar el error
                Console.WriteLine($"Error al insertar en la bitácora: {response.ErrorMessage}");
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