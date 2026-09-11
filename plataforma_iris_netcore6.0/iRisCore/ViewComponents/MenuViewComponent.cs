using iRisCore.Access;
using iRisCore.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using RestSharp;
using System.Web.Helpers;
using System.Security.Claims;
using Newtonsoft.Json.Linq;
using System.Configuration;
using iRisCore.Session;

namespace iRisCore.ViewComponents
{
    public class MenuViewComponent:ViewComponent
    {
        private readonly IConfiguration _config;
        public MenuViewComponent(IConfiguration Config) 
        { 
            _config = Config;
        }

        public async Task<IViewComponentResult> InvokeAsync()
        {
            var identity = (ClaimsIdentity)User.Identity;

            string returnUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.NameIdentifier)?.Value;

            //string ipUser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Uri)?.Value;
            //string iduser = identity.Claims.FirstOrDefault(c => c.Type == ClaimTypes.UserData)?.Value;


            dynamic menus = GetMenu(returnUser);

            List<MenuModel> oMenus = new List<MenuModel>();

            foreach(var menu in menus) 
            {
                string valMenu = menu.descripcion;
                string[] itemMenu = valMenu.Split('|');
                if (itemMenu.GetLength(0) == 2)
                {
                    oMenus.Add (new MenuModel { 
                        descripcion = itemMenu[0],
                        clase = itemMenu[1],
                        id = menu.id,
                        clave = menu.clave,
                        url = menu.url,
                        setransaccionNombre = menu.setransaccionNombre,
                        usuarioCreacion = menu.usuarioCreacion,
                        usuarioModificacion = menu.usuarioModificacion,
                        estatus = menu.estatus,
                        idAplicativo = menu.idAplicativo
                    });
                }
                else
                {
                    oMenus.Add(
                        new MenuModel { 
                            descripcion = itemMenu[0],
                            clase = "",
                            id = menu.id,
                            clave = menu.clave,
                            url = menu.url,
                            setransaccionNombre = menu.setransaccionNombre,
                            usuarioCreacion = menu.usuarioCreacion,
                            usuarioModificacion = menu.usuarioModificacion,
                            estatus = menu.estatus,
                            idAplicativo = menu.idAplicativo
                    });
                }
            }


            List<MenuModel> Lmodulos = (from a in oMenus where a.clave == "" select a).ToList();
            List<MenuModel> Lsubmodulos = (from b in oMenus where b.clave != "" orderby b.id select b).ToList();

            ViewBag.Lmodulos = Lmodulos;
            ViewBag.Lsubmodulos = Lsubmodulos;

            return View();
        }

        #region "MENU"

        private dynamic GetMenu(string UserName)
        {
            int sucursal = 0;
            var URLSecurity = _config["EndPointsCore:apiURL"];
            var url = URLSecurity + "/permisos/consulta/" + UserName;
            var client = new RestClient(url);
            var request = new RestRequest(url, RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(@"");
            var response = client.ExecutePost(request);
            dynamic data = JObject.Parse(response.Content);

            if (data.seTransaccionResponse.codeEstatus == "SUCCESS")
            {
                dynamic usuarios = data.usuario;
                sucursal = usuarios.idSucursal;
                HttpContext.Session.SetInt32("sucursalUsuario", sucursal);
                dynamic Grants = GetPermisos(data.seTransaccionResponse.resultados);               

                return Grants;
            }

            return data.seTransaccionResponse.resultados;
        }

        private dynamic GetPermisos(dynamic Resultados)
        {
            //var aplicativo = _config["Miscelanius:ApplicationID"];

            var aplicativo = HttpContext.Session.GetInt32("plataformaUsuario");

            List<PermissionUserModel> oPermisos = new List<PermissionUserModel>();

            foreach (var item in Resultados)
            {
                oPermisos.Add(new PermissionUserModel { id= item.id });
                oPermisos.Add(new PermissionUserModel { seestatus = item.seestatus });
                oPermisos.Add(new PermissionUserModel { usuarioCreacion = item.usuarioCreacion });
                oPermisos.Add(new PermissionUserModel { fechaCreacion = item.fechaCreacion });
                oPermisos.Add(new PermissionUserModel { usuarioModificacion = item.usuarioModificacion });
                oPermisos.Add(new PermissionUserModel { fechaModificacion = item.fechaModificacion });
                oPermisos.Add(new PermissionUserModel { nombre = item.nombre });
                oPermisos.Add(new PermissionUserModel { descripcion = item.descripcion});
            }

            PermissionQuery pMQ = new PermissionQuery();
            //pMQ.idAplicativo = Int32.Parse(aplicativo); //- Cambiarlo por el parametro

            pMQ.idAplicativo = aplicativo; //- Cambiarlo por el parametro
            pMQ.transacciones = oPermisos;

            var URLSecurity = _config["EndPointsCore:apiURL"];

            var url = URLSecurity + "/menus/consulta";
            var client = new RestClient(url);
            var request = new RestRequest(url, RestSharp.Method.Post);
            request.AddHeader("Content-Type", "application/json");
            request.RequestFormat = DataFormat.Json;
            request.AddJsonBody(pMQ);
            var response = client.ExecutePost(request);
            dynamic data = JObject.Parse(response.Content);

            return data.resultados;
        }

        #endregion "MENU"

    }
}
