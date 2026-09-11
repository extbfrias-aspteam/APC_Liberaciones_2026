using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class DuplicadosModel
    {
        public string correo { get; set; }  //telefono o correo

        public string telefono { get; set; }
    }

    public class Duplicados
    {
        public string solicitanteId { get; set; }
        public string curp { get; set; } //Cuenta CLABE del ordenante
        public string rfc { get; set; } //Cuenta CLABE del beneficiario
        public string correo { get; set; }
        public string telefono { get; set; }
        public string nombre { get; set; }
        public string cuenta { get; set; }
        public string producto { get; set; }
    }

    public class DuplicadosResponse
    {
        public string codigo { get; set; }
        public string mensaje { get; set; }
        public dynamic data { get; set; }
    }

    public class ResponseDuplicados
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public dynamic data { get; set; }
    }
}
