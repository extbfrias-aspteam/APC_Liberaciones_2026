using iRisCore.Data;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace iRisCore.Controllers
{
    public class DashboardClienteOnePageController : Controller
    {
        [HttpGet]
        //[Authorize]
        public IActionResult DashboardClienteOnePage()
        {
            ViewBag.TipoCliente = DALOnePageClientes.GetTipoClienteSpei();

            ViewBag.Idioma = DALOnePageClientes.GetIdioma();

            return View();
        }

        [HttpPost]
        //[Authorize]
        public async Task<IActionResult> getClientesB2B()
        {
            List<ClienteB2BModel> lstClientes = DALOnePageClientes.GetClienteB2B();
            return Json(lstClientes);
        }

        //[Authorize]
        [HttpGet]
        public async Task<IActionResult> DashboardB2B(int id)
        {
            ViewBag.Nombre = "Aqui va el DS";
            return View();
        }

        public async Task<IActionResult> SaveClienteB2B(ClienteB2BModel model)
        {
            ClienteB2BModel objSet = DALOnePageClientes.SetClienteB2B(model);

            return Json(objSet);
        }
    }
}
