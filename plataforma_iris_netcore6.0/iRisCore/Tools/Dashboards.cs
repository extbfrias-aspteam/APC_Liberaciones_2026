using iRisModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System.Reflection;
using System.Web.Helpers;

namespace iRisCore.Tools
{
    public class Dashboards
    {
        public static DashboardCuentasDetalle GetDashboardCuentas(string URLCard, string userASP, string pwdASP, string productoId)
        {
            string year = DateTime.Parse(DateTime.Now.ToString()).Year.ToString();

            DashboardCuentasResponse resultado = new DashboardCuentasResponse();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            DashboardCuentasModel model = new DashboardCuentasModel { anio = int.Parse(year), productoId = int.Parse(productoId) };

            var request = new RestRequest("/dashboardCuentas", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);
            var response = client.ExecutePost(request);

            dynamic datos = JObject.Parse(response.Content);
            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.detalle = datos.data;
            DashboardCuentasDetalle da = JsonConvert.DeserializeObject<DashboardCuentasDetalle>(resultado.detalle);
            return da;
        }

        public static ListadoTarjetaResponse GetDashboardEstatusTarjeta(string URLCard, string userASP, string pwdASP, string empresaId)
        {
            ListadoTarjetaResponse resultado = new ListadoTarjetaResponse();

            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);


            var request = new RestRequest("/listadoTarjetas", RestSharp.Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);
            request.AddParameter("empresaId", empresaId);

            var response = client.ExecuteGet(request);

            dynamic datos = JObject.Parse(response.Content);
            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.detalle = datos.data;
            List<ListadoTarjetaDetalle> da = JsonConvert.DeserializeObject<List<ListadoTarjetaDetalle>>(resultado.detalle);

            resultado.totalTFSinAsignar = da.Where(x => x.tipoTarjeta == "FISICA" && x.estatus == "SIN ASIGNAR").Count(); ;
            resultado.totalTFActiva = da.Where(x => x.tipoTarjeta == "FISICA" && x.estatus == "ACTIVADA").Count();
            resultado.totalTFBloqueada = da.Where(x => x.tipoTarjeta == "FISICA" && x.estatus == "BLOQUEADA").Count();
            resultado.totalTFCancelada = da.Where(x => x.tipoTarjeta == "FISICA" && x.estatus == "CANCELADA").Count();

            resultado.totalTVSinAsignar = da.Where(x => x.tipoTarjeta == "VIRTUAL" && x.estatus == "SIN ASIGNAR").Count(); ;
            resultado.totalTVActiva = da.Where(x => x.tipoTarjeta == "VIRTUAL" && x.estatus == "ACTIVADA").Count();
            resultado.totalTVBloqueada = da.Where(x => x.tipoTarjeta == "VIRTUAL" && x.estatus == "BLOQUEADA").Count();
            resultado.totalTVCancelada = da.Where(x => x.tipoTarjeta == "VIRTUAL" && x.estatus == "CANCELADA").Count();

            return resultado;
        }

        public static async Task<TotalSpeisResponse> GetDashboardSpeiIN(string URLCard, string userASP, string pwdASP)
        {

            TotalSpeisResponse resultado = new TotalSpeisResponse();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            DashboardSpeiModel model = new DashboardSpeiModel { fecha = DateTime.Now.ToString("yyyy-MM-dd"), tipoSpei = "IN", cveRastreo = null };
            //DashboardSpeiModel model = new DashboardSpeiModel { fecha = "2022-12-03", tipoSpei = "IN", cveRastreo = null };

            var request = new RestRequest("/consultaSpei", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);
            var response = await client.ExecutePostAsync(request);

            dynamic datos = JObject.Parse(response.Content);

            ConsultaSpeiResponse result = JsonConvert.DeserializeObject<ConsultaSpeiResponse>(response.Content);

            resultado.detalleSpei = result;
            
            List<string> lstEstatus = result.data.Select(x => x.descripcionEstatus).ToList();

            string[] estatusSpeiIn = lstEstatus.Distinct().ToArray();

            foreach (string s in estatusSpeiIn)
            {
                TotalEstatusSpei obj = new TotalEstatusSpei()
                {
                    estatusSpei = s,
                    totalSpei = result.data.Where(x => x.descripcionEstatus == s).Count()
                };
                resultado.totalSpeis.Add(obj);
            }


            return resultado;
        }

        public static async Task<TotalSpeisResponse> GetDashboardSpeiOut(string URLCard, string userASP, string pwdASP)
        {

            TotalSpeisResponse resultado = new TotalSpeisResponse();

            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            DashboardSpeiModel model = new DashboardSpeiModel { fecha = DateTime.Now.ToString("yyyy-MM-dd"), tipoSpei = "OUT", cveRastreo = null };
            //DashboardSpeiModel model = new DashboardSpeiModel { fecha = "2022-12-03", tipoSpei = "OUT", cveRastreo = null };

            var request = new RestRequest("/consultaSpei", RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(model);
            var response = await client.ExecutePostAsync(request);

            ConsultaSpeiResponse result = JsonConvert.DeserializeObject<ConsultaSpeiResponse>(response.Content);

            resultado.detalleSpei = result;

            List<string> lstEstatus = result.data.Select(x => x.descripcionEstatus).ToList();

            string[] estatusSpeiIn = lstEstatus.Distinct().ToArray();

            foreach (string s in estatusSpeiIn)
            {
                TotalEstatusSpei obj = new TotalEstatusSpei()
                {
                    estatusSpei = s,
                    totalSpei = result.data.Where(x => x.descripcionEstatus == s).Count()
                };
                resultado.totalSpeis.Add(obj);
            }

            return resultado;
        }

    }
}
