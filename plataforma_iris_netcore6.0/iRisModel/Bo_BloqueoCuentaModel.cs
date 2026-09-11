using iRisModel;

namespace iRisCore.Models
{
    public class BloqueoCuentaIndModel
    {
        public RequestHeaderModel header { get; set; }

        public string clabe { get; set; }

        public int estatusCuenta { get; set; }

        public string causa { get; set; }

        public string tipoSpei { get; set; }

        public string usuarioCreacion { get; set; }

    }

    public class BloqueoCuentaIndResponse
    {
        public string eiyuResponseCode { get; set; }

        public string mensaje { get; set; }

        public BloqueoCuentaIndContentResponse content { get; set; }

    }

    public class BloqueoCuentaIndContentResponse
    {
        public string clabe { get; set; }

        public int estatusCuenta { get; set; }

        public string causa { get; set; }

        public string tipoSpei { get; set; }

        public string fechaCreacion { get; set; }

        public string fechaModificacion { get; set; }

        public string usuarioCreacion { get; set; }

        public string? usuarioModificacion { get; set; }
    }

    public class ClabesNoAceptadas
    {
        public string clabe { get; set; }

        public string motivo { get; set; }
    }
}
