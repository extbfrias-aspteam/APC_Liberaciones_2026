using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class QueryModel
    {
        public string tipoBusqueda { get; set; }
        public string valorBusqueda { get; set; }

        public string claveProducto { get; set; }
        public string rfc { get; set; }
        public string curp { get; set; }
        public string telefono { get; set; }
        public string cuentaASP { get; set; }
        public string cuentaCLABE { get; set; }
        public string numeroTarjeta { get; set; }

        public string correo { get; set; }
    }

    public class ActualizaCurpRequest
    {
        public string telefono { get; set; }
        public string curp { get; set; }
        public string rfc { get; set; }
        public string usuarioId { get; set; }
        public string ip { get; set; }

    }

    public class ActualizaRfcRequest
    {
        public string telefono { get; set; }
        public string rfc { get; set; }
        public string usuarioId { get; set; }
        public string ip { get; set; }

    }

    public class ActualizaCurpRfcResponse
    {
        public string codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }

    }
}
