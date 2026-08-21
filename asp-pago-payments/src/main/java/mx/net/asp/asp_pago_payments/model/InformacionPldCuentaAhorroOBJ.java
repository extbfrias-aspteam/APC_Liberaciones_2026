package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacionPldCuentaAhorroOBJ {
    private Integer id;
    private Integer cuentaId;
    private BigDecimal montoMaxAho;
    private BigDecimal ingresosMen;
    private String puesto;
    private Integer estatusId;
}
