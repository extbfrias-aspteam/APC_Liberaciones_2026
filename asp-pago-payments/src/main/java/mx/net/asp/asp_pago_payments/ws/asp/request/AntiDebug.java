package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntiDebug {
    @JsonProperty("underDebug")
    private String underDebug;

    @JsonProperty("isDeveloperModeEnabled")
    private String isDeveloperModeEnabled;

    @JsonProperty("playstore")
    private String playstore;
}
