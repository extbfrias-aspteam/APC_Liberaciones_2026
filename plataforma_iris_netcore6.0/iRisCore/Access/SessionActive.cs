using iRisModel;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using RestSharp.Authenticators;
using RestSharp;
using DocumentFormat.OpenXml.EMMA;
using DocumentFormat.OpenXml.Spreadsheet;
using System.Security.Claims;
using System.Reflection;
using iRisLog;

namespace iRisCore.Access
{
    public class SessionActive
    {
        public static SesionesModel GetSesionesActiva(string URLCard, string userASP, string pwdASP, int aplicativoId, string usuarioiRis)
        {
            try
            {
                logiRis.Add("Usuario:" + usuarioiRis + "- Initial Proceess Log In - Active Sessions", "SEG");

                SesionesModel resultado = new SesionesModel();

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/buscarSesionesActivas", Method.Get);
                request.AddHeader("Content-Type", "application/json");
                var body = @"";
                request.AddParameter("text/plain", body, ParameterType.RequestBody);
                request.AddParameter("idAplicativo", aplicativoId);
                request.AddParameter("usuario", usuarioiRis);

                var response = client.ExecuteGet(request);
                dynamic datos = JObject.Parse(response.Content);

                resultado.codigo = datos.codigo;
                resultado.mensaje = datos.mensaje;
                resultado.data = datos.data;

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

                logiRis.Add("Usuario:" + usuarioiRis + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + usuarioiRis + "-Response:" + request.Resource + response.Content, "SEG");

                return resultado;
            }
            catch (Exception ex)
            {
                logiRis.Add("Usuario:" + usuarioiRis + "- Initial Proceess Log In - Active Sessions" + ex.ToString(), "SEG");
                return null;
            }
        }

        public static void CerrarSesionesActivas(string URLCard, string userASP, string pwdASP, SesionesModel model)
        {
            try
            {
                logiRis.Add("Usuario:" + model.usuario + "- Initial Proceess Log In - Close Sessions", "SEG");

                var options = new RestClientOptions(URLCard)
                {
                    Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
                };
                var client = new RestClient(options);

                var request = new RestRequest("/cerrarSesionesActivas", RestSharp.Method.Post);
                request.AddHeader("Content-Type", "application/json");
                request.RequestFormat = DataFormat.Json;
                request.AddJsonBody(model);
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

                logiRis.Add("Usuario:" + model.usuario + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + model.usuario + "-Response:" + request.Resource + response.Content, "SEG");

            }
            catch (Exception e)
            {
                logiRis.Add("Usuario:" + model.usuario + "- Initial Proceess Log In - Close Sessions" + e.ToString(), "SEG");

            }

        }

    }
}
