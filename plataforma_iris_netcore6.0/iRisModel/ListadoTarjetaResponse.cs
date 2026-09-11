using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ListadoTarjetaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string detalle { get; set; }

        public int? totalTFActiva { get; set; }
        public int? totalTFBloqueada { get; set; }
        public int? totalTFCancelada { get; set; }
        public int? totalTFSinAsignar { get; set; }

        public int? totalTVActiva { get; set; }
        public int? totalTVBloqueada { get; set; }
        public int? totalTVCancelada { get; set; }
        public int? totalTVSinAsignar { get; set; }

    }

    public class ListadoTarjetaDetalle
    {
        public string pan { get; set; }
        public string estatus { get; set; }
        public string tipoTarjeta { get; set; }
        public string cuenta { get; set; }
        public int claveEmpresa { get; set; }
        public string nombreCorto { get; set; }
    }

}
