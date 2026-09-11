using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class NIPModel
    {
        public RequestHeaderModel header { get; set; }
        public string numeroTarjeta { get; set; }
        public string nip { get; set; }
    }
    public class NIPResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }
}
