package mx.net.asp.asp_pago_onb.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroCuentaSimplificadaResponse {
    private String mensaje;
    private String cuenta_id;
    private String clabe;
    private String folioContrato;
    private String persona_id;
    private String cuenta;
}
