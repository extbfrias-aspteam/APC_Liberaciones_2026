package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosOrdenanteOBJ {
    private String nombreOrdenante;
    private Integer idTipoCuentaOrdenante;
    private String cuentaOrdenante;
    private Integer idInstitucionOrdenante;
}
