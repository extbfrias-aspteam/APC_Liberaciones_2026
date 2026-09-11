using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BloquearCuentaModel
    {
        public RequestHeaderModel header { get; set; }
        public string cuentaAsp { get; set; }
    }
    public class BloquearCuentaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }
}
