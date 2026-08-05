package mx.net.asp.vf_core_api.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProspectoOBJ {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer tipoTelefono;
    private String telefono;
    private String calle1;
    private String calle2;
    private String calle3;
    private String localidad;
    private String municipio;
    private String estado;
    private Integer tipoProducto;
    private Double monto;
    private Integer administradorComisionistasId;
}
