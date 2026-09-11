using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class TotalSpeisResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string detalle { get; set; }
        public List<TotalEstatusSpei> totalSpeis { get; set; } = new List<TotalEstatusSpei>();

        public ConsultaSpeiResponse detalleSpei { get; set; }
    }

    public class TotalEstatusSpei
    {
        public string estatusSpei { get; set; }
        public int totalSpei { get; set; }
    }


    public class DashboardSpeiModel
    {
        public string fecha { get; set; }
        public string tipoSpei { get; set; }
        public string cveRastreo { get; set; }
    }

    public class DetalleSpei
    {
        public string bancoEmisor { get; set; }
        public string bancoDestino { get; set; }
        public string claveRastreo { get; set; }
        public string cuentaClabeOrigen { get; set; }
        public string cuentaClabeDestino { get; set; }
        public string nombreBeneficiario { get; set; }
        public string cliente { get; set; }
        public double monto { get; set; }
        public DateTime fechaOperacion { get; set; }
        public string estatusOperacion { get; set; }
        public string tipoSpei { get; set; }
    }
}
