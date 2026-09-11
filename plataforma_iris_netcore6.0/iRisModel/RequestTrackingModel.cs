using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class RequestTrackingModel
    {
        public string guia { get; set; } = "";
    }

    public class ResponseTrackingModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public DataTracking data { get; set; }
    }
    public class DataTracking
    {
        public List<string> tarjetas { get; set; }
        public string estatus { get; set; }
        public string ubicacion { get; set; }
        public string destino { get; set; }
        public string sucursal { get; set; }
        public string fechaLastEvent { get; set; }
        public string lastEvent { get; set; }
        public string fechaEntrega { get; set; }
        public string recibio { get; set; }
    }

    
}
