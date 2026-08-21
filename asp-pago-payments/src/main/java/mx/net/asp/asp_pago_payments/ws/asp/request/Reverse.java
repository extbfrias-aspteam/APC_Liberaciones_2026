package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reverse {
    @JsonProperty("antiDetect")
    private String antiDetect;

    @JsonProperty("sslkiller")
    private String sslkiller;

    @JsonProperty("xposed")
    private String xposed;

    @JsonProperty("justrustme")
    private String justrustme;

    @JsonProperty("screnAttack")
    private String screnAttack;

    @JsonProperty("friday")
    private String friday;

    @JsonProperty("superUser")
    private String superUser;

    @JsonProperty("droidmon")
    private String droidmon;

}
