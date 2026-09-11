using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iRisModel
{
    public class RequestHeaderModel
    {
        public int idComisionista { get; set; }
        public int idTransaccion { get; set; }
        public int idEmpresa { get; set; }
        public int idResponsabilidad { get; set; }
        public string usuarioClave { get; set; }
        public int idUsuario { get; set; }
        public int idClaseCanalAtencion { get; set; }
        public int idCanalAtencion { get; set; }
        public int idPuntoAtención { get; set; }
        public int idUbicacion { get; set; }
        public int idSucursal { get; set; }
        public string ipHost { get; set; }
        public int idSesion { get; set; }
        public string nameHost { get; set; }
        public int longitud { get; set; }
        public int latitud { get; set; }
        public string idBanco { get; set; }
    }
}
