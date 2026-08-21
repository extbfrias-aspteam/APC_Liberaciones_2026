package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudeCheckReq {
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("PbluId")
    private String PbluId;
    @JsonProperty("CveRastreo")
    private String CveRastreo;
    @JsonProperty("MonitorPlusId")
    private String MonitorPlusId;
    @JsonProperty("Transaction")
    private FraudeCheckTransaction Transaction;
}