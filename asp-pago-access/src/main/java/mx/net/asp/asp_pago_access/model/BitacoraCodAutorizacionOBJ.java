package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraCodAutorizacionOBJ {
    private Long id;
    private Integer id_servicio;
    private String id_usuario;
    private String cod_autorizacion;
    private Boolean estatus;
    private String observaciones;
}
