using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaPagoServicioModel
    {
        public string proveedor { get; set; }
        public string fechaOperacion { get; set; }
        public string numeroReferencia { get; set; }
        public string numeroAutorizacion { get; set; }
    }

    public class ResponsePagoServicioModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class ListadoPagoServicioModel
    {
        public string cuentaASP { get; set; }
        public string descripcionMovimiento { get; set; }
        public string emisor { get; set; }
        public string movimiento { get; set; }
        public string numeoReferencia { get; set; }
        public string fechaOperacion { get; set; }
        public string fechaAplicacion { get; set; }
        public double monto { get; set; }
        public string proveedor { get; set; }
        public string estatus { get; set; }
        public string mensajeEstatus { get; set; }
        public string numeroAutorizacion { get; set; }

    }

    public class ListadoProveedoresPagoServicio
    {
        public string proveedor { get; set; }
    }


}
