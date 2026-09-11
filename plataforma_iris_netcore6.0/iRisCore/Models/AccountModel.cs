using System.ComponentModel.DataAnnotations.Schema;

namespace iRisCore.Models
{
    public class AccountModel
    {
        public string? usuario { get; set; }
        public string? contrasena { get; set; }
        public string? idUsuario { get; set; }
        public string? ipUsuario { get; set; }
        public string? idSesion { get; set; }
        public string? estatus { get; set; }
        public string? idAplicativo { get; set; }       
    }

    public class ModelLoginDock
    {
        public string NombreUsuario { get; set; }
        public string Password { get; set; }
    }

    public class ModelLoginDockResponse 
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public string data { get; set; }
    }

    public class ModelPlataformas
    {
        public string IdPlataforma { get; set; } = "";
        public string NombrePlataforma { get; set; } = "";
    }

    public class ModelToken
    {
        public string clave { get; set; }
        public string numeroEmpleado { get; set; }
        public string usuarioCero { get; set; }
        public string password { get; set; }
        public string ipHost {  get; set; }
    }

    public class ModelVerifyToken
    {
        public string Token { get; set; }
        public string NumeroEmpleado { get; set; }
    }

}
