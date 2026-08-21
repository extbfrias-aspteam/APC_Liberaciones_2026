package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geoip {
    @JsonProperty("as")
    private String as;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("isp")
    private String isp;

    @JsonProperty("country")
    private String country;

    @JsonProperty("region")
    private String region;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("proxy")
    private String proxy;

    @JsonProperty("org")
    private String org;

    @JsonProperty("district")
    private String district;

    @JsonProperty("continent")
    private String continent;

    @JsonProperty("regionName")
    private String regionName;

    @JsonProperty("countryCode3")
    private String countryCode3;

    @JsonProperty("reverse")
    private String reverse;

    @JsonProperty("asname")
    private String asname;

    @JsonProperty("offset")
    private String offset;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("city")
    private String city;

    @JsonProperty("status")
    private String status;

    @JsonProperty("continentCode")
    private String continentCode;

    @JsonProperty("query")
    private String query;

    @JsonProperty("hosting")
    private String hosting;

    @JsonProperty("currentTime")
    private String currentTime;
}
