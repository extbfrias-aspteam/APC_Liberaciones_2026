using Microsoft.AspNetCore.Mvc;

namespace iRisCore.Controllers
{
    public class HomeBOController : Controller
    {
        private readonly IConfiguration _config;
        public HomeBOController(IConfiguration config)
        {
            _config = config;
        }
        public IActionResult Index()
        {
            return View();
        }
    }
}
