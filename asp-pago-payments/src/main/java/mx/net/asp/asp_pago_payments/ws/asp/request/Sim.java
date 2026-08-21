package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sim {
    @JsonProperty("isAirPlanModeOn")
    private String isAirPlanModeOn;

    @JsonProperty("lineNumber")
    private String lineNumber;

    @JsonProperty("simStatus")
    private String simStatus;
}