using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class MovimientoManualModel
    {
        public RequestHeaderModel header { get; set; }
        public string tarjeta { get; set; }
        public double importe { get; set; }
        public string claveMovimiento { get; set; }
        public string observaciones { get; set; }
        public string refNumerica { get; set; }
        public string conceptoPago { get; set; }
        public string token { get; set; }
    }

    public class MovimientoManualResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string detalle { get; set; }
        public string IDSolicitud { get; set; }
        public string CodRespuesta { get; set; }
        public string DescRespuesta { get; set; }
        public string ClaveMovimiento { get; set; }
        public string Tarjeta { get; set; }
        public string MedioAcceso { get; set; }
        public double Importe { get; set; }
        public double SaldoFinal { get; set; }
        public DateTime Fecha { get; set; }
        public string Referencia { get; set; }
        public int MovimientoId { get; set; }
    }


    public class CatalogoMovimientoManual
    {
        public string claveMovimiento { get; set; }
        public string descripcionMovimiento { get; set; }
        public string tipoMovimiento { get; set; }
    }


}
