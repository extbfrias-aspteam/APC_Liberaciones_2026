using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class CodigoSeguridadModel
    {
        public RequestHeaderModel header { get; set; }
        public string? tipoReenvio { get; set; }
        public string? correoElectronico { get; set; }
        public string? telefono { get; set; }
    }
}
