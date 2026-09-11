using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.NetworkInformation;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class DashboardCuentasModel
    {
        public int anio { get; set; }
        public int productoId { get; set; }
    }
    public class DashboardCuentasResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string detalle { get; set; }

    }
    public class DashboardCuentasDetalle
    {
        public List<ConcentradoActualCuenta> concentradoActual { get; set; }
        public List<DetalleCuenta> detalladoAnual { get; set; }
    }

    public class ConcentradoActualCuenta
    {
        public string? estatus { get; set; }
        public int? total { get; set; }
    }

    public class DetalleCuenta
    {
        public string? mes { get; set; }
        public List<DetalleCuentaEstatus> estatusCuenta { get; set; }
    }

    public class DetalleCuentaEstatus
    {
        public string? estatus { get; set; }
        public int cantidad { get; set; }
    }

}
