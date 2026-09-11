using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaMovimientoTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string tipoMedioAcceso { get; set; }
        public string medioAcceso { get; set; }
        public string fechaInicial { get; set; }
        public string fechaFinal { get; set; }
        public string tipoTarjeta { get; set; }
        public string numeroTarjeta { get; set; }
        public string token { get; set; }
        public int maxMovimientos { get; set; }
    }
}
