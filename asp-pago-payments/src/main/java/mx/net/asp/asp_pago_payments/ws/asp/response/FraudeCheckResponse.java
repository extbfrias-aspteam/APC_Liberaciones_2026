package mx.net.asp.asp_pago_payments.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudeCheckResponse {
    private boolean data;
    private boolean success;
    private String message;
    private Integer code;
}
