package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroTransferenciaOBJ {
    private Integer id;
    private String cuentaOrigen;
    private String cuentaDestino;
    private Date fecha;
    private Double monto;
    private Integer movimientoId;
    private Integer creadoPor;
    private Integer modificadoPor;
    private Integer depositoId;
    private Integer disposicionId;
    private Integer spei;
}
