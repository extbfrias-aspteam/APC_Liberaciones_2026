using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class TokenModel
    {
    }

    public class TokenResponse
    {
        public string codigo { get; set; }
        public string mensaje { get; set; }
        public TokenData data { get; set; }
    }

    public class TokenData
    {
        public string refreshToken { get; set; }
        public string token { get; set; }
        public string message { get; set; }
    }

    public class RefreshTokenRequest
    {
        public string refreshToken { get; set; }
        public string token { get; set; }
        public UserToken user { get; set; }
    }

    public class UserToken
    {
        public string usuario { get; set; }
        public string numEmpleado { get; set; }
        public string idUsuario { get; set; }
        public string idAplicativo { get; set; }
    }
}
