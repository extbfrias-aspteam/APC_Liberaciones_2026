package mx.net.asp.asp_pago_payments.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesamientoSpeiSimpleResponse {
    private String estatusOperacion;
    private Double monto;
    private String beneficiario;
    private String referencia;
    private String claveRastreo;
}
