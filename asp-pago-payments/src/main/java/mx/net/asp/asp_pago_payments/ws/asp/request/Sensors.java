package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensors {
    @JsonProperty("bluetooth")
    private String bluetooth;

    @JsonProperty("gyroscope")
    private String gyroscope;

    @JsonProperty("deviceOrientation")
    private String deviceOrientation;

    @JsonProperty("accelerometer")
    private String accelerometer;

    @JsonProperty("devicePosition")
    private String devicePosition;

    @JsonProperty("devicePressure")
    private String devicePressure;

    @JsonProperty("stepCounter")
    private String stepCounter;

    @JsonProperty("devicePressureByPoint")
    private String devicePressureByPoint;

    @JsonProperty("deviceMotion")
    private String deviceMotion;

    @JsonProperty("nfc")
    private String nfc;
}
