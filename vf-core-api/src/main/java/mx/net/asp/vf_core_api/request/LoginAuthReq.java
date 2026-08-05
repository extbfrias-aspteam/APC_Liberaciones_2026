package mx.net.asp.vf_core_api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuthReq {
    private String tenantId;
    private String path;
    private Integer idCanal;
}
