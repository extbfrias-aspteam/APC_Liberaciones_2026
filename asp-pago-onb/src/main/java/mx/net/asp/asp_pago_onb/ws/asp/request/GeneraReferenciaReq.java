package mx.net.asp.asp_pago_onb.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_onb.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraReferenciaReq {
    private HeaderWS header;
    private Integer producto;
    private Integer tipoReferencia;
}
