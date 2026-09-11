using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class SolicitarReposicionModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjeta { get; set; }
        public string nombreEmbozado { get; set; }
        public string razonReposicion { get; set; }
    }

    public class SolicitarReposicionResponse 
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
        public string tarjetaNueva { get; set; }

    }
}
