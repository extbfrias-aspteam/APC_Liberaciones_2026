using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConsultaCuentaModel
    {
        public int? codigo { get; set; }
        public string mensaje { get; set; }
        public string nombreCompleto { get; set; }
        public string direccion { get; set; }
        public string curp { get; set; }
        public string telefono { get; set; }
        public string ocupacion { get; set; }
        public string fechaNacimiento { get; set; }
        public string correoElectronico { get; set; }
        public string lugarNacimiento { get; set; }
        public string sexo { get; set; }
        public string RFC { get; set; }
        public string numero { get; set; }
        public string nivelCuenta { get; set; }
        public double saldoCuenta { get; set; }
        public string data { get; set; }
        public DataBeneficiario beneficiario { get; set; } 
        public List<Medios> mediosDisposicion { get; set; }
    }

    public class DataBeneficiario
    {
        public string nombre { get; set; }
        public string calle { get; set; }
        public string numExt { get; set; }
        public string numInt { get; set; }
        public string municipio { get; set; }
        public string CP { get; set; }
        public string colonia { get; set; }
        public string ciudad { get; set; }
        public string telefono { get; set; }
        public string entidadfederativa { get; set; }
        public string fechaNacimiento { get; set; }
        public string parentesco { get; set; }
    }

    public class Medios
    {
        public string medio { get; set; }
        public string cardId { get; set; }
        public string clave { get; set; }
        public string estatus { get; set; }
        public string tipoProducto { get; set; }
        public string tipo { get; set; }
    }
}
