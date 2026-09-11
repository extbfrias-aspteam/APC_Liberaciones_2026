using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ActualizarCorreoRequest
    {
        public int usuarioId { get; set; }
        public string solicitanteId { get; set; }
        public string correo { get; set; }
        public string ip { get; set; }
    }
}
