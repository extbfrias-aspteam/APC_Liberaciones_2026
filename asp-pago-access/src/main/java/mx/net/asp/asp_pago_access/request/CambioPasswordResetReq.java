package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioPasswordResetReq {
    @NotBlank(message = "El campo 'nueva_password' es obligatorio")
    private String nueva_password;
    @NotBlank(message = "El campo 'device_id' es obligatorio")
    private String device_id;
    @NotBlank(message = "El campo 'resetSessionToken' es obligatorio")
    private String resetSessionToken;
}
