package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditoOBJ {
    private String cuentaClabe;
    private String referencia;
    private String control;
    private BigDecimal monto;
}
