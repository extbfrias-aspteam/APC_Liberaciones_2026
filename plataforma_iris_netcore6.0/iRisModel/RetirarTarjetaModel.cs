using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class RetirarTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjeta { get; set; } = "";
        public string tipoMedioAcceso { get; set; } = "";
        public string medioAcceso { get; set; } = "";
        public double importe { get; set; }
        public string referenciaNumerica { get; set; }
        public string observaciones { get; set; }
    }
    public class RetirarTarjetaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; } = "";
        public string data { get; set; } = "";
        public string IDSolicitud { get; set; } = "";
        public string Tarjeta { get; set; } = "";
        public double Importe { get; set; }
        public double SaldoFinal { get; set; }
        public string CodRespuesta { get; set; } = "";
        public string DescRespuesta { get; set; } = "";
        public int movimientoId { get; set; }
    }
}
