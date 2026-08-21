package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioPasswordReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'password' es obligatorio")
    private String password;
    @NotBlank(message = "El campo 'confPassword' es obligatorio")
    private String confPassword;
    @NotBlank(message = "El campo 'token' es obligatorio")
    private String token;
}
