package mx.net.asp.asp_pago_payments.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthData {
    private boolean success;
    private String token;
}
