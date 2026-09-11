using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    // Modelos adicionales
    public class DomicilioResponseBase
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public int usuarioId { get; set; }
        public string domicilio { get; set; }
        public int coloniaId { get; set; }
        public string observaciones { get; set; }
        public int catDomicilio1 { get; set; }
        public int catDomicilio2 { get; set; }
        public int catDomicilio3 { get; set; }
        public int catDomicilio4 { get; set; }
        public int catDomicilio5 { get; set; }
        public string descripcionDomicilio1 { get; set; }
        public string descripcionDomicilio2 { get; set; }
        public string descripcionDomicilio3 { get; set; }
        public string descripcionDomicilio4 { get; set; }
        public string descripcionDomicilio5 { get; set; }
        public Colonia colonia { get; set; }
    }

    public class DomicilioData
    {
        public int usuarioId { get; set; }
        public string domicilio { get; set; }
        public int coloniaId { get; set; }
        public string observaciones { get; set; }
        public int catDomicilio1 { get; set; }
        public int catDomicilio2 { get; set; }
        public int catDomicilio3 { get; set; }
        public int catDomicilio4 { get; set; }
        public int catDomicilio5 { get; set; }
        public string descripcionDomicilio1 { get; set; }
        public string descripcionDomicilio2 { get; set; }
        public string descripcionDomicilio3 { get; set; }
        public string descripcionDomicilio4 { get; set; }
        public string descripcionDomicilio5 { get; set; }
        public Colonia colonia { get; set; }
    }

    public class Colonia
    {
        public string cp { get; set; }
        public string colonia { get; set; }
        public string localidad { get; set; }
        public string municipio { get; set; }
        public string estado { get; set; }
        public string clave { get; set; }

    }
}
