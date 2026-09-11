using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace iRisModel
{
 
        public class GeneraPinOTPRequest
        {
            public string clave { get; set; }
            public string valor { get; set; }
            public int usuarioId { get; set; }
        }

        public class GeneraPinOTPResponse
        {
            public int codigo { get; set; }
            public string mensaje { get; set; }
            public object data { get; set; }
        }
 
}
