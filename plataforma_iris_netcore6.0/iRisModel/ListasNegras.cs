using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    

    public class RequestHeaderModelListasNegras
    {
        public int idEmpresa { get; set; }
        public int idResponsabilidad { get; set; }
         public string usuarioClave { get; set; }
        public string idUsuario { get; set; }
        public int idClaseCanalAtencion { get; set; }
         public int idCanalAtencion { get; set; }
        public int idPuntoAtencion { get; set; }
        public int idUbicacion { get; set; }
        public int idSucursal { get; set; }
         public int idComisionista { get; set; }
        public int idTransaccion { get; set; }
        public string ipHost { get; set; }
    }

    public class ListasNegrasRequest
    {
        public RequestHeaderModelListasNegras header { get; set; }
        public string valor { get; set; }
       public int tipoId { get; set; }
         public string motivoAlta { get; set; }
    }

    public class ListasNegrasResponse
    {
       public int codigo { get; set; }
        public string mensaje { get; set; }
    }

    public class CatalogoListasNegrasResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public List<CatalogoItem> data { get; set; }
    }

    public class CatalogoItem
    {
        public int id { get; set; }
        public string nombre { get; set; }
        public string descripcion { get; set; }
        public string estatus { get; set; }
    }



    public class ListaNegraResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public List<ListaNegraItem> data { get; set; }
    }

    public class ListaNegraItem
    {
        public int id { get; set; }
        public string valor { get; set; }
        public int tipoId { get; set; }
        public string estatus { get; set; }
        public string motivoAlta { get; set; }
        public string motivoBaja { get; set; }
    }

    public class PermisoUsuarioListasNegrasResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public PermisoUsuarioData data { get; set; }
    }

    public class PermisoUsuarioData
    {
        public bool tienePermiso { get; set; }
    }

    public class ListasNegrasBajaPatchRequest
    {
        public PatchHeader header { get; set; }
        public string estatus { get; set; }
        public string motivo { get; set; }
    }

    public class ListasNegrasBajaAutorizadaRequest
    {
        public PatchHeader header { get; set; }
        public string estatus { get; set; }
        public string token { get; set; }
    }
    public class PatchHeader
    {
        public string numCuenta { get; set; }
        public int idComisionista { get; set; }
        public int idTransaccion { get; set; }
        public int idEmpresa { get; set; }
        public int idResponsabilidad { get; set; }
        public string usuarioClave { get; set; }
        public int idUsuario { get; set; }
        public int idClaseCanalAtencion { get; set; }
        public int idCanalAtencion { get; set; }
        public int idPuntoAtencion { get; set; }
        public int idUbicacion { get; set; }
        public int idSucursal { get; set; }
        public string ipHost { get; set; }
        public float latitud { get; set; }
        public float longitud { get; set; }

        public int vistaId { get; set; }
    }

    public class BitacoraRequestLN
    {
        public string usuario { get; set; }
        public int accionId { get; set; }
        public string resultado { get; set; }
        public string ip { get; set; }
    }
}
