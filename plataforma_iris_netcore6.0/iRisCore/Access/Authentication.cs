using DocumentFormat.OpenXml.EMMA;
using DocumentFormat.OpenXml.Spreadsheet;
using iRisCore.Models;
using iRisCore.Services;
using iRisLog;
using iRisModel;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ViewFeatures;
using Microsoft.DotNet.Scaffolding.Shared.CodeModifier.CodeChange;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System;
using System.Net.Http.Json;
using System.Reflection;
using System.Security.Policy;
using System.Text.Json.Nodes;
using System.Web.Helpers;

namespace iRisCore.Access
{
    public class Authentication
    {
        private static IConfiguration _configuration;
        private static TokenService _tokenService;

        // Método estático para inicializar el TokenService
        public static void Initialize(IConfiguration iConfig, TokenService tokenService)
        {
            _tokenService = tokenService;
            _configuration = iConfig;
        }
        public static LoginTokenResponse LoginASP(string UserName, string CadenaCifrada, string IpHost, string IdAplicativo, string apiUrl)
        {
            try
            {
                var modelLogin = new AccountModel()
                {
                    usuario = UserName,
                    contrasena = CadenaCifrada,
                    idUsuario = "0",
                    ipUsuario = IpHost,
                    idSesion = "0",
                    estatus = "0",
                    idAplicativo = IdAplicativo
                };

                var url = apiUrl;
                var client = new RestClient(url);
                var request = new RestRequest(url, RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(modelLogin);

                var response = client.ExecutePost(request);
                LoginTokenResponse x = JsonConvert.DeserializeObject<LoginTokenResponse>(response.Content);

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

                logiRis.Add("Usuario:" + UserName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + UserName + "-Response:" + request.Resource + response.Content, "SEG");

                return x;
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message, "SEG");
                return null;
            }
        }

        public static ModelLoginDockResponse LoginDock(string UserName, string CadenaCifrada, string apiUrl, string userASP, string pwdASP)
        {
            try
            {
                var LoginDock = new ModelLoginDock()
                {
                    NombreUsuario = UserName,
                    Password = CadenaCifrada
                };

                var options = new RestClientOptions(apiUrl)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };

                var client = new RestClient(options);
                var request = new RestRequest("/logInDock", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(LoginDock);

                var response = client.ExecutePost(request);


                ModelLoginDockResponse da = JsonConvert.DeserializeObject<ModelLoginDockResponse>(response.Content);


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

                logiRis.Add("Usuario:" + UserName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog));
                logiRis.Add("Usuario:" + UserName + "-Response:" + request.Resource + response.Content);

                return da;
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al registrar en el log-" + ex.Message);
                return null;
            }
        }

        public static async Task<dynamic> LogOutASPAsync(string UserName, string idUser, string ipHost, string url, string userASP, string pwdASP, string appId)
        {
            var modelLogin = new AccountModel()
            {
                idUsuario = idUser,
                ipUsuario = ipHost,
                idSesion = "0",
                estatus = "0",
                idAplicativo = appId
            };


            //var options = new RestClientOptions(URLSeg)
            //{
            //    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            //};
            //var client = new RestClient(options);

            //var request = new RestRequest("/logout", RestSharp.Method.Post);
            //request.AddHeader("Content-Type", "application/json");
            //request.RequestFormat = DataFormat.Json;
            //request.AddJsonBody(modelLogin);
            //var response = client.ExecutePost(request);
            var response = await _tokenService.SendRequestWithToken(UserName, url + "/logout", RestSharp.Method.Post, modelLogin);
            //dynamic x = JObject.Parse(response.Content);
            return response.IsSuccessful;
        }

