package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FingerPrint {
    @JsonProperty("Sim")
    private Sim Sim;

    @JsonProperty("General")
    private General General;

    @JsonProperty("Sensors")
    private Sensors Sensors;

    @JsonProperty("Device")
    private Device Device;

    @JsonProperty("Geoip")
    private Geoip Geoip;

    @JsonProperty("Identifiers")
    private Identifiers Identifiers;

    @JsonProperty("AntiDebug")
    private AntiDebug AntiDebug;

    @JsonProperty("Geolocation")
    private Geolocation Geolocation;

    @JsonProperty("Network")
    private Network Network;

    @JsonProperty("Reverse")
    private Reverse Reverse;
}
