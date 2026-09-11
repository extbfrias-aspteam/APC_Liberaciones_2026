using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class RecargaMovilModel
    {
        public string? numeroCelular { get; set; }
        public string? fechaOperacion { get; set; }
        public string? companiaTel { get; set; }
        public string numeroReferencia { get; set; }
    }

    public class ResponseRecargaMovilModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set;}
    }

    public class ListadoRecargasMovilModel
    {
        public string cuentaAsp { get; set; }
        public string fechaOperacion { get; set; }
        public string fechaAplicacion { get; set; }
        public double monto { get; set; }
        public string descripcionMovimiento { get; set; }
        public string numeroAutorizacion { get; set; }
        public string referencia { get; set; }
        public string numeroCelular { get; set; }
        public string companiaTel { get; set; }
        public string tipoRecarga { get; set; }
        public string status { get; set; }
        public string descripcionEstatus { get; set; }
    }

    public class ListadoCompanias 
    {
        public string compania { get; set; }
    }

}
