using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ActualizaTelefonoRequest
    {
        public int usuarioId { get; set; }
        public string solicitanteId { get; set; }
        public string telefono { get; set; }
        public string ip { get; set; }
    }
}
