package mx.net.asp.asp_pago_jwt.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResp {
    private String access_token;
    private String refresh_token;
    private String expires_in;
}
