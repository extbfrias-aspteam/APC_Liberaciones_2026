using iRisModel;

namespace iRisCore.Models
{




    public class PaticipantePi
    {
        public int idParticipantes { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string estatus { get; set; }

    }

    public class ConsultaRequestID
    {
        public DateTime FechaInicio { get; set; }
        public DateTime FechaActual { get; set; }
        public int id { get; set; }
    }
    public class ConsultaRequest222
    {
        public DateTime FechaInicio { get; set; }
        public DateTime FechaActual { get; set; }
    }
    public class FormlarioModal
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<PaticipantePi> Content { get; set; }
    }

    public class FormlarioParticpanteModal
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<ParticipanteModal> Content { get; set; }
    }
    public class FormlarioParticpanteId
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public ParticipanteModal Content { get; set; }
    }

    public class ParticipanteModal
    {
        public int idParticipantes { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string rfc { get; set; }
        public string fechaCelebracion { get; set; }
        public string inicioServicio { get; set; }
        public string terminoServicio { get; set; }
        public string duracionContrato { get; set; }
        public string tercia { get; set; }
        public string nomComerciaMarca { get; set; }
        public string proveedor { get; set; }
        public string nombreFirmante { get; set; }
        public string cargoPuesto { get; set; }


    }


    public class FormlarioParticpanteActu
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public ParticipanteModal Content { get; set; }
    }


    public class FormlarioResponse
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<ParticipantesCuentas> Content { get; set; }
    }

    public class ParticipantesCuentas
    {


        public int idParticipante { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string cuentasAsignadas { get; set; }




    }
    public class GrupoRequestGrup
    {
        public int Trimestre { get; set; }
        public List<ParticipanteGrupo> Participantes { get; set; }
    }

    public class ConsultaRequestTrimestre
    {

        public int Trimestre { get; set; }
    }

    public class ConsultaRequestTrimestreArchivo
    {

        public int Trimestre { get; set; }
        public string Tipo { get; set; }
    }
    public class ConsultaRequestTrimestreFrima
    {

        public int Trimestre { get; set; }
        public int estatus { get; set; }
    }


    public class GrupoRequest
    {
        public int Anio { get; set; }
        public int Periodo { get; set; }
        public string UsuarioCreacion { get; set; }
        public List<ParticipanteGrupo> Grupo { get; set; } // Lista de participantes
    }

    public class ParticipanteGrupo
    {
        public int IdParticipante { get; set; } // Cambié el tipo a int para reflejar tu ejemplo JSON
    }
    public class FormlarioResponseGrupo
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<ParticipantesCuentas> Content { get; set; }
    }


    public class FormlarioResponseGrupoFirmas
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<ParticipantesCuentasFirmas> Content { get; set; }
    }

    public class ParticipantesCuentasFirmas
    {


        public int idParticipantes { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string cuentasAsignadas { get; set; }
        public string estatus { get; set; }
        public string nombreFirmante { get; set; }
        public string CargoPuesto { get; set; }



    }

    public class GenerarArchivoRequestFormulario
    {
        public List<int> idsParticipantes { get; set; }
        public string tipoArchivo { get; set; }
    }

    public class arregloParticipantes
    {
        public List<int> idsParticipantes { get; set; }
    }

    // Clase de respuesta que retorna la API
    public class ResponseParticipantesFormulario2
    {
        public string eiyuResponseCode { get; set; }
        public string descripcion { get; set; }
        public List<ParticipanteFormulario2> content { get; set; }
    }

    public class ParticipanteFormulario2
    {
        public string idParticipantes { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string cuentasAsignadas { get; set; }
        public string estatus { get; set; }
        public string nombreFirmante { get; set; }
        public string CargoPuesto { get; set; }
    }

    public class GenerarArchivoRequest
    {
        public List<int> idsParticipantes { get; set; }
        public string tipoArchivo { get; set; }
    }

    public class ListadoCA
    {
        public string RazonSocial { get; set; }
        public int Tercia { get; set; }
        public string ClabeClienteIndirecto { get; set; }
        public DateTime fechaAsginacionClabe { get; set; } 
    }

    public class Content
    {
        public List<ListadoCA> ListadoCA { get; set; }
        public string NombreFirmante { get; set; }
        public string CargoPuesto { get; set; }
    }


    public class ApiResponse22
    {
        public string ResponseCode { get; set; }
        public string Descripcion { get; set; }
        public Content Content { get; set; }
    }

    public class ListadoCAFormulario3
    {
        public string RazonSocial { get; set; }
        public int Tercia { get; set; }
        public string ClabeClienteIndirecto { get; set; }
        public DateTime fechaBajaClabe { get; set; } // 
    }

    public class ContentFormulario3
    {
        public List<ListadoCAFormulario3> ListadoCA { get; set; }
        public string NombreFirmante { get; set; }
        public string CargoPuesto { get; set; }
    }
    public class ApiResponseFormulario3
    {
        public string ResponseCode { get; set; }
        public string Descripcion { get; set; }
        public ContentFormulario3 Content { get; set; }
    }

    public class ContentendorAsignados
    {
        public List<ParticipanteAsignados> Items { get; set; }
    }

    public class ParticipanteAsignados
    {
        public string razonSocial { get; set; }
        public string tercia { get; set; }
        public string clabeClienteIndirecto { get; set; }
        public string fechaAsginacionClabe { get; set; }
    }


    public class FormlarioResponseBajas
    {
        public string EiyuResponseCode { get; set; }
        public string Description { get; set; }
        public List<ParticipantesCuentaBajas> Content { get; set; }
    }
    public class ParticipantesCuentaBajas
    {
        public int idParticipante { get; set; }
        public string clabe { get; set; }
        public string razonSocial { get; set; }
        public string cuentasBajas { get; set; }
    }

}


