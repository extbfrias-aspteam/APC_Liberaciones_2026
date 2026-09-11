using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class SesionesModel
    {
        public RequestHeaderModel header { get; set; }
        public int idAplicativo { get; set; }
        public string? usuario { get; set; }
        public int? codigo { get; set; }
        public string? mensaje { get; set; }
        public string? data { get; set; }

    }
}
