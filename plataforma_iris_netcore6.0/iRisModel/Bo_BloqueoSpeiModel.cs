using iRisModel;

namespace iRisCore.Models
{
    public class BloqueoSpeiModel
    {
        public RequestHeaderModel header { get; set; }

        public string idPblu { get; set; }

        public string accion { get; set; } //se bloquea o desbloquea

        public string descripcion { get; set; }

        public string tipoOperacion { get; set; }

        public string usuarioCreacion { get; set; }

    }

    public class BloqueoSpeiResponse
    {
        public string eiyuResponseCode { get; set; }

        public string descripcion { get; set; }

        public BloqueoCuentaIndContentResponse content { get; set; }

    }
}