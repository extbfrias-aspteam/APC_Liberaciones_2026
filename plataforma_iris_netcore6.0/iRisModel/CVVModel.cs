using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class CVVModel
    {
        public RequestHeaderModel header { get; set; }
        public string tarjeta { get; set; }
    }



    public class CVVResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
        public int IDSolicitud { get; set; }
        public string Tarjeta { get; set; }
        public string TipoMedioAcceso { get; set; }
        public string MedioAcceso { get; set; }
        public string ValorD2 { get; set; }
        public string CodRespuesta { get; set; }
        public string DescRespuesta { get; set; }
    }

    public class CVVDinamicoResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class CVVDinamicoDataResponse
    {
        public string NumeroTarjeta { get; set; }
        public string TipoDeMedioDeAcceso { get; set; }
        public string MedioDeAcceso { get; set; }
        public string CVV { get; set; }
        public string FechaYHoraDeExpiracion { get; set; }
        public string CodRespuesta { get; set; }
        public string DescRespuesta { get; set; }
    }
}
