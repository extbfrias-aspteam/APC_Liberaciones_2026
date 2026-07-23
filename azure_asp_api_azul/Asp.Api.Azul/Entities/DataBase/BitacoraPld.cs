namespace Asp.Api.Azul.Entities.DataBase
{
    public class BitacoraPld
    {
        public long Id { get; set; }
        public string? rfc { get; set; }
        public string? curp { get; set; }
        public int? id_pblu { get; set; }
        public string? request_body { get; set; }
        public string? error_message { get; set; }
        public DateTime fecha_creacion { get; set; }
    }
}
