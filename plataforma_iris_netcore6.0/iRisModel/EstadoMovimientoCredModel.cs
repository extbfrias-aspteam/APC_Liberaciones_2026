using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class EstadoMovimientoCredModel
    {
        public int noDisposicion { get; set; }
        public string cuentaCredito { get; set; } = "";
        public int sucursalId  { get; set; }

    }

    public class ResponseEstadoMovimientoCredModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; } = "";
        public Data data { get; set; } = new Data();
    }

    public class Data
    {
        public Cabecero cabecero { get; set; } = new Cabecero();
        public List<Detalle> detalle { get; set; } = new List<Detalle>();
        public PagoFooter pagoFooter { get; set; } = new PagoFooter();
        public VencimientoFooter vencimientoFooter { get; set; } = new VencimientoFooter();
        public VencidoFooter vencidoFooter { get; set; } = new VencidoFooter();
        public VenceFooter venceFooter { get; set; } = new VenceFooter();
        public VigenteFooter vigenteFooter { get; set; } = new VigenteFooter();
        public object calificacion { get; set; } = "";
    }

    public class Cabecero
    {
        public string periodo { get; set; } = "";
        public double tasaAnual { get; set; }
        public double tasaMensual { get; set; }
        public double tasaMoratoria { get; set; }
        public double montoDispersion { get; set; }
        public string fechaDescuento { get; set; } = "";
        public string empresa { get; set; } = "";
        public string solicitante { get; set; } = "";
        public string referencia { get; set; } = "";
        public int numeroDisposicion { get; set; }
        public double cat { get; set; }
    }

    public class Detalle
    {
        public VencimientosDetalle vencimientosDetalle { get; set; } = new VencimientosDetalle();
        public PagoDetalle pagoDetalle { get; set; } = new PagoDetalle();
    }

    public class DetallePagoEdoMov
    {
        public double capital { get; set; }
        public double intereses { get; set; }
        public double esquemaCobro { get; set; }
        public double tipoDisposicion { get; set; }
        public double impuestoNormal { get; set; }
        public double impuestoEsquemaCobro { get; set; }
        public double impuestoTipoDisposicion { get; set; }
        public double total { get; set; }
        public string fechaPago { get; set; } = "";
    }

    public class ImpuestoEsquemaCobro
    {
        public string tipo { get; set; } = "";
        public double tipoValue { get; set; }
    }

    public class ImpuestoTipoDisposicion
    {
        public string tipo { get; set; } = "";
        public double tipoValue { get; set; }
    }

    public class PagoDetalle
    {
        public double capital { get; set; }
        public double interesNormal { get; set; }
        public double faltaPago { get; set; }
        public TipoDisposicion tipoDisposicion { get; set; } = new TipoDisposicion();
        public double impuestoNormal { get; set; }
        public ImpuestoEsquemaCobro impuestoEsquemaCobro { get; set; } = new ImpuestoEsquemaCobro();
        public ImpuestoTipoDisposicion impuestoTipoDisposicion { get; set; } = new ImpuestoTipoDisposicion();
        public double total { get; set; }
        public string fechaPago { get; set; } = "";
        public int diasAtraso { get; set; }
        public List<DetallePagoEdoMov> detallePagoEdoMov { get; set; } = new List<DetallePagoEdoMov>();
    }

    public class PagoFooter
    {
        public double totalImpuestoTipoDisposicionPago { get; set; }
        public double totalInteresNormalPago { get; set; }
        public double totalCapitalSum { get; set; }
        public double totalPago { get; set; }
        public double totalTipoDisposicionPago { get; set; }
        public double totalImpuestoNormalPago { get; set; }
        public double totalFaltaPagoPago { get; set; }
        public double totalImpuestoEsquemaCobroPago { get; set; }
    }



    public class TipoDisposicion
    {
        public string tipo { get; set; } = "";
        public double tipoValue { get; set; }
    }

    public class VenceFooter
    {
        public double venceFaltaPago { get; set; }
        public double venceTotal { get; set; }
        public double venceImpuestoEsquemaCobro { get; set; }
        public double venceTipoDisposicion { get; set; }
        public double venceImpuestoNormal { get; set; }
        public double venceCapital { get; set; }
        public double venceImpuestoTipoDisposicion { get; set; }
        public double venceInteresNormal { get; set; }
    }

    public class VencidoFooter
    {
        public double vencidoImpuestoNormal { get; set; }
        public double vencidoTipoDisposicion { get; set; }
        public double vencidoImpuestoEsquemaCobro { get; set; }
        public double vencidoCapital { get; set; }
        public double vencidoInteresNormal { get; set; }
        public double vencidoTotal { get; set; }
        public double vencidoFaltaPago { get; set; }
        public double vencidoImpuestoTipoDisposicion { get; set; }
    }

    public class VencimientoFooter
    {
        public double totalVencimiento { get; set; }
        public double totalImpuestoEsquemaCobroVencimiento { get; set; }
        public double totalImpuestoTipoDisposicionVencimiento { get; set; }
        public double totalInteresNormalVencimiento { get; set; }
        public double totalImpuestoNormalVencimiento { get; set; }
        public double totalCapitalVencimiento { get; set; }
        public double totalTipoDisposicionVencimiento { get; set; }
        public double totalFaltaPagoVencimiento { get; set; }
    }

    public class VencimientosDetalle
    {
        public int consecutivoPago { get; set; }
        public string fechaVencimiento { get; set; } = "";
        public double capitalPagar { get; set; }
        public double interesNormal { get; set; }
        public double faltaPago { get; set; }
        public TipoDisposicion tipoDisposicion { get; set; } = new TipoDisposicion();
        public double impuestoNormal { get; set; }
        public ImpuestoEsquemaCobro impuestoEsquemaCobro { get; set; } = new ImpuestoEsquemaCobro();
        public ImpuestoTipoDisposicion impuestoTipoDisposicion { get; set; } = new ImpuestoTipoDisposicion();
        public double total { get; set; }
        public string edoAmortizacion { get; set; } = "";
    }

    public class VigenteFooter
    {
        public double vigenteInteresNormal { get; set; }
        public double vigenteTipoDisposicion { get; set; }
        public double vigenteImpuestoEsquemaCobro { get; set; }
        public double vigenteFaltaPago { get; set; }
        public double vigenteTotal { get; set; }
        public double vigenteCapital { get; set; }
        public double vigenteImpuestoNormal { get; set; }
        public double vigenteImpuestoTipoDisposicion { get; set; }
    }


}
