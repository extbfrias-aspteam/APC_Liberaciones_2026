package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarTokenCambioPassReq {
    @NotBlank(message = "El campo 'token' es obligatorio")
    private String token;
}
