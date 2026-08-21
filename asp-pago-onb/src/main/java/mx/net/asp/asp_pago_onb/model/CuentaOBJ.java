package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaOBJ {
    private Integer cuentaId;
    private String cuenta;
    private String clabe;
    private Integer productoId;
    private String estatus;
    private String tipoCuenta;
    private boolean tieneCuenta;
}
