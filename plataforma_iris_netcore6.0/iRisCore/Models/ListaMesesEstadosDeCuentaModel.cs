using Newtonsoft.Json.Linq;

namespace iRisCore.Models
{
    public class MyDataMesResponse
    {
        public string EiyuResponseCode { get; set; }
        public string Descripcion { get; set; }
        public JObject Content { get; set; }

    }
    public class ListaMesesEstadosDeCuentaModel
    {
        public string? Enero{ get; set; }
        public string? Febrero { get; set; }

        public string? Marzo { get; set; }

        public string? Abril { get; set; }

        public string? Mayo { get; set; }

        public string? Junio { get; set; }

        public string? Julio { get; set; }

        public string? Agosto { get; set; }

        public string? Septiembre { get; set; }

        public string? Octubre { get; set; }

        public string? Noviembre { get; set; }

        public string? Diciembre { get; set; }

    }
}
