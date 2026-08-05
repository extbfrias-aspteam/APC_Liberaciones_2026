package mx.net.asp.vf_core_api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDatos {
    @NotBlank(message = "El campo 'usuario' es obligatorio")
    private String usuario;
    @NotBlank(message = "El campo 'password' es obligatorio")
    private String password;
    @NotBlank(message = "El campo 'token' es obligatorio")
    private String token;
}
