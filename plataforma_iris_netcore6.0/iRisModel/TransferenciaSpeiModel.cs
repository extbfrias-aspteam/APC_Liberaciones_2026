using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.PortableExecutable;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class TransferenciaSpeiModel
    {
        public RequestHeaderModel header { get; set; }
        public string cuentaOrigen { get; set; }
        public string cuentaBeneficiaria { get; set; }
        public double monto { get; set; }
        public string nombreBenefSpeiDestino { get; set; }
        public string rfcBenefSpeiDestino { get; set; }
        public string conceptoPago { get; set; }
        public string correoBenefSpeiDestino { get; set; }
    }

    public class TransferenciaSpeiResponse
    {
        public int? codigo { get; set; }
        public string mensaje { get; set; }
        public string detalle { get; set; }
    }

    public class TransferenciaSpeiDetalleResponse
    {
        public int codigo { get; set; }
        public string cuentaOrigen { get; set; }
        public string cuentaBeneficiario { get; set; }
        public double monto { get; set; }
        public string bancoOrigen { get; set; }
        public string bancoDestino { get; set; }
        public string fechaOperacion { get; set; }
        public string claveRastreo { get; set; }
    }



}
