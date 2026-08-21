package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudeCheckHeader {
    @JsonProperty("date")
    private String date;
    @JsonProperty("time")
    private String time;
    @JsonProperty("event")
    private String event;
    @JsonProperty("nicknamenode")
    private String nicknamenode;
    @JsonProperty("nicknamemodule")
    private String nicknamemodule;
    @JsonProperty("user")
    private String user;
    @JsonProperty("rtind")
    private String rtind;
}
