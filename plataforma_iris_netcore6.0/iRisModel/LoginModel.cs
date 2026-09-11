using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class LoginModel
    {
        public string UserName { get; set; } = "";
        public string Password { get; set; } = "";
    }

    public class ResponseLoginModel
    {      
        public int Codigo { get; set; } = 0;
        public string Mensaje { get; set; } = "";
        public string Token { get; set; } = "";
    }

    public class LoginTokenData
    {
        public string RefreshToken { get; set; }
        public string Token { get; set; }
        public string Message { get; set; }
        public string NumEmpleado { get; set; }
        public string IdUsuario { get; set; }
    }

    public class LoginTokenResponse
    {
        public int Codigo { get; set; }
        public string Mensaje { get; set; }
        public LoginTokenData Data { get; set; }
    }
}
