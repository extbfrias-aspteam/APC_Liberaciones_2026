package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosPosGlobalOBJ {
    private String estatus;
    private String tipoCuenta;
    private String cuenta;
    private String referencia;
    private BigDecimal saldoDisponible;
    private BigDecimal saldoTotal;
    private String idPersona;
    private String cuentaClabe;
    private String tarjeta;
}
