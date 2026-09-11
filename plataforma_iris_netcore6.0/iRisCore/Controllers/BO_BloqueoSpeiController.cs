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
using System.Security.Policy;
using DocumentFormat.OpenXml.EMMA;

namespace iRisCore.Controllers
{
    public class BO_BloqueoSpeiController : Controller
    {
        private readonly IConfiguration _config;
        private readonly TokenService _tokenService;

        public BO_BloqueoSpeiController(IConfiguration config, TokenService tokenService)
        {
            _config = config;
            _tokenService = tokenService;
        }

        public IActionResult BO_BloqueoSpei()
        {
            return View();
        }

        [HttpPost]
        public async Task<ActionResult> BloqueoSpei(BloqueoSpeiModel model)
        {
            BloqueoSpeiResponse resultado = new BloqueoSpeiResponse();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.usuarioCreacion = returnUser;

                var url = _config["EndPointsCore:apiURLNotificaciones"];
                url = url + "/spei/bloqueoDesbloqueoCuenta";

                var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Post, model);
                dynamic datos = JObject.Parse(response.Content);

                if (datos.eiyuResponseCode == "OK")
                {
                    resultado.descripcion = datos.descripcion;
                    resultado.eiyuResponseCode = datos.eiyuResponseCode;
                    return Json(resultado);
                }
                else if (resultado.eiyuResponseCode == "ERROR")
                {
                    resultado.descripcion = datos.descripcion;
                    resultado.eiyuResponseCode = datos.eiyuResponseCode;
                    return Json(resultado);
                }
                else
                {
                    resultado.descripcion = "Error en el servicio";
                    resultado.eiyuResponseCode = datos.eiyuResponseCode;
                    return Json(resultado);
                }
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

        [HttpGet]
        public async Task<ActionResult> GetParticipantes()
        {
            List<MyDataModel> participantes = new List<MyDataModel>();
            var identity = (ClaimsIdentity)User.Identity;
            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;
            var responseData = new MyDataResponse2();
            var jResult = new ResponseConfigNotif();

            var url = _config["EndPointsCore:apiURLParticipantes"];
            url = url + "/participantes/list";

            var response = await _tokenService.SendRequestWithToken(returnUser, url, RestSharp.Method.Get);

            if (response.IsSuccessful)
            {
                responseData = JsonConvert.DeserializeObject<MyDataResponse2>(response.Content);

                if (responseData.EiyuResponseCode == "OK")
                {
                    List<MyDataModel> participantes_ = responseData.Content.ToObject<List<MyDataModel>>();
                    participantes = participantes_.Where(d => d.activo).ToList();

                    jResult = new ResponseConfigNotif { Codigo = 1, Mensaje = "", Data = participantes };
                    return Json(new { detalle = "", result = jResult });
                }
                else
                {
                    jResult = new ResponseConfigNotif { Codigo = 0, Mensaje = "error en /participantes/list", Data = "" };
                }
            }
            else
            {
                jResult = new ResponseConfigNotif { Codigo = -1, Mensaje = response.ErrorException.Message, Data = "" };
            }

            return Json(new { detalle = "", result = jResult });
        }
    }
}
