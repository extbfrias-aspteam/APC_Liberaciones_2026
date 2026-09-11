using iRisModel;

namespace iRisCore.Models
{

    public class FirmanteUpdateDto
    {
        public int IdFirmante { get; set; }
        public string NombreFirmante { get; set; }
        public string Usuario { get; set; }
        public string Puesto { get; set; }
        public bool Sigmatario { get; set; }
    }
    public class ApiResponse
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public FirmanteContent Content { get; set; }
    }

    public class FirmanteContent
    {
        public int IdFirmante { get; set; }
        public string Nombre { get; set; }
        public string Puesto { get; set; }
        public bool EsSignatario { get; set; }
        public string UsuarioCreacion { get; set; }
        public DateTime FechaCreacion { get; set; }
        public string UsuarioModificacion { get; set; }
        public DateTime FechaModificacion { get; set; }
    }
    public class FirmanteResponse1
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<Firmante1> Content { get; set; }
    }
    public class FirmanteUpdateRequest
    {
        public int IdFirmante { get; set; }
        public string NombreFirmante { get; set; }
        public string Usuario { get; set; }
        public string Puesto { get; set; }
        public bool Sigmatario { get; set; }
    }
    public class FirmanteDTO
    {
        public int? IdFirmante { get; set; }  // Este campo puede ser nulo si es un nuevo firmante
        public string NombreFirmante { get; set; }
        public string CargoPuesto { get; set; }
        public bool Signatario { get; set; }
        public string Usuario { get; set; }
    }
    public class Firmante1
    {
        public int IdFirmante { get; set; }
        public string Nombre { get; set; }
        public string Puesto { get; set; }
        public bool EsSignatario { get; set; }
        public string UsuarioCreacion { get; set; }
        public string FechaCreacion { get; set; }
        public string UsuarioModificacion { get; set; }
        public string FechaModificacion { get; set; }
    }




}


