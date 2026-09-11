namespace iRisCore.Models
{
    public class PermissionUserModel
    {
        public int id { get; set; }
        public string? seestatus { get; set; }
        public int? usuarioCreacion { get; set; }
        public long fechaCreacion { get; set; }
        public int? usuarioModificacion { get; set; }
        public long? fechaModificacion { get; set; }
        public string? nombre { get; set; }
        public string? descripcion { get; set; }
    }

    public class PermissionQuery
    {
        public int? idAplicativo { get; set; }
        public List<PermissionUserModel> transacciones { get; set; }
    }
}
