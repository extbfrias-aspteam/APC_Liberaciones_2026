using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class AsignarMedioAccesoModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjeta { get; set; }
        public string numeroTarjetaExtra { get; set; }
    }

    public class AsignarMedioAccesoResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
        public string tarjetaExtra { get; set; }
    }
}
