namespace iRisCore.Models
{
    public class MenuModel
    {
        public int id { get; set; }
        public string clave { get; set; }
        public string descripcion { get; set; }
        public string url { get; set; }
        public string setransaccionNombre { get; set; }
        public int? usuarioCreacion { get; set; }
        public long? fechaCreacion { get; set; }
        public int? usuarioModificacion { get; set; }
        public long? fechaModificacion { get; set; }
        public int? estatus { get; set; }
        public int? idAplicativo { get; set; }
        public string clase { get; set; }
    }
}
