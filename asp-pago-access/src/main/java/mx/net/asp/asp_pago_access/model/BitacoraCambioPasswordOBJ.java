package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraCambioPasswordOBJ {
    private Long id;
    private String valor_referencia;
    private String tipo_referencia;
    private String device_id;
    private String tipo_evento;
    private String resultado;
    private String observaciones;
}
