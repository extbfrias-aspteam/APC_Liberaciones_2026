using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ReasignarTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjetaAnterior { get; set; }
        public string numeroTarjetaNueva { get; set; }

        public string token { get; set; }
    }


    public class ReasignarTarjetaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
        public string tarjetaNueva { get; set; }
    }
}
