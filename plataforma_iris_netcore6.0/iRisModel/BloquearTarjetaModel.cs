using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BloquearTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjeta { get; set; }
        public string motivoBloqueo { get; set; }
        public string token { get; set; }
    }

    public class BloquearTarjetaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }
}
