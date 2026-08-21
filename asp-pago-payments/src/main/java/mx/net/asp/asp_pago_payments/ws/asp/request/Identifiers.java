package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Identifiers {
    @JsonProperty("hash")
    private String hash;

    @JsonProperty("anonymity1")
    private String anonymity1;

    @JsonProperty("anonymity2")
    private String anonymity2;

    @JsonProperty("anonymity3")
    private String anonymity3;

    @JsonProperty("anonymity5")
    private String anonymity5;
}