        public static async Task<int> SendTokenSMSAsync(string apiUrl, string UserName, string CadenaAcceso, string numeroEmpleado, string usuarioCero, string ipHost, string userASP, string pwdASP)
        {
            try
            {
                var modelToken = new ModelToken()
                {
                    clave = UserName,
                    numeroEmpleado = numeroEmpleado,
                    usuarioCero = usuarioCero,
                    password = CadenaAcceso,
                    ipHost = ipHost
                };

                var url = apiUrl + "/login";
                //var client = new RestClient(url);
                //var request = new RestRequest(url, RestSharp.Method.Post);
                //request.AddBody(modelToken);

                //var response = client.ExecutePost(request);
                var response = await _tokenService.SendRequestWithToken(UserName, url, RestSharp.Method.Post, modelToken);
                dynamic x = JObject.Parse(response.Content);

                //var requestToLog = new
                //{
                //    resource = request.Resource,
                //    parameters = request.Parameters.Select(parameter => new
                //    {
                //        name = parameter.Name,
                //        value = parameter.Value,
                //        type = parameter.Type.ToString()
                //    }),
                //    method = request.Method.ToString(),
                //    uri = client.BuildUri(request),
                //};

                //logiRis.Add("Usuario:" + UserName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                //logiRis.Add("Usuario:" + UserName + "-Response:" + request.Resource + response.Content, "SEG");

                return 0;
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al enviar el Token - " + ex.Message, "SEG");
                return -1;
            }
        }

        public static async Task<int> VerifyTokenSMSAsync(string apiUrl, string userName, string numeroEmpleado, string tokenSMS)
        {
            try
            {
                var modelToken = new ModelVerifyToken()
                {
                    NumeroEmpleado = numeroEmpleado,
                    Token = tokenSMS
                };

                var url = apiUrl + "/check-token";
                //var client = new RestClient(url);
                //var request = new RestRequest(url, RestSharp.Method.Post);
                //request.AddBody(modelToken);

                //var response = client.ExecutePost(request);
                var response = await _tokenService.SendRequestWithToken(userName, url, RestSharp.Method.Post, modelToken);
                dynamic x = JObject.Parse(response.Content);

                //var requestToLog = new
                //{
                //    resource = request.Resource,
                //    parameters = request.Parameters.Select(parameter => new
                //    {
                //        name = parameter.Name,
                //        value = parameter.Value,
                //        type = parameter.Type.ToString()
                //    }),
                //    method = request.Method.ToString(),
                //    uri = client.BuildUri(request),
                //};

                //logiRis.Add("Usuario:" + userName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                //logiRis.Add("Usuario:" + userName + "-Response:" + request.Resource + response.Content, "SEG");

                if (x.data == "EXITO")
                    return 1;
                return 0;
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al enviar el Token - " + ex.Message, "SEG");
                return -1;
            }
        }

        public static bool Verify2Fact(string apiUrl, string userName)
        {
            try
            {
                var url = apiUrl + "serviciosactivos/consulta/0";
                var client = new RestClient(url);
                var request = new RestRequest(url, RestSharp.Method.Post);
                var response = client.ExecutePost(request);
                dynamic x = JObject.Parse(response.Content);

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

                logiRis.Add("Usuario:" + userName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + userName + "-Response:" + request.Resource + response.Content, "SEG");

                if (x.resultados.Count > 0)
                    return true;
            }
            catch (Exception ex)
            {
                logiRis.Add("Error al consultar Token activo - " + ex.Message, "SEG");
            }

            return false;
        }

        public static string GetSign(string apiUrl, string userName, string body)
        {
            try
            {
                var url = apiUrl + "/publico/firmar";
                var client = new RestClient(url);
                var request = new RestRequest(url, RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.AddJsonBody(new { body });

                var response = client.ExecutePost(request);

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

                logiRis.Add("Usuario:" + userName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + userName + "-Response:" + request.Resource + response.Content, "SEG");

                if (response.IsSuccessful)
                    return response.Content;
                return "";

            }
            catch (Exception ex)
            {
                logiRis.Add("Error al enviar el Token - " + ex.Message, "SEG");
                return "";
            }
        }
    }
}
