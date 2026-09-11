using iRisCore.Session;
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

namespace iRisCore.Controllers
{
    public class RecepcionEntregaTarjetasController : Controller
    {
        private readonly IConfiguration _config;
        public RecepcionEntregaTarjetasController(IConfiguration Config)
        {
            _config = Config;
        }

        [HttpGet]
        [Authorize]
        public IActionResult RecepcionEntregaTarjetas()
        {
            var URLCard = _config["EndPointsCore:apiURLLogistica"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];

            var strEstatus = _config["Miscelanius:EstatusEnSucursal"];
            var estatus = strEstatus.Split('|');

            CatalogoLogisticaModel model = Catalogos.GetCatalogoLogistica(URLCard, userASP, pwdASP);

            ViewBag.Marcas = model.marcas;


            List<EstatusModel> lstEstatus = new List<EstatusModel>();

            foreach (string item in estatus)
            {
                List<EstatusModel> lstEstatusCajera = model.estatus.Where(x => x.idEstatus == item).ToList();
                foreach (var item2 in lstEstatusCajera)
                {
                    lstEstatus.Add(new EstatusModel
                    {
                        idEstatus = item2.idEstatus,
                        Estatus = item2.Estatus
                    });

                }
            }
            ViewBag.Estatus = lstEstatus;


            // HttpContext.Session.SetObject("sucursales", model.sucursales);

            return View();
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> RecepcionEntregaTarjetas(LogisticaRequestModel model)
        {
            LogisticaModel objLogistica = new LogisticaModel();
            try
            {
                var idSucursal = HttpContext.Session.GetInt32("sucursalUsuario");
                model.sucursal = idSucursal;
                //model.sucursal = 15;  //- Transito
                //model.sucursal = 2;

                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;



                var URLLogistica = _config["EndPointsCore:apiURLLogistica"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/consultarTarjetas-pan", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                objLogistica.codigo = datos.codigo;
                objLogistica.mensaje = datos.mensaje;
                objLogistica.data = datos.data;

                if (objLogistica.codigo != 0)
                {
                    //return Json(objLogistica);
                    return Json(new { detalle = "", result = objLogistica });
                }

                List<LogisticaModel> da = JsonConvert.DeserializeObject<List<LogisticaModel>>(objLogistica.data);

                List<DetalleLogisticaModel> lstLogistica = new List<DetalleLogisticaModel>();

                foreach (LogisticaModel item in da)
                {
                    foreach (DetalleLogisticaModel itemDet in item.tarjetas)
                    {
                        lstLogistica.Add(new DetalleLogisticaModel
                        {
                            sucursal = itemDet.sucursal,
                            marca = itemDet.marca,
                            id = itemDet.idFolio,
                            PAN = itemDet.PAN != "" ? string.Concat("******", itemDet.PAN.Substring(6)) : itemDet.PAN,
                            fechaVencimiento = itemDet.fechaVencimiento,
                            guia = itemDet.guia,
                            nombre = itemDet.nombre,
                            direccion1 = itemDet.direccion1,
                            direccion2 = itemDet.direccion2,
                            colonia = itemDet.colonia,
                            ciudad = itemDet.ciudad,
                            estado = itemDet.estado,
                            cp = itemDet.cp,
                            telefono = itemDet.telefono,
                            celular = itemDet.celular,
                            email = itemDet.email,
                            estatus = itemDet.estatus,
                            cuentaASP = itemDet.cuentaASP,
                            guiaDev = itemDet.guiaDev,
                            observaciones = itemDet.observaciones,
                            fechaEntrega = itemDet.fechaEntrega,
                            fechaSolicitud = itemDet.fechaSolicitud,
                            fechaTransito = itemDet.fechaTransito
                        });
                    }
                }

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

                //return Json(lstLogistica);
                return Json(new { detalle = lstLogistica, result = objLogistica });
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> RecepcionTarjetas(LogisticaRequestModel model, string tipoOperacion, string observaciones)
        {
            LogisticaModel objLogistica = new LogisticaModel();
            try
            {
                var strEstatus = _config["Miscelanius:EstatusEnSucursal"];
                var estatus = strEstatus.Split('|');

                model.estatus = estatus[0].ToString();  //-- TRAN == Tránsito

                var idSucursal = HttpContext.Session.GetInt32("sucursalUsuario");
                model.sucursal = idSucursal;


                //model.sucursal = 15;  //- Transito
                //model.sucursal = 2;


                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;



                var URLLogistica = _config["EndPointsCore:apiURLLogistica"];
                var userASP = _config["EndPointsCore:userASP"];
                var pwdASP = _config["EndPointsCore:passASP"];

                var options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/consultarTarjetas-pan", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                objLogistica.codigo = datos.codigo;
                objLogistica.mensaje = datos.mensaje;
                objLogistica.data = datos.data;

                if (objLogistica.codigo != 0)
                {
                    return Json(objLogistica);
                }


                //-- Crea el request con el numero de tarjetas a actualizar como preasignada ----
                RequestLogisticaActualizarModel requestModel = new RequestLogisticaActualizarModel();
                if (tipoOperacion == "receive")
                {
                    requestModel.estatusId = "ASIG";
                }
                else if (tipoOperacion == "return")
                {
                    requestModel.estatusId = "DEV";
                }

                requestModel.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];
                requestModel.marcaId = model.marca;
                requestModel.sucursalId = idSucursal;
                requestModel.observaciones = observaciones;
                requestModel.guiaDev = "N/A EN SUCURSAL";

                List<LogisticaModel> da = JsonConvert.DeserializeObject<List<LogisticaModel>>(objLogistica.data);

                List<DetalleLogisticaModel> lstLogistica = new List<DetalleLogisticaModel>();

                foreach (LogisticaModel item in da)
                {
                    foreach (DetalleLogisticaModel itemDet in item.tarjetas)
                    {
                        requestModel.detalleTarjeta.Add(
                                                       new RequestLogisticaDetalleTarjetaModel
                                                       {
                                                           numeroTarjeta = itemDet.PAN
                                                       });
                    }
                }

                //--- WS de actualizar el estatus --------

                options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                client = new RestClient(options);

                request = new RestRequest("/actualizarEstatusTarjetas-pan", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(requestModel);
                response = await client.ExecutePostAsync(request);

                datos = JObject.Parse(response.Content);
                objLogistica.codigo = datos.codigo;
                objLogistica.mensaje = datos.mensaje;
                objLogistica.data = datos.data;

                if (objLogistica.codigo != 0)
                {
                    return Json(objLogistica);
                }
                ///--- Fin de la actualización ----


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


                return Ok(new { codigo = "0", mensaje = "" });
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }

    }
}
