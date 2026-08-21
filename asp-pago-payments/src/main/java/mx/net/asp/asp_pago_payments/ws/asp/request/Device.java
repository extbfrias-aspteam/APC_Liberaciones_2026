package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("imei")
    private String imei;

    @JsonProperty("androidId")
    private String androidId;

    @JsonProperty("hostName")
    private String hostName;

    @JsonProperty("imsi")
    private String imsi;

    @JsonProperty("isDeviceRoot")
    private String isDeviceRoot;

    @JsonProperty("isEmulator")
    private String isEmulator;

    @JsonProperty("kernel")
    private String kernel;

    @JsonProperty("deviceModel")
    private String deviceModel;

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("operativeSystem")
    private String operativeSystem;

    @JsonProperty("deviceOs")
    private String deviceOs;

    @JsonProperty("deviceBrand")
    private String deviceBrand;

}
