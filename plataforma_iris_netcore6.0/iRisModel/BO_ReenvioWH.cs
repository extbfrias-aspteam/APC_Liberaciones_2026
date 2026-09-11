using iRisModel;

namespace iRisCore.Models
{
    public class BO_ReenvioWH
    {

    }

    public class ReenvioWHRequest
    {
        public int idPblu { get; set; }
        public string cveRastreo { get; set; }
        public string tipoOperacion { get; set; }
    }
    public class ReenvioWHResponse
    {
        public string EiyuResponseCode { get; set; }
        public string Content { get; set; }
    }
    public class ReenvioWHResponseItem : ReenvioWHRequest
    {
        public ReenvioWHResponse Response { get; set; }
    }

    public class NotificacionesWH
    {
        public int id { get; set; }
        public string clave { get; set; }
        public string tipoOperacion { get; set; }
        public string jsonRequest { get; set; }
        public string jsonResponse { get; set; }
        public string monto { get; set; }
        public string respuesta { get; set; }
        public bool estatus { get; set; }
    }

    public class NotificacionesWHRequest
    {
        public int idPblu { get; set; }
        public string tipoOperacion { get; set; }
        public string fchInicio { get; set; }
        public string fchFin { get; set; }
        public string estatus { get; set; }

    }

    public class NotificacionesWHResponse
    {
        public string EiyuResponseCode { get; set; }
        public string Descripcion { get; set; }
        public dynamic Content { get; set; }
    }

    public class ResponseWH
    {
        public int Codigo { get; set; }
        public string Mensaje { get; set; }
        public dynamic Data { get; set; }
    }
}
