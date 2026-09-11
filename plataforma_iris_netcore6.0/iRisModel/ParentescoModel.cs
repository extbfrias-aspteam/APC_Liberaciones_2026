using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ParentescoModel
    {
        public int codigo { get; set; }
        public string mensaje { get; set; } = "";
        public List<Parentesco> data { get; set; } = new List<Parentesco>();
    }

    public class Parentesco
    {
        public int idParentesco { get; set; }
        public string decripcionParentesco { get; set; } = "";
    }

}
