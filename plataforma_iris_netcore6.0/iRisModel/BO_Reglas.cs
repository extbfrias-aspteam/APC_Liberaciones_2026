using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BO_Reglas
    {

    }

    public class Reglas
    {
        public int idRegla { get; set; }
        public string nombreRegla { get; set; }
        public string estatus { get; set; }
        public string fechaAplicacion { get; set; }
        public string usuario { get; set; }
    }

    public class ReglasResponseWs
    {
        public string code { get; set; }
        public string mensaje { get; set; }
        public dynamic content { get; set; }
    }

    public class ReglasResponseFr
    {
        public int Codigo { get; set; }
        public string Mensaje { get; set; }
        public dynamic Data { get; set; }
    }

    public class ConfigsRequest
    {
        public int idPblu { get; set; }
        public string estatus { get; set; }
    }

    public class ConfReglas
    {
        public int idPendiente { get; set; }
        public int idParticipante { get; set; }
        public string nombre { get; set; }
        public string fechaCreacion { get; set; }
        public decimal monto { get; set; }
        public string estatus { get; set; }

    }

    public class ConfReglasRequest
    {
        public int idParticipante { get; set; }
        public decimal monto { get; set; }
        public string usuario { get; set; }
    }
    public class AuthConfRequest
    {
        public int idPendiente { get; set; }
        public string usuario { get; set; }
    }
}
