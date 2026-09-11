using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace iRisCore.Filters
{
    public class ConfiguracionSesionFilter : IActionFilter
    {
        private readonly IConfiguration _config;

        public ConfiguracionSesionFilter(IConfiguration config)
        {
            _config = config;
        }

        public void OnActionExecuting(ActionExecutingContext context)
        {
            var controller = context.Controller as Controller;
            if (controller != null)
            {
                var minutos = _config.GetValue<int>("Miscelanius:tiempoInactividadMinutos");
                controller.ViewBag.MilisegundosTimeout = minutos * 60 * 1000;
            }
        }

        public void OnActionExecuted(ActionExecutedContext context) { }
    }
}