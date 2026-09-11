using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConcentradoMovimientoModel
    {
        public RequestHeaderModel header { get; set; }
        public string fechaInicio { get; set; }
        public string fechaFin { get; set; }       
        public string numeroTelefono { get; set; }
        public string cuentaASP { get; set; }
    }

    public class ConcentradoMovimientoResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class ConcentradoMovimientoHeaderResponse
    {
        public int id { get; set; }
        public string titulo { get; set; } = "";
        public string descripcion { get; set; } = "";
        public string concepto { get; set; } = "";
        public double monto { get; set; } = 0.00;
        public string fecha { get; set; } = "";
        public string tipo { get; set; } = "";
        public ConcentradoMovimientoMovtoDetailResponse detalleMovimiento { get; set; }

    }

    public class ConcentradoMovimientoMovtoDetailResponse
    {
        public string emisor { get; set; } = "";
        public string beneficiario { get; set; } = "";
        public string origen { get; set; } = "";
        public string destino { get; set; } = "";
        public string bancoOrigen { get; set; } = "";
        public string bancoDestino { get; set; } = "";
        public List<ConcentradoMovimientosDetailResponse> detalle { get; set; }
    }

    public class ConcentradoMovimientosDetailResponse
    {
        public string nombre { get; set; } = "";
        public string tipoDato { get; set; } = "";
        public string valor { get; set; } = "";
    }
}
