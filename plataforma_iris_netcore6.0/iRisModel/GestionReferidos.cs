using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace iRisModel
{
  
        public class Referenciador
        {
            public string Solicitor { get; set; }
            public string Nombre { get; set; } 
            public string Direccion { get; set; } 
            public string Curp { get; set; }

        }

        public class Campana
        {
            public string Nombre { get; set; } // Nombre de la campaña
            public string Descripcion { get; set; } // Descripción de la campaña
            public int ProductoId { get; set; } // ID del producto seleccionado
            // public List<Producto> Productos { get; set; } // Lista de productos disponibles
            public DateTime FechaInicial { get; set; } // Fecha inicial de la campaña
            public DateTime FechaFinal { get; set; } // Fecha final de la campaña
        }
        public class Codigo
        {
            public string ReferenciadorId { get; set; } // ID del referenciador asociado
            public string CampanaId { get; set; } // ID de la campaña asociada
                                                  //  public string Codigo { get; set; } 
            public class ActivarDesactivar
        {
            public string Id { get; set; } // ID del elemento a activar/desactivar (referenciador/código/campaña)
            public bool Activo { get; set; } // Estado de activación (true = activo, false = inactivo)
        }
        public class Producto
        {
            public int Id { get; set; } // ID único del producto
            public string Nombre { get; set; } // Nombre del producto
        }
        public class GestionReferidosViewModel
        {
            public Referenciador Referenciador { get; set; } // Datos del referenciador
            public Campana Campana { get; set; } // Datos de la campaña
            public Codigo Codigo { get; set; } // Datos del código
            public ActivarDesactivar ActivarDesactivarReferenciador { get; set; } // Activación/desactivación de referenciador
            public ActivarDesactivar ActivarDesactivarCodigo { get; set; } // Activación/desactivación de código
            public ActivarDesactivar ActivarDesactivarCampana { get; set; } // Activación/desactivación de campaña
        }
    }


        public class ReferenciadorHeader
        {
          
            public int idEmpresa { get; set; }
           
            public int idResponsabilidad { get; set; }
           
            public string usuarioClave { get; set; }
           
            public string idUsuario { get; set; }
           
            public int idClaseCanalAtencion { get; set; }
           
            public int idCanalAtencion { get; set; }
          
            public int idPuntoAtencion { get; set; }
            
            public int idUbicacion { get; set; }
           
            public int idSucursal { get; set; }
           
            public int idComisionista { get; set; }
           
            public int idTransaccion { get; set; }
           
            public string ipHost { get; set; }
        }
        public class ReferenciadorRequest
        {
            
            public ReferenciadorHeader header { get; set; }
           
            public string cuenta { get; set; }
          
            public string compensacion { get; set; } //Compensacion solo puede tener valores (SI|NO)
    }

        public class ReferenciadorResponse
        {
            
            public int codigo { get; set; }
          
            public string mensaje { get; set; }

        public object data { get; set; }

    }
    public class ReferenciadorBuscarCuentaResponseData
    {
        public string cuenta { get; set; }
        public string nombre { get; set; }
        public string direccion { get; set; }
        public string curp { get; set; }
    }
    public class ReferenciadorResponseBuscarCuenta
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public ReferenciadorBuscarCuentaResponseData data { get; set; }
    }

    public class ReferenciadorActivarDesactivarRequest
    {
        public ReferenciadorHeader header { get; set; }
        public string producto { get; set; }
        public string status { get; set; } // "1" para activo, "0" para inactivo
    }


    public class ReferenciadorBuscarCuenta
    {
        public ReferenciadorHeader header { get; set; }
        public string cuenta { get; set; }
        
    }
    public class ReferenciadorListResponse
    {
       
        public int codigo { get; set; }
     
        public string mensaje { get; set; }
       
        public List<ReferenciadorData> data { get; set; }
    }

    public class ReferenciadorData
    {
      
        public int id { get; set; }
    
        public string cuenta { get; set; }

        public string nombre { get; set; }

        public string estatus { get; set; }
       
        public string compensacion { get; set; }
       
        public string fecha_creacion { get; set; }
      
        public string fecha_modificacion { get; set; }
    }
    // --- Modelos para /productos ---
    public class ProductoData
    {
     
        public int id { get; set; }
      
        public string nombre { get; set; }
       
        public string descripcion { get; set; }
       
        public string tipo { get; set; }
       
        public string proveedor { get; set; }
      
        public int usuario_creacion { get; set; }
        
        public string fecha_creacion { get; set; }
    }
    public class ProductoListResponse
    {
       
        public int codigo { get; set; }
        
        public string mensaje { get; set; }
       
        public List<ProductoData> data { get; set; }
    }
    // --- ViewModel para el Frontend ---
    // Este es el modelo que enviarás a la vista con la data para los selects
    public class AltaCodigoViewModel
    {
        public List<ProductoData> Productos { get; set; } = new List<ProductoData>();
        public List<ReferenciadorData> Referenciadores { get; set; } = new List<ReferenciadorData>();
    }
    public class AltaCodigoRequest
    {
        
        public ReferenciadorHeader header { get; set; }
      
        public string codigo { get; set; }
        
        public string descripcion { get; set; }
        
        public string estatus { get; set; } // Solo "ACTIVO" o "INACTIVO"
        
        public int productId { get; set; }
       
        public string fechaInicial { get; set; } // Formato "YYYY-MM-DD HH:MM:SS"
       
        public string fechaFinal { get; set; } // Formato "YYYY-MM-DD HH:MM:SS"
       
        public int referenciadorId { get; set; }
    }


    

    public class ReferenciadorDesactivarRequest
    {
        public ReferenciadorHeader header { get; set; }
        public string cuenta { get; set; }

        public string estatus { get; set; }

        public string compensacion { get; set; }
    }

    public class ReferenciadorDesactivarResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public object data { get; set; }
    }
    public class CodigoData
    {
        public int id { get; set; }
        public string codigoReferencia { get; set; }
        public string descripcion { get; set; }
        public int product_id { get; set; }
        public string estatus { get; set; }
        public string fechaInicial { get; set; }
        public string fechaFinal { get; set; }
        public string fechaCreacion { get; set; }
        public int referenciadorId { get; set; }
        public string cuentaReferenciador { get; set; }
    }

    public class CodigoListResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public List<CodigoData> data { get; set; }
    }



    public class CodigoModificarEstatusRequest
    {
        public ReferenciadorHeader header { get; set; }
        public int codigoId { get; set; }
        public string estatus { get; set; } // "ACTIVO" o "INACTIVO"
    }

    public class CodigoModificarEstatusResponse
    {
        public int codigo { get; set; }
        public string mensaje { get; set; }
        public object data { get; set; }
    }
}

