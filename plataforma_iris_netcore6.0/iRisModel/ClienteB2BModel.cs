using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ClienteB2BModel
    {
        public int idCliente { get; set; }
        public string Rfc { get; set; }
        public string razonSocial { get; set; }
        public string Nombre { get; set; }
        public string direccionFiscal { get; set; }
        public int? idIdioma { get; set; }
        public string tieneTraductor { get; set; }
        public string horarioAtencion { get; set; }
        public int? numeroCliente { get; set; }
        public string numeroCuenta { get; set; }
        public string cuentaClabe { get; set; }
        public int? idTipoCliente { get; set; }
        public string descripcionTipoCliente { get; set; }
        public string estatusCliente { get; set; }
        public string modeloNegocio { get; set; }

        public string messageResponse { get; set; }
    }
}
