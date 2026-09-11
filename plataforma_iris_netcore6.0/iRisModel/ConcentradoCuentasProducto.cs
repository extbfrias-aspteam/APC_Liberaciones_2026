using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ConcentradoCuentasProducto
    {
        public string producto { get; set; } = "";
        public bool actualizaPayloads { get; set; } = false;
    }

    public class ConcentradoCuentasProductoResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }
    public class DetalleConcentradoCuentasProducto
    {
        public int totalRegistros { get; set; }
        public List<ListaSolicitante> listaSolicitantes { get; set; }
    }
    public class ListaSolicitante
    {
        public CuentaHabiente cuentaHabiente { get; set; }
    }
    public class CuentaHabiente
    {
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
        public double saldoCuenta { get; set; }
        public string onboarding { get; set; }
        public Beneficiario beneficiario { get; set; }
        public List<MediosDisposicion> mediosDisposicion { get; set; }
    }

    public class Beneficiario
    {
        public string nombre { get; set; }
        public string fechaNacimiento { get; set; }
        public string domicilio { get; set; }
        public string parentesco { get; set; }
    }
    public class MediosDisposicion
    {
        public string medio { get; set; }
        public string clave { get; set; }
        public string estatus { get; set; }
        public string tipoProducto { get; set; }
        public string tipo { get; set; }
    }








}
