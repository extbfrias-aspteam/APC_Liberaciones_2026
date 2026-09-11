using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class NivelCuentaModel
    {
        public string cuentaAsp { get; set; }
        public string nivelCuenta { get; set; }
    }

    public class NivelCuentaResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }
    public class DetalleNiveles
    {
        public int? id { get; set; }
        public string? clave { get; set; }
        public string? monto { get; set; }
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }


}
