using DocumentFormat.OpenXml.EMMA;
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
using System.Security.Policy;
using DocumentFormat.OpenXml.Spreadsheet;
using iRisCore.Models;
using System.Reflection;
using OfficeOpenXml;
using OfficeOpenXml.Style;
using System.Data;
using System.Drawing;


namespace iRisCore.Controllers
{

    public class DashboardTarjetasSucursalController : Controller
    {

        private readonly IConfiguration _config;
        private readonly string URLLogistica = "";
        private readonly string userASP = "";
        private readonly string pwdASP = "";

        public DashboardTarjetasSucursalController(IConfiguration Config)
        {
            _config = Config;
            URLLogistica = _config["EndPointsCore:apiURLLogistica"];
            userASP = _config["EndPointsCore:userASP"];
            pwdASP = _config["EndPointsCore:passASP"];
        }

        [HttpGet]
        [Authorize]
        public IActionResult DashboardTarjetasSucursal()
        {

            var URLCard = _config["EndPointsCore:apiURLLogistica"];
            var userASP = _config["EndPointsCore:userASP"];
            var pwdASP = _config["EndPointsCore:passASP"];
            CatalogoLogisticaModel model = Catalogos.GetCatalogoLogistica(URLCard, userASP, pwdASP);

            ViewBag.Sucursales = model.sucursales;
            ViewBag.Marcas = model.marcas;
            ViewBag.Estatus = model.estatus;
            HttpContext.Session.SetObject("sucursales", model.sucursales);

            return View();
        }


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> GetSucursales(int pMarca)
        {
            try
            {
                ///-- Obtiene el catalogo de sucursales a traves de la session ---
                List<SucursalModel> objComplex = HttpContext.Session.GetObject<List<SucursalModel>>("sucursales");

            //--- Consulta el listado de tarjetas con estatus preasignada para agregarla en la tabla --------

                LogisticaModel objLogistica = new LogisticaModel();

                LogisticaRequestModel model = new LogisticaRequestModel();

                model.marca = 1;
                model.estatus = "PREASIG";
                model.sucursal = null;

                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

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
                    return Ok(new { sucursales = objComplex, tarjetaPreAsignadas = objLogistica });
                }

                List<LogisticaModel> da = JsonConvert.DeserializeObject<List<LogisticaModel>>(objLogistica.data);

                //-- Falta agregar aqui la seleccion de las tarjetas que no tengan asociada un archivo de distribución

                List<DetalleLogisticaModel> lstLogistica = new List<DetalleLogisticaModel>();

                foreach (LogisticaModel item in da)
                {
                    foreach (DetalleLogisticaModel itemDet in item.tarjetas)
                    {
                        if (itemDet.pan_used == "0")
                        {
                            lstLogistica.Add(new DetalleLogisticaModel
                            {
                                sucursal = itemDet.sucursal,
                                marca = itemDet.marca,
                                id = itemDet.idFolio,
                                //PAN = string.Concat("********", itemDet.PAN.Substring(8)),
                                PAN = itemDet.PAN,
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
                                cuentaASP = itemDet.cuentaASP
                            });
                        }
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



                var q = from c in lstLogistica 
                        group c by c.sucursal into g 
                        select new { g.Key, Count = g.Count() };

                List<PreasignadaModel> objPreasignadas = new List<PreasignadaModel>();
                foreach (var g in q)
                {
                    foreach (var item in objComplex)
                    {
                        if (g.Key == item.Sucursal)
                        {
                            objPreasignadas.Add(new PreasignadaModel { sucursalId = item.idSucursal, sucursal = item.Sucursal, cantidad = g.Count });
                        }
                    }
                }


                    return Ok(new { sucursales = objComplex, tarjetaPreAsignadas = objPreasignadas });
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }



        [HttpPost]
        [Authorize]
        public async Task<IActionResult> TarjetasSucursal(LogisticaRequestModel model)
        {
            LogisticaModel objLogistica = new LogisticaModel();

            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

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
                            //PAN = string.Concat("******", itemDet.PAN.Substring(6)),
                            PAN = itemDet.PAN,
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
                            fechaSolicitud = itemDet.fechaSolicitud,
                            fechaTransito = itemDet.fechaTransito,
                            fechaEntrega = itemDet.fechaEntrega
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
                if (model.estatus == "SINASIG") HttpContext.Session.SetObject("TarjetasSinAsignar", lstLogistica);                
                return Json(lstLogistica.OrderBy(x=>x.estatus).ToList());
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }

        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> PreAsignarTarjetasSucursal(int cantidadAsignar, int sucursalId, int marcaId, string quienRecibe)
        {
            int contador = 1;

            LogisticaRequestModel model = new LogisticaRequestModel();
            LogisticaModel objLogistica = new LogisticaModel();

            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.marca = marcaId;
                model.estatus = "SINASIG";
                model.sucursal = null;

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
                            PAN = itemDet.PAN,
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
                            cuentaASP = itemDet.cuentaASP
                        });
                    }
                }

