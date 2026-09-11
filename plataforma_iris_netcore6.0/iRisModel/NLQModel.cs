using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class NLQModel
    {
        public string ClaveMovimiento { get; set; }
        public string Movimiento { get; set; }
        public string MovimientoOriginal { get; set; }
        public string Tarjeta { get; set; }
        public string CodigoTx { get; set; }
        public string Comercio { get; set; }
        public string Ciudad { get; set; }
        public string Pais { get; set; }
        public string MCC { get; set; }
        public string Referencia { get; set; }
        public string ImporteAut { get; set; }
        public string MonedaAut { get; set; }
        public string ImporteOtraMoneda { get; set; }
        public string OtraMoneda { get; set; }
        public string ImporteCompensado { get; set; }
        public string MonedaCompensacion { get; set; }
        public string Autorizacion { get; set; }
        public string FechaAut { get; set; }
        public string FechaPresentacion { get; set; }
        public string FechaProcesamiento { get; set; }
        public string FechaJuliana { get; set; }
        public string Compensada { get; set; }
    }
}
