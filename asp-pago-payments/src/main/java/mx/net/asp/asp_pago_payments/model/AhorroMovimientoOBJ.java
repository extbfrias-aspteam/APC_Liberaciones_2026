package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroMovimientoOBJ {
    private Integer movimientoId;
    private String descripcion;
    private String operacion;
    private Integer creadoPor;
    private Date fechaCreacion;
    private Integer modificadoPor;
    private Date fechaModificacion;
    private String salvoBuenCobro;
    private String cuentaContable;
    private String provisionar;
    private String manual;
}
