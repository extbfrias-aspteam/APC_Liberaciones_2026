using iRisCore.Tools;
using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace iRisCore.Controllers
{
    public class MonitorSpeiController : Controller
    {
        private readonly IConfiguration _config;
        public MonitorSpeiController(IConfiguration config)
        {
            _config = config;
        }
        public IActionResult MonitorSpei()
        {
            return View();
        }


        [Authorize]
        [HttpPost]
        public async Task<IActionResult> DashBoardSpeiIn()
        {
            TotalSpeisResponse objSpeiIn = await Dashboards.GetDashboardSpeiIN(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            return Json(objSpeiIn);
        }

        [Authorize]
        [HttpPost]
        public async Task<IActionResult> DashBoardSpeiOut()
        {
            TotalSpeisResponse objSpeiOut = await Dashboards.GetDashboardSpeiOut(_config["EndPointsCore:apiURLBusquedas"], _config["EndPointsCore:userASP"], _config["EndPointsCore:passASP"]);
            return Json(objSpeiOut);
        }
    }
}
