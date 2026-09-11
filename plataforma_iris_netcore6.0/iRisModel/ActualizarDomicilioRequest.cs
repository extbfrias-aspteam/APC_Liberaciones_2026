using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace iRisModel
{
    public class ActualizarDomicilioRequest
    {
    
        public int usuarioId { get; set; }

        public int coloniaId { get; set; }

        public string solicitanteId { get; set; }


        public string domicilio { get; set; }


        public string ip { get; set; }

    
        public string catDomicilio1 { get; set; }

        public string descripcionDomicilio1 { get; set; }



        public string catDomicilio2 { get; set; }


        public string descripcionDomicilio2 { get; set; }


        public string catDomicilio3 { get; set; }

 
        public string descripcionDomicilio3 { get; set; }




       
        public string catDomicilio4 { get; set; }


        public string descripcionDomicilio4 { get; set; }


        public string catDomicilio5 { get; set; }


        public string descripcionDomicilio5 { get; set; }

  
        public string observaciones { get; set; }



        public Colonia1 colonia { get; set; }

    }
    public class Colonia1
    {
        public string cp { get; set; }
        public string colonia { get; set; }
        public string localidad { get; set; }
        public string municipio { get; set; }
        public string estado { get; set; }
        

    }

}