                //-- Crea el request con el numero de tarjetas a actualizar como preasignada ----
                RequestLogisticaActualizarModel requestModel = new RequestLogisticaActualizarModel();
                requestModel.estatusId = "PREASIG";  //-- Estatus de preasignar la tarjeta para crear el archivo de distribución
                requestModel.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];
                requestModel.marcaId = marcaId;
                requestModel.sucursalId = sucursalId;
                requestModel.observaciones = "";
                requestModel.nombreRecibe = quienRecibe;
                foreach (DetalleLogisticaModel item in lstLogistica)
                {
                    if (contador <= cantidadAsignar)
                    {
                        requestModel.detalleTarjeta.Add(
                                                        new RequestLogisticaDetalleTarjetaModel 
                                                        { 
                                                            numeroTarjeta = item.PAN 
                                                        });
                        contador++;
                    }
                    else
                    {
                        break;
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


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> CargarArchivoTarjetas(IList<IFormFile> files)
        {
            
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                string? filePath = "";

                string[] valor;
                try
                {
                    

                    long size = files.Sum(f => f.Length);
                    foreach (var formFile in files)
                    {
                        if (formFile.Length > 0)
                        {
                            var path = Environment.CurrentDirectory + "/InventarioTF/";

                            if (!Directory.Exists(path))
                            {
                                Directory.CreateDirectory(path);
                            }
                            filePath = path + Path.GetFileName(formFile.FileName);

                            using (var stream = System.IO.File.Create(filePath))
                            {
                                await formFile.CopyToAsync(stream);
                            }

                            //csvData = System.IO.File.ReadAllText(filePath);
                            //if (System.IO.File.Exists(filePath))
                            //    System.IO.File.Delete(filePath);
                        }
                    }

                    RequestLogisticaHeader model = new RequestLogisticaHeader();

                    model.header = new RequestHeaderModel
                    {
                        idComisionista = 0,
                        idTransaccion = 0,
                        idEmpresa = 0,
                        idResponsabilidad = 0,
                        usuarioClave = returnUser,
                        idUsuario = Int32.Parse(iduser),
                        idClaseCanalAtencion = 0,
                        idPuntoAtención = 0,
                        idCanalAtencion = 1,
                        idUbicacion = 0,
                        idSucursal = 1,
                        ipHost = ipUser
                    };

                    var options = new RestClientOptions(URLLogistica)
                    {
                        Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                    };
                    var client = new RestClient(options);

                    var request = new RestRequest("/registrarArchivoCargaTarjetas", Method.Post);
                    request.AlwaysMultipartFormData = false;
                    request.AddFile("file", filePath);
                    request.AddParameter("datos", JsonConvert.SerializeObject(model));
                    RestResponse response = await client.ExecuteAsync(request);
                    dynamic datos = JObject.Parse(response.Content);

                    int code = datos.codigo;
                    string msg = datos.mensaje;

                    return Ok(new { codigo = code, mensaje= msg });


                }
                catch (IOException ex)
                {
                    logiRis.Add("Cargar archivo de tarjetas: Error en carga de archivo inventario de Tarjetas Físicas:" + ex.Message);
                    return Ok(new { codigo = "-1", mensaje = "Cargar archivo de tarjetas: Error al cargar el archivo: " + ex.Message });
                }                

            } catch (Exception ex)
            {
                logiRis.Add("Cargar archivo de tarjetas: Error el procesamiento de carga del archivo de Tarjetas Físicas:" + ex.Message);
                return Ok(new { codigo = "-2", mensaje = "Cargar archivo de tarjetas: Error en la funcionalidad de la carga: " + ex.Message });
            }

        }


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> DesasignarTarjetasSucursal(string valorId, int marcaId)
        {
            //-- Action para desasignar las tarjetas pre-asignadas ---


            LogisticaModel objLogistica = new LogisticaModel();
            LogisticaRequestModel model = new LogisticaRequestModel();

            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                var items = valorId.Split('|');

                model.sucursal = Int32.Parse(items[0]);
                model.marca = marcaId;
                model.estatus = "PREASIG";

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

                List<LogisticaModel> da = JsonConvert.DeserializeObject<List<LogisticaModel>>(objLogistica.data);

                List<DetalleLogisticaModel> lstLogistica = new List<DetalleLogisticaModel>();

                int contador = 1;

                //-- Crea el request con el numero de tarjetas a actualizar como preasignada ----
                RequestLogisticaActualizarModel requestModel = new RequestLogisticaActualizarModel();
                requestModel.estatusId = "SINASIG";  
                requestModel.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];
                requestModel.marcaId = marcaId;
                requestModel.sucursalId = Int32.Parse(items[0]);
                requestModel.observaciones = "";

                foreach (LogisticaModel item in da)
                {
                    foreach (DetalleLogisticaModel itemDet in item.tarjetas)
                    {
                        if (itemDet.pan_used == "0" && contador <= Int32.Parse(items[1]))   //--- Valida que la tajeta no este en un arch de dist y que la cantidad sea igual a la preasignada
                        {

                            requestModel.detalleTarjeta.Add(
                                                        new RequestLogisticaDetalleTarjetaModel
                                                        {
                                                            numeroTarjeta = itemDet.PAN
                                                        });                           
                        }
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


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> SetArchivoDistribucion()
        {
            RequestLogisticaArchivoDistribucion objDistribucion = new RequestLogisticaArchivoDistribucion();
            ResponseArchivoDistribucion responseDistribucion = new ResponseArchivoDistribucion();
            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                objDistribucion.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];

                var options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/archivoDistribucion-pan", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(objDistribucion);
                var response = await client.ExecutePostAsync(request);

                dynamic datos = JObject.Parse(response.Content);
                responseDistribucion.codigo = datos.codigo;
                responseDistribucion.mensaje = datos.mensaje;
                responseDistribucion.data = datos.data;

                if (responseDistribucion.codigo != 0)
                {
                    return Json(responseDistribucion);
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

                return Json(responseDistribucion);
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return Json(null);

            }
        }


        [HttpPost]
        [Authorize]
        public async Task<IActionResult> SetAsignarCEDIS(int marcaId)
        {
            int contador = 1;

            LogisticaRequestModel model = new LogisticaRequestModel();
            LogisticaModel objLogistica = new LogisticaModel();

            try
            {
                var identity = (ClaimsIdentity)User.Identity;

                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

                model.marca = marcaId;
                model.estatus = "SINASIG";
                model.sucursal = null;

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
                            PAN = itemDet.PAN,
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
                            cuentaASP = itemDet.cuentaASP
                        });
                    }
                }

                //-- Crea el request con el numero de tarjetas a actualizar como preasignada ----
                RequestLogisticaActualizarModel requestModel = new RequestLogisticaActualizarModel();
                requestModel.estatusId = "ASIG";  //-- Estatus de asignar la tarjeta para
                requestModel.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];
                requestModel.marcaId = marcaId;
                requestModel.sucursalId = Int32.Parse(_config["Miscelanius:idSucursalCEDIS"]);
                requestModel.observaciones = "";
                requestModel.nombreRecibe = "";
                foreach (DetalleLogisticaModel item in lstLogistica)
                {                   
                    requestModel.detalleTarjeta.Add(
                                                new RequestLogisticaDetalleTarjetaModel
                                                {
                                                    numeroTarjeta = item.PAN
                                                });
                       
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



        public async Task<IActionResult> DevolucionCEDIS(RequestLogisticaActualizarModel model, string numTarjeta)
        {
            LogisticaModel objLogistica = new LogisticaModel();
            try
            {    
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

                //-- Crea el request con el numero de tarjetas a actualizar como preasignada ----
                model.sucursalId = Int32.Parse(_config["Miscelanius:idSucursalCEDIS"]);
                model.estatusId = "DEV";               
                model.origenSolicitud = _config["Miscelanius:OrigenSolicitud"];

                RequestLogisticaDetalleTarjetaModel objTarjeta = new RequestLogisticaDetalleTarjetaModel();
                objTarjeta.numeroTarjeta = numTarjeta;

                model.detalleTarjeta.Add(objTarjeta);

                //--- WS de actualizar el estatus --------

                options = new RestClientOptions(URLLogistica)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                client = new RestClient(options);

                var request = new RestRequest("/actualizarEstatusTarjetas-pan", RestSharp.Method.Post);
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



        [HttpGet]
        [Authorize]
        public async Task<FileResult> DownloadExcel(string marcaId, string sucursalId, string estatusId, bool firstStock, string sucursalNombre)
        {
            ExcelPackage.LicenseContext = LicenseContext.NonCommercial;
            LogisticaModel objLogistica = new LogisticaModel();
            LogisticaRequestModel model = new LogisticaRequestModel();
            model.marca = Int32.Parse(marcaId);
            model.estatus = estatusId;
            model.sucursal = Int32.Parse(sucursalId);
            model.firstStock = firstStock;
            try
            {
                var identity = (ClaimsIdentity)User.Identity;
                string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

                string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
                string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;

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

                List<LogisticaModel> da = JsonConvert.DeserializeObject<List<LogisticaModel>>(objLogistica.data);

                List<DetalleLogisticaModel> lstLogistica = new List<DetalleLogisticaModel>();

                using (var package = new ExcelPackage())

                {

                    // Add a new worksheet to the empty workbook

                    var worksheet = package.Workbook.Worksheets.Add("Sheet1");


                    using (ExcelRange Rng = worksheet.Cells[5, 1, 6, 8])
                    {
                       // Rng.Value = "Welcome to Everyday be coding - tutorials for beginners";
                        Rng.Style.Font.Size = 10;
                        Rng.Style.Font.Bold = true;
                        Rng.Style.Font.Italic = true;
                    }
                    //System.Drawing.Color DeepBlueHexCode = ColorTranslator.FromHtml("#254061");

                    ////First Border Box  
                    //using (ExcelRange Rng = worksheet.Cells[5, 2, 8, 4])
                    //{
                    //    Rng.Value = "Thin";
                    //    Rng.Merge = true;
                    //    Rng.Style.Border.Top.Style = ExcelBorderStyle.Thin;
                    //   // Rng.Style.Border.Top.Color.SetColor(System.Drawing.Color.Red);
                    //    Rng.Style.Border.Left.Style = ExcelBorderStyle.Thin;
                    //   // Rng.Style.Border.Left.Color.SetColor(System.Drawing.Color.Green);
                    //    Rng.Style.Border.Right.Style = ExcelBorderStyle.Thin;
                    //   // Rng.Style.Border.Right.Color.SetColor(System.Drawing.Color.Green);
                    //    Rng.Style.Border.Bottom.Style = ExcelBorderStyle.Thin;
                    //    //Rng.Style.Border.Bottom.Color.SetColor(DeepBlueHexCode);
                    //}

                    // Add some sample data


                    worksheet.Cells["A1"].Value = "TARJETA DE MOVIMIENTOS DE ALMACEN TARJETAS MASTERCARD";
                    worksheet.Cells["A1:F1"].Merge = true;
                    worksheet.Cells["A2"].Value = "ARTICULO";
                    worksheet.Cells["B2"].Value = "TARJETAS MASTERCARD";
                    worksheet.Cells["A3"].Value = "SUCURSAL";
                    worksheet.Cells["B3"].Value = sucursalNombre;
                    worksheet.Cells["F3"].Value = "RESPONSABLE";
                    worksheet.Cells["G3"].Value = returnUser;

                    worksheet.Cells["A6"].Value = "FECHA";
                    worksheet.Cells["B6"].Value = "REFERENCIA";
                    worksheet.Cells["C6"].Value = "CONCEPTO";

                    worksheet.Cells["D5"].Value = "FOLIOS";
                    worksheet.Cells["D5:E5"].Merge = true;

                    worksheet.Cells["D6"].Value = "ENTRADAS";
                    worksheet.Cells["E6"].Value = "SALIDAS";

                    worksheet.Cells["F5"].Value = "UNIDADES";
                    worksheet.Cells["F5:G5"].Merge = true;

                    worksheet.Cells["F6"].Value = "ENTRADAS";
                    worksheet.Cells["G6"].Value = "SALIDAS";
                    worksheet.Cells["H6"].Value = "EXISTENCIA";

                  
                    worksheet.Cells.AutoFitColumns();

                    //worksheet.Cells["B2"].Value = "John Doe";

                    // Set the stream to save the file

                    var stream = new MemoryStream();

                    // Save the package to the stream

                    package.SaveAs(stream);




                    // Set the position of the stream to 0

                    stream.Position = 0;

                    // Return the stream as a file download

                    return File(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MyExcelFile.xlsx");

                }

                //foreach (LogisticaModel item in da)
                //{
                //    foreach (DetalleLogisticaModel itemDet in item.tarjetas)
                //    {
                //        lstLogistica.Add(new DetalleLogisticaModel
                //        {
                //            sucursal = itemDet.sucursal,
                //            marca = itemDet.marca,
                //            id = itemDet.idFolio,                            
                //            PAN = itemDet.PAN,
                //            fechaVencimiento = itemDet.fechaVencimiento,
                //            guia = itemDet.guia,
                //            nombre = itemDet.nombre,
                //            direccion1 = itemDet.direccion1,
                //            direccion2 = itemDet.direccion2,
                //            colonia = itemDet.colonia,
                //            ciudad = itemDet.ciudad,
                //            estado = itemDet.estado,
                //            cp = itemDet.cp,
                //            telefono = itemDet.telefono,
                //            celular = itemDet.celular,
                //            email = itemDet.email,
                //            estatus = itemDet.estatus,
                //            cuentaASP = itemDet.cuentaASP
                //        });
                //    }
                //}

                
                
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return null;
            }




            // Setting the license context for EPPlus

            //-- ExcelPackage.LicenseContext = LicenseContext.NonCommercial;

            // Create a new Excel package

            //using (var package = new ExcelPackage())

            //{

            //    // Add a new worksheet to the empty workbook

            //    var worksheet = package.Workbook.Worksheets.Add("Sheet1");

            //    // Add some sample data

            //    worksheet.Cells["A1"].Value = "ID";

            //    worksheet.Cells["B1"].Value = "Name";

            //    worksheet.Cells["A2"].Value = 1;

            //    worksheet.Cells["B2"].Value = "John Doe";

            //    // Set the stream to save the file

            //    var stream = new MemoryStream();

            //    // Save the package to the stream

            //    package.SaveAs(stream);

            //    // Set the position of the stream to 0

            //    stream.Position = 0;

            //    // Return the stream as a file download

            //    return File(stream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MyExcelFile.xlsx");

            //}

        }







        //[HttpGet]
        //[Authorize]
        //public void ReporteSpeiByDate()
        //{          
        //    try
        //    {

        //        var pck = ExportToExcelTask();
        //        Response.Clear();
        //        Response.ContentType = "application/vnd.openxmlformats-officedocument.spreadsheetlm.sheet";
        //        Response.AddHeader("content-disposition", "attachment; filename=SpeiInOut-Report.xlsx");
        //        Response.BinaryWrite(pck);
        //        Response.End();

        //    }
        //    catch (Exception ex)
        //    {
        //        ViewBag.Error = ex.Message;
        //    }
        //}

        //private byte[] ExportToExcelTask()
        //{

        //    ExcelPackage blackListExcel = new ExcelPackage();
        //    ExcelWorksheet ws = blackListExcel.Workbook.Worksheets.Add("SpeiIn");          

        //    ws.Cells["A1"].Value = "Clave Rastreo";
        //    ws.Cells["A1"].Style.Font.Bold = true;
        //    ws.Cells["B1"].Value = "Cuenta Ordenante";
        //    ws.Cells["B1"].Style.Font.Bold = true;
        //    ws.Cells["C1"].Value = "Cuenta Beneficiario";
        //    ws.Cells["C1"].Style.Font.Bold = true;
        //    ws.Cells["D1"].Value = "Empresa";
        //    ws.Cells["D1"].Style.Font.Bold = true;
        //    ws.Cells["E1"].Value = "Fecha Operación";
        //    ws.Cells["E1"].Style.Font.Bold = true;
        //    ws.Cells["F1"].Value = "Monto";
        //    ws.Cells["F1"].Style.Font.Bold = true;
        //    ws.Cells["G1"].Value = "Último Estatus";
        //    ws.Cells["G1"].Style.Font.Bold = true;
        //    ws.Cells["H1"].Value = "Estado";
        //    ws.Cells["H1"].Style.Font.Bold = true;
        //    ws.Cells["I1"].Value = "Ultimo Cambio Estado";
        //    ws.Cells["I1"].Style.Font.Bold = true;
        //    ws.Cells["J1"].Value = "Ultimo Cambio Estado DD/MM/AAA";
        //    ws.Cells["J1"].Style.Font.Bold = true;
        //    ws.Cells["K1"].Value = "Motivo de Rechazo";
        //    ws.Cells["K1"].Style.Font.Bold = true;        


            
        //    ws.Cells["A:I"].AutoFitColumns();

           
        //    return blackListExcel.GetAsByteArray();
        //}

    }
}
