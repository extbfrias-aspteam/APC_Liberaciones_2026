using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.Extensions.Configuration;

namespace iRisCore.Controllers
{
    public class BO_FormulariosController : Controller
    {
        private readonly IConfiguration _config;
        public BO_FormulariosController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        [Authorize]
        public IActionResult BO_Formularios()
        {
            return View();
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetParticipantesSaldoPblu()
        {


            var URLCard = _config["EndPointsCore:apiURLParticipantes"];


            var options = new RestClientOptions(URLCard);

            var client = new RestClient(options);
           
            var request = new RestRequest("/participantes/saldoPblu", RestSharp.Method.Get);
            var response = await client.ExecuteAsync(request);
            if (response.IsSuccessful)
            {
                var participantes = JsonConvert.DeserializeObject<List<ParticipanteSaldos>>(response.Content);
                return Json(participantes);
            }
            else
            {
                return StatusCode((int)response.StatusCode, response.Content);
            }
        }
    }

    
}
