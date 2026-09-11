using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BO_Alertas
    {
    }

    public class Alertas
    {
        public int idPblu { get; set; }
        public string claveRastreo { get; set; }
        public string clabe { get; set; }
        public string pbluClabe { get; set; }
        public string fechaClabe { get; set; }
        public string titularClabe { get; set; }
        public string idAlerta { get; set; }
        public decimal monto { get; set; }
        public string fechaCreacion{ get; set; }
        public string estatus { get; set; }
    }

    public class AlertasResponseWs
    {
        public string code { get; set; }
        public string mensaje { get; set; }
        public dynamic content { get; set; }
    }

    public class AlertasResponseFr
    {
        public int Codigo { get; set; }
        public string Mensaje { get; set; }
        public dynamic Data { get; set; }
    }

    public class ResolverAlertaRequest
    {
        public string claveRastreo { get; set; }
        public string nuevoEstado { get; set; }
        public string usuario { get; set; }
        public string resolucion { get; set; }
        public string detalles { get; set; }
    }

    public class AlertaRequest
    {
        public string idParticipante{ get; set; }
        public string clabe { get; set; }
        public string claveRastreo { get; set; }
        
    }
}
