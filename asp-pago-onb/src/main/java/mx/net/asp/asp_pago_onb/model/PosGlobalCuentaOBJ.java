package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosGlobalCuentaOBJ {
    private String tipoCuenta;
    private String cuentaah;
    private String clabe;
    private String estatus;
    private String cuenta_cobro;
    private String cuenta_pago;
}
