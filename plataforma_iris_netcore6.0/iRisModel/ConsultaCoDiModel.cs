using System;
using System.Collections.Generic;
using System.Linq;
using System.Security;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaCoDiModel
    {
        public string fechaInicio { get; set; }
        public string fechaFin { get; set; }

    }

    public class ResponseConsultaCoDi
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class ListadoOperacionesCoDi
    {
        public string nombreEmisor { get; set; }
        public string cuentaOrigen { get; set; }
        public string bancoOrigen { get; set; }
        public string nombreBeneficiario { get; set; }
        public string cuentaDestino { get; set; }
        public string bancoDestino { get; set; }
        public double monto { get; set; }
        public string estatus { get; set; }
        public string descripcionMovimiento { get; set; }
        public string claveRastreo { get; set; }
        public string referencia { get; set; }
        public string fechaAplicacion { get; set; }
        public string fechaOperacion { get; set; }
        public string tipoOperacion { get; set; }
    }

}
