using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ActualizaBeneficiarioModel
    {
        public string cuentaASP { get; set; } = "";
        public string numeroTelefono { get; set; } = "";
        public datosBeneficiario beneficiario { get; set; } = new datosBeneficiario();
    }

    public class datosBeneficiario
    {
        public string nombre { get; set; } = "";
        public string descripcionParentesco { get; set; } = "";
        public string calle { get; set; } = "";
        public string numExt { get; set; } = "";
        public string numInt { get; set; } = "";
        public string municipio { get; set; } = "";
        public string cp { get; set; } = "";
        public string colonia { get; set; } = "";
        public string ciudad { get; set; } = "";      
        public string entidadfederativa { get; set; } = "";
       
        public string porcentaje { get; set; } = "";
        public string fechaNacimiento { get; set; } = "";
        public string telefono { get; set; } = "";
        public string email { get; set; } = "";
    }

}
