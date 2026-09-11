using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using RestSharp;
using RestSharp.Authenticators;
using System.Security.Claims;

namespace iRisCore.Controllers
{
    public class ConsultaDenegacionesController : Controller
    {
        private readonly IConfiguration _config;
        public ConsultaDenegacionesController(IConfiguration Config)
        {
            _config = Config;
        }

        [Authorize]
        [HttpGet]
        public IActionResult ConsultaDenegaciones()
        {
            return View();
        }
        [Authorize]
        [HttpPost]
        public async Task<ActionResult> ConsultaDenegaciones(DenegacionesModel model, string tipoMedio)
        {
            ConsultarTarjetaResponse resultado = new ConsultarTarjetaResponse();
            try
            {
                var returnUser = User.Claims?.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value ?? string.Empty;

                var options = new RestClientOptions(_config["EndPointsCore:apiUrlDenegaciones"])
                {
                    Authenticator = new HttpBasicAuthenticator(_config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"])
                };
                var client = new RestClient(options);
                var request = new RestRequest("/Denials", Method.Get);

                request.AddParameter("cardNumber", model.tarjeta);

                var from = string.IsNullOrEmpty(model.fechaInicial)
                    ? DateTime.UtcNow.AddDays(-90).Date
                    : DateTime.Parse(model.fechaInicial).Date;
                from = DateTime.SpecifyKind(from, DateTimeKind.Utc);

                request.AddParameter("from", from.ToString("yyyy-MM-ddTHH:mm:ss"));

                var to = string.IsNullOrEmpty(model.fechaFinal)
                    ? DateTime.UtcNow.Date.AddDays(1).AddSeconds(-1)
                    : DateTime.Parse(model.fechaFinal).Date.AddDays(1).AddSeconds(-1);
                to = DateTime.SpecifyKind(to, DateTimeKind.Utc);
                request.AddParameter("to", to.ToString("yyyy-MM-ddTHH:mm:ss"));

                var response = await client.GetAsync(request);

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
                logiRis.Add("Usuario:" + returnUser + "-Response:" + request.Resource + response);

                var result = JsonConvert.DeserializeObject<List<Transacciones>>(response.Content ?? string.Empty);

                return Json(result);
            }
            catch (Exception ex)
            {
                logiRis.Add("Hubo un error al consultar denegaciones: " + ex.Message);
                return Json(null);
            }
        }
    }
}
