using iRisModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using RestSharp;
using RestSharp.Authenticators;
using System.Reflection;
using System.Web.Helpers;

namespace iRisCore.Tools
{
    public class Catalogos
    {
        public static List<CatalogoMovimientoManual> GetMovimientos(string URLCard, string userASP, string pwdASP)
        {
            MovimientoManualResponse resultado = new MovimientoManualResponse();

            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/consultaCatalogoMovimientos", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);
            resultado.detalle = datos.data;

            List<CatalogoMovimientoManual> da = JsonConvert.DeserializeObject<List<CatalogoMovimientoManual>>(resultado.detalle);

            return da;
        }
        public static List<DetalleNiveles> GetNiveles(string URLCard, string userASP, string pwdASP)
        {
            DetalleNiveles resultado = new DetalleNiveles();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/catalogoNivelCuenta", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<DetalleNiveles> da = JsonConvert.DeserializeObject<List<DetalleNiveles>>(resultado.data);

            return da;
        }
        public static List<EmpresaModel> GetEmpresa(string URLCard, string userASP, string pwdASP)
        {
            EmpresaModel resultado = new EmpresaModel();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/comboEmpresa", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);
            //request.AddParameter("empresaId", empresa);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<EmpresaModel> da = JsonConvert.DeserializeObject<List<EmpresaModel>>(resultado.data);

            return da;
        }
        public static CatalogoLogisticaModel GetCatalogoLogistica(string URLCard, string userASP, string pwdASP)
        {
            CatalogoLogisticaModel resultado = new CatalogoLogisticaModel();

            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/obtenerCatalogos", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);
            //request.AddParameter("empresaId", empresa);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            CatalogoLogisticaModel da = JsonConvert.DeserializeObject<CatalogoLogisticaModel>(resultado.data);

            return da;
        }

        public static List<DetalleReenvio> GetCatalogoReenvio(string URLCard, string userASP, string pwdASP)
        {
            DetalleReenvio resultado = new DetalleReenvio();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/consultarCatalogoReenvios", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<DetalleReenvio> da = JsonConvert.DeserializeObject<List<DetalleReenvio>>(resultado.data);

            return da;
        }

        public static List<ListadoCompanias> GetCompanias(string URLCard, string userASP, string pwdASP)
        {
            DetalleNiveles resultado = new DetalleNiveles();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/catalogoCompaniasTelefonicas", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<ListadoCompanias> da = JsonConvert.DeserializeObject<List<ListadoCompanias>>(resultado.data);

            return da;
        }

        public static List<ProductoModel> GetProducto(string URLCard, string userASP, string pwdASP)
        {
            ProductoModel resultado = new ProductoModel();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/productos", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);
            //request.AddParameter("empresaId", empresa);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<ProductoModel> da = JsonConvert.DeserializeObject<List<ProductoModel>>(resultado.data);

            return da;
        }

        public static List<ListadoProveedoresPagoServicio> GetProveedores(string URLCard, string userASP, string pwdASP)
        {
            DetalleNiveles resultado = new DetalleNiveles();


            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/proveedoresServicios", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);

            var response = client.ExecuteGet(request);
            dynamic datos = JObject.Parse(response.Content);

            resultado.codigo = datos.codigo;
            resultado.mensaje = datos.mensaje;
            resultado.data = datos.data;

            List<ListadoProveedoresPagoServicio> da = JsonConvert.DeserializeObject<List<ListadoProveedoresPagoServicio>>(resultado.data);

            return da;
        }


        public static ParentescoModel GetParentesco(string URLCard, string userASP, string pwdASP)
        {
            var options = new RestClientOptions(URLCard)
            {
                Authenticator = new HttpBasicAuthenticator(userASP, pwdASP)
            };
            var client = new RestClient(options);

            var request = new RestRequest("/catalogo-parentesco", Method.Get);
            request.AddHeader("Content-Type", "application/json");
            var body = @"";
            request.AddParameter("text/plain", body, ParameterType.RequestBody);            

            var response = client.ExecuteGet(request);

            ParentescoModel da = JsonConvert.DeserializeObject<ParentescoModel>(response.Content);

            return da;
        }

    }
}
