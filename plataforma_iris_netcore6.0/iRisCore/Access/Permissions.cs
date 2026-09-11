using Newtonsoft.Json.Linq;
using RestSharp;
using iRisCore.Models;

namespace iRisCore.Access
{
    public class Permissions
    {
        private readonly IConfiguration _config;
        public Permissions(IConfiguration Config)
        {
            _config = Config;
        }
        public bool ValidarPermiso(string user, int aplicativo, string permiso)
        {
            dynamic menus = GetPermissions(user, aplicativo);

            List<PermissionUserModel> oPermisos = new List<PermissionUserModel>();

            foreach (var item in menus)
            {
                oPermisos.Add(new PermissionUserModel { id = item.id });
                oPermisos.Add(new PermissionUserModel { seestatus = item.seestatus });
                oPermisos.Add(new PermissionUserModel { usuarioCreacion = item.usuarioCreacion });
                oPermisos.Add(new PermissionUserModel { fechaCreacion = item.fechaCreacion });
                oPermisos.Add(new PermissionUserModel { usuarioModificacion = item.usuarioModificacion });
                oPermisos.Add(new PermissionUserModel { fechaModificacion = item.fechaModificacion });
                oPermisos.Add(new PermissionUserModel { nombre = item.nombre });
                oPermisos.Add(new PermissionUserModel { descripcion = item.descripcion });
            }

            bool existe = oPermisos.Any(m => m.nombre == permiso);

            return existe;
        }
        private dynamic GetPermissions(string UserName, int aplicativo)
        {
            int sucursal = 0;
            var URLSecurity = _config["EndPointsCore:apiURL"];
            var url = URLSecurity + "/permisos/consulta/" + UserName;
            var client = new RestClient(url);
            var request = new RestRequest(url, Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            //request.AddJsonBody(@"");
            var response = client.ExecutePost(request);
            dynamic data = JObject.Parse(response.Content);
            if (!string.IsNullOrEmpty(response.Content))
                if (data.seTransaccionResponse.codeEstatus == "SUCCESS")
                    return data.seTransaccionResponse.resultados;

            return "";
        }
    }
}