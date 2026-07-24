package mx.net.asp.asp_pago_management.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultarTarjetaReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    private String tipoTarjeta;
    private String numeroTarjeta;
    private String token;
}
