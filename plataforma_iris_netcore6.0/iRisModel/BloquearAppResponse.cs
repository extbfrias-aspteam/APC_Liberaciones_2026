using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class BloquearAppResponse
    {
        public int codigo { get; set; }
        public string? mensaje { get; set; }
        public string? data { get; set; }
    }
    
    public class BloqueoDesbloqueoApp
    {
        public string? telefono { get; set; }
        public string? cuenta { get; set; }
    }
}
