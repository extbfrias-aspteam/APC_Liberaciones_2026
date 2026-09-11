using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaSpeiModel
    {
        public string cveRastreo { get; set; } = "";
        public string fecha { get; set; } = "";
        public string? tipoSpei { get; set; } = "";
    }

    public class ConsultaSpeiResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public List<detalleSpei> data { get; set; } = new List<detalleSpei>();
    }

    public class detalleSpei
    {
        public string claveRastreo { get; set; } = "";
        public string bancoEmisor { get; set; } = "";
        public string bancoDestino { get; set; } = "";
        public string cuentaClabeOrigen { get; set; } = "";
        public string cuentaClabeDestino { get; set; } = "";
        public string nombreOrigen { get; set; } = "";
        public double monto { get; set; } = 0.0;     
        public string fechaOperacion { get; set; } = "";
        public int? estatusOperacion { get; set; } = 0;
        public string referenciaCobranza { get; set; } = "";
        public string nombreDestino { get; set; } = "";
        public string conceptoPago { get; set; } = "";
        public string nombreUdn { get; set; } = "";
        public string clabeUdn { get; set; } = "";
        public string descripcionEstatus { get; set; } = "";
        public string fechaAplicacion { get; set; } = "";
        public string descripcionDetallada { get; set; } = "";
        public string tipoSpei { get; set; } = "";
    }

    public class ParticipanteSaldos
    {
        public int Id_pblu { get; set; }
        public string Descripcion { get; set; }

        public decimal Saldo { get; set; }
    }
}
