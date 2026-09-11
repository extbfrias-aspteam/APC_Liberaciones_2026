using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public  class TraspasoTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjetaOrigen { get; set; }
        public string numeroTarjetaDestino { get; set; }
        public double importe { get; set; }
        public string refNumerica { get; set; }
        public string observaciones { get; set; }
    }


    public class TraspasoTarjetaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }

        public string IDSolicitud { get; set; }
        public string ClaveMovimiento { get; set; }
        public string Tarjeta { get; set; }
        public string MedioAcceso { get; set; }
        public double Importe { get; set; }
        public double SaldoFinal { get; set; }
        public DateTime Fecha { get; set; }
        public string Referencia { get; set; }
        public string CodRespuesta { get; set; }
        public string DescRespuesta { get; set; }
    }
}
