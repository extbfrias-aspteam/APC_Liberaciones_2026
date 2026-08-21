package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Network {
    @JsonProperty("privateIp")
    private String privateIp;

    @JsonProperty("detectNetwork")
    private String detectNetwork;

    @JsonProperty("celId")
    private String celId;

    @JsonProperty("isoCountryCode")
    private String isoCountryCode;

    @JsonProperty("macAddress")
    private String macAddress;

    @JsonProperty("mcc")
    private String mcc;

    @JsonProperty("CarrierName")
    private String CarrierName;

    @JsonProperty("allowIp")
    private String allowIp;

    @JsonProperty("networkCode")
    private String networkCode;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("mnc")
    private String mnc;
}
