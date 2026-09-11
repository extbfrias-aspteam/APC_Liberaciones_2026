using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class DenegacionesModel
    {
        public RequestHeaderModel header { get; set; }
        public string tarjeta { get; set; }
        public string tipoMedioAcceso { get; set; }
        public string medioAcceso { get; set; }
        public string fechaInicial { get; set; }
        public string fechaFinal { get; set; }
    }
}
