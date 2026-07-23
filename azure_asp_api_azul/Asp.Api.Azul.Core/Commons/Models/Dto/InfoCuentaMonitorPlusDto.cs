using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Asp.Api.Azul.Core.Commons.Models.Dto
{
    public class InfoCuentaMonitorPlusDto
    {
        public string ClabeOrigen { get; set; }
        public DateTime? FechaAltaClabe { get; set; }
        public string EstatusCuenta { get; set; }
        public string TitularNombre { get; set; }
    }
}
