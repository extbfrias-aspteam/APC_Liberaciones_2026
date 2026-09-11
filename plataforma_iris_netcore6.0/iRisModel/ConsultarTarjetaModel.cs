using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ActivarTarjetaModel
    {
        public RequestHeaderModel header { get; set; }

        public string numeroTarjeta { get; set; }
    }


    public class ActivarTarjetaResponse
    {
        public int? codigo { get; set; }
        public string? mensaje { get; set; }
        public string? detalle { get; set; }
    }

    public class ConsultarTarjetaModel
    {
        public RequestHeaderModel header { get; set; }
        public string tipoTarjeta { get; set; }
        public string numeroTarjeta { get; set; }
    }

    public class ConsultarTarjetaResponse {
        public int? codigo { get; set; }
        public string mensaje { get; set; }
        public string NumeroTarjeta { get; set; }
        public string TipoTarjeta { get; set; }
        public string Cacao { get; set; }
        public string data { get; set; }
        public string Nombre { get; set; }
        public string CtaClabe { get; set; }
        public string Vigencia { get; set; }
        public string Estatus { get; set; }
        public string Saldo { get; set; }
        public string CuentaASP { get; set; }
    }

    public class Datas {
        public string IDSolicitud { get; set; }
        public string CodRespuesta { get; set; }
        public string DescRespuesta { get; set; }
        public string Tarjeta { get; set; }
        public string TipoManufactura { get; set; }
        public string Cuenta { get; set; }
        public string CLABE { get; set; }
        public string CuentaCacao { get; set; }
        public DateTime FechaVigencia { get; set; }
        public string Status { get; set; }
        public string DescripcionStatus { get; set; }
        public string Nombre { get; set; }
        public string PrimerApellido { get; set; }
        public string solicitante_id { get; set; }
        public List<Saldos> SaldoActual { get; set; }
        public List<Movtos> Movimientos { get; set;}
        public List<Transacciones> transaccionesDenegadas { get; set; }

    }

    public class Movtos 
    {
        public double ImporteOrigenMovimiento { get; set; }
        public string MonedaOrigenMovimiento { get; set; }
        public string NumeroAfiliacionComercio { get; set; }
        public string DescripcionComercio { get; set; }
        public string PaisComercio { get; set; }
        public string DescripcionPais { get; set; }
        public double SaldoDespuesMovimiento { get; set; }
        public string ClaveMovimiento { get; set; }
        public string MCC { get; set; }
        public string EstatusPostOperacion { get; set; }
        public string DescripcionPostOperacion { get; set; }
        public string Autorizacion { get; set; }
        public string IDMovimiento { get; set; }
        public DateTime FechaMovimiento { get; set; }
        public string ConceptoMovimiento { get; set; }
        public string TipoMovimientoCargoAbono { get; set; }
        public double ImporteMovimiento { get; set; }
        public string MonedaMovimiento { get; set; }
        public int? Referencia { get; set; }
        public string Observaciones { get; set; }

    }
    public class Saldos
    {
        public string ID_Cuenta { get; set; }
        public string ClaveTipoCuenta { get; set; }
        public string DescripcionTipoCuenta { get; set; }
        public double Saldo { get; set; }
    }


    public class Transacciones
    {
        public string fechaTransaccion { get; set; }
        public string horaTransaccion { get; set; }
        public string formaTransaccion { get; set; }
        public string tipoTransaccion { get; set; }
        public string afiliadoComercio { get; set; }
        public string sucursalComercio { get; set; }
        public string nombreComercio { get; set; }
        public string razonDenegacion { get; set; }
        public string razonDenegacionDescripcion { get; set; }
        public string numeroTarjeta { get; set; }
        public string nombreTH { get; set; }
        public string montoDenegacion { get; set; }
        public string codigoMoneda { get; set; }
        public string nombreMoneda { get; set; }
        public string estadoTarjeta { get; set; }
        public string descripcionEstadoTarjeta { get; set; }




    }


}
