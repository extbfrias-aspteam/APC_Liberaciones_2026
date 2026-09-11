using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class PagoCreditoModel
    {
        public string cuentaCredito { get; set; }
        public string fechaOperacion { get; set; }
    }

    public class ResponsePagoCreditoModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class ListadoPagoCreditoModel
    {
        public credito credito { get; set; } = new credito();
        public List<detalleCredito> detalleCredito { get; set; } = new List<detalleCredito>();


    }

    public class credito
    {
        public double montoCredito { get; set; }
        public double saldoCapital { get; set; }
        public double capitalPagado { get; set; }
        public double saldoInteres { get; set; }
        public double interesPagado { get; set; }
        public double saldoIva { get; set; }
        public double ivaPagado { get; set; }
        public double saldoMora { get; set; }
        public double moraPagado { get; set; }
        public double saldoMoraIva { get; set; }
        public double moraIvaPagado { get; set; }
    }

    public class detalleCredito
    {
        public double montoPago { get; set; }
        public List<detallePago> detallePago { get; set; } = new List<detallePago>();
    }

    public class detallePago
    {
        public string pagoObs { get; set; }
        public string clavePago  { get; set; }
        public string descripcionPago { get; set; }
        public double monto { get; set; }
        public string fechaPago { get; set; }
        public string fechaDeposito { get; set; }
    }

}