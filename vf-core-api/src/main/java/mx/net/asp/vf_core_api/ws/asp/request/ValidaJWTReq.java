package mx.net.asp.vf_core_api.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaJWTReq {
    private String jwtToken;
    private String endpoint;
    private String personaId;
    private String telefono;
    private Integer IdCanal;
}

