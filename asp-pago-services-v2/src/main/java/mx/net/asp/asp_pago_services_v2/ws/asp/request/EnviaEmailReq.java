package mx.net.asp.asp_pago_services_v2.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_services_v2.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviaEmailReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    private String destinatario;
    private String asunto;
    private String contenido;
}
