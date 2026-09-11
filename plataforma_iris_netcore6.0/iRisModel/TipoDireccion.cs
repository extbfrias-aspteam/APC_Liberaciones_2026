using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaTipoDireccionResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; } // Mantenemos como string para deserializar después
    }

    public class ConsultaTipoDireccionDetalle
    {
        public int id { get; set; }
        public int clave { get; set; }
        public string descripcion { get; set; }
        public string abreviacion { get; set; }
    }
}
