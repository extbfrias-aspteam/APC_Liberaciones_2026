package mx.net.asp.asp_pago_access.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevocaJwtTokenClienteReq {
    private String telefono;
}