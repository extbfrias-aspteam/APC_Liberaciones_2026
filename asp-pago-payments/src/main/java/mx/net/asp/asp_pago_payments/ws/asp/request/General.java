package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class General {
    @JsonProperty("CertifiedID")
    private String CertifiedID;

    @JsonProperty("voiceMailerNumber")
    private String voiceMailerNumber;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("AppAction")
    private String AppAction;

    @JsonProperty("processorCount")
    private String processorCount;

    @JsonProperty("typeAuthentication")
    private String typeAuthentication;

    @JsonProperty("fingerprintVersion")
    private String fingerprintVersion;

    @JsonProperty("geolocation")
    private String geolocation;

    @JsonProperty("timezoneName")
    private String timezoneName;

    @JsonProperty("packageName")
    private String packageName;

    @JsonProperty("screenResolution")
    private String screenResolution;

    @JsonProperty("timezoneID")
    private String timezoneID;

    @JsonProperty("languageCountry")
    private String languageCountry;

    @JsonProperty("hardwareSerial")
    private String hardwareSerial;

    @JsonProperty("appVersion")
    private String appVersion;

    @JsonProperty("language")
    private String language;

    @JsonProperty("appName")
    private String appName;
}
