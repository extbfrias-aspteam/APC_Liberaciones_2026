package mx.net.asp.vf_core_api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerTokenReq {
    @NotBlank(message = "El campo 'clave' es obligatorio")
    private String password;
    @NotBlank(message = "El campo 'nombre' es obligatorio")
    private String usuario;
}
