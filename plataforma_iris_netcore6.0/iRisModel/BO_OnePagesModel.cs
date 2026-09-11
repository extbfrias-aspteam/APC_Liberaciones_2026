using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BO_OnePagesModel
    {
    }

    public class OnePages
    {
        public int idPblu { get; set; }
        public string nombreComercial { get; set; }
        public string razonSocial { get; set; }
        public string cuentaClabe { get; set; }
        public string estatus { get; set; }
        public string rfc { get; set; }
        public string direccionFiscal { get; set; }
        public string idioma { get; set; }
        public string horariodeatencion { get; set; }
        public bool traductor { get; set; }
        public string numerodeCliente { get; set; }
        public string numerodeCuenta { get; set; }
        public string tipodeCliente { get; set; }
        public string url{ get; set; }
        public string fechadeEmision { get; set; }
        public string fechadeTermino { get; set; }
        public string fechadeAnexosAdendas { get; set; }
        public string fechaconveniomodificatiorio { get; set; }
        public string productoContratado { get; set; }
        public int? idProductoContratado { get; set; }

    }
    public class OnePageRequest
    {
        public int idParticipante { get; set; }
    }
    public class OnePageRequest2
    {
        public int idPblu { get; set; }
    }
    public class OnePagesResponseWs
    {
        public string responseCode { get; set; }
        public string descripcion { get; set; }
        public dynamic content { get; set; }
    }
    public class OnePagesResponseFR
    {
        public int Codigo { get; set; }
        public string Mensaje { get; set; }
        public dynamic Data { get; set; }
    }
    public class OnePagesConceptos
    {
        public int id_concepto_comercial { get; set; }
        public string concepto { get; set; }
        public string clave { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesContratoProductos
    {
        public int id { get; set; }
        public string nombre { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesTiposCobro
    {
        public int id_tipo_cobro { get; set; }
        public string tipo_cobro { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesCondicionesReq
    {
        public int id_condicion_comercial { get; set; }
        public int id_concepto_comercial { get; set; }
        public int idPblu { get; set; }
        public int id_tipo_cobro { get; set; }
        public decimal precio_unitario { get; set; }
        public int meses { get; set; }
        public bool activo { get; set; }
        public string usuario { get; set; }
    }
    public class OnePagesCondicionesRes
    {
        public int id_condicion_comercial { get; set; }
        public OnePagesConceptos id_concepto_comercial { get; set; }
        public OnePagesPblu pblu { get; set; }
        public OnePagesTiposCobro id_tipo_cobro { get; set; }
        public decimal precio_unitario { get; set; }
        public int meses { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesTarifasGet
    {
        public int pblu { get; set; }
        public int idTipoTarifa { get; set; }
    }
    public class OnePagesTarifasSet
    {
        public int id_tarifa_comercial { get; set; }
        public int idPblu { get; set; }
        public int id_tipo_tarifa_comercial { get; set; }
        public int min_operaciones { get; set; }
        public int max_operaciones { get; set; }
        public double precio_unitario { get; set; }
        public bool activo { get; set; }
        public string usuario { get; set; }
    }
    public class OnePagesTarifasRes
    {
        public int id_tarifa_comercial { get; set; }
        public OnePagesPblu pblu { get; set; }
        public OnePagesTipoTarifa id_tipo_tarifa_comercial { get; set; }
        public int min_operaciones { get; set; }
        public int max_operaciones { get; set; }
        public decimal precio_unitario { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesTipoTarifa
    {
        public int id_tipo_tarifa_comercial { get; set; }
        public string tipo_tarifa { get; set; }
        public bool activo { get; set; }
    }
    public class OnePagesPblu
    {
        public int idPblu { get; set; }
        public int prefijoPblu { get; set; }
        public string clabePblu { get; set; }
        public string descripcion { get; set; }
        public bool activo { get; set; }
        public string rfc { get; set; }
    }
    public class OnePagesContactosSet
    {
        public int id_contacto { get; set; }
        public string nombre { get; set; }
        public string area { get; set; }
        public string puesto { get; set; }
        public string telefono { get; set; }
        public string whatsapp { get; set; }
        public string correo { get; set; }
        public int idPblu { get; set; }
        public string usuario { get; set; }
        public bool estatu { get; set; }
    }
    public class OnePagesContactos
    {
        public int id_contacto { get; set; }
        public string nombre { get; set; }
        public string area { get; set; }
        public string puesto { get; set; }
        public string telefono { get; set; }
        public string whatsapp { get; set; }
        public string correo { get; set; }
        public int idPblu { get; set; }
        public bool estatu { get; set; }
    }
    public class OnePagesContratoB2BSet
    {
        public int idPblu { get; set; }
        public string fechaTermino { get; set; }
        public string fechaAddenda { get; set; }
        public string fechaConvenio { get; set; }
        public string usuario { get; set; }
        public int? idProductoContratado { get; set; }

    }
    public class OnePagesInformacionB2BSet
    {
        public int idPblu { get; set; }
        public string idioma { get; set; }
        public string horario { get; set; }
        public bool traductor { get; set; }
        public string url { get; set; }
        public string usuario { get; set; }
    }
}
