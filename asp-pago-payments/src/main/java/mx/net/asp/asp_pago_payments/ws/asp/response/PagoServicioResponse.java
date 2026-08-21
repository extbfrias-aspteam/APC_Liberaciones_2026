package mx.net.asp.asp_pago_payments.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoServicioResponse {
    private int resultado;
    private String respuesta;
    private String msgHost;
    private String referencia;
    private String numAutorizacion;
    private int resultadoCatel;
}
