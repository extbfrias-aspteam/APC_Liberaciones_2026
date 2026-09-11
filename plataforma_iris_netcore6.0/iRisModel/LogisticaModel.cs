using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class LogisticaModel
    {
        public int? codigo { get; set; }
        public string? mensaje { get; set; }
        public string? data { get; set; }
        public int total { get; set; }
        public string estatus { get; set; }

        public List<DetalleLogisticaModel>? tarjetas { get; set; }
    }
    public class DetalleLogisticaModel
    {
        public string? sucursal { get; set; }
        public string? marca { get; set; }
        public string? id { get; set; }
        public string? idFolio { get; set; }
        public string? PAN { get; set; }
        public string? fechaVencimiento { get; set; }
        public string guia { get; set; }
        public string nombre { get; set; }
        public string direccion1 { get; set; }
        public string? direccion2 { get; set; }
        public string? colonia { get; set; }
        public string ciudad { get; set; }
        public string? estado { get; set; }
        public string? cp { get; set; }
        public string? telefono { get; set; }
        public string? celular { get; set; }
        public string? email { get; set; }
        public string? estatus { get; set; }
        public string? cuentaASP { get; set; }
        public string? pan_used { get; set; }
        public string observaciones { get; set; } = "";
        public string guiaDev { get; set; } = "";
        public string? fecha { get; set; }
        public string? fechaSolicitud { get; set; }
        public string? fechaTransito { get; set; }
        public string? fechaEntrega { get; set; }


    }

    public class LogisticaRequestModel
    {
        public int? marca { get; set; }
        public int? sucursal { get; set; }
        public string? estatus { get; set; }
        public string? guia { get; set; }
        public bool firstStock { get; set; } = false;

    }
    public class LogisticaEntregaTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string origenSolicitud { get; set; }
        public int? sucursalId { get; set; }
        public string cuentaASP { get; set; } = "";
        public string idFolio { get; set; }
        public int? marcaId { get; set; }
        public string pan { get; set; } = "";
        public bool reposicion { get; set; } = false;
    }
    public class RequestLogisticaActualizarModel
    {
        public string? origenSolicitud { get; set; }
        public int? marcaId { get; set; }
        public int? sucursalId { get; set; }
        public string? estatusId { get; set; }
        public string? observaciones { get; set; } = "";
        public string? nombreRecibe { get; set; } = "";
        public string guiaDev { get; set; } = "";
        public List<RequestLogisticaDetalleTarjetaModel>? detalleTarjeta { get; set; } = new List<RequestLogisticaDetalleTarjetaModel>();
    }

    public class RequestLogisticaDetalleTarjetaModel
    {
        public string? numeroTarjeta { get; set; }
    }


    public class RequestLogisticaArchivoDistribucion
    {
        public string? origenSolicitud { get; set; }
    }

    public class ResponseArchivoDistribucion
    {
        public int codigo { get; set; }
        public string? mensaje { get; set; }
        public string? data { get; set; }
    }

}
