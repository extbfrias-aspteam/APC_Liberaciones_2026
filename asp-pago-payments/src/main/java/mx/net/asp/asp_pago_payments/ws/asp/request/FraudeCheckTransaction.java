package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudeCheckTransaction {
    @JsonProperty("Header")
    private FraudeCheckHeader header;
    @JsonProperty("Fields")
    private FraudeCheckFields Fields;
}