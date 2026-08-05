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
public class ProcesaReq {
    @NotNull(message = "El campo 'accesoId' es obligatorio")
    @Min(value = 1, message = "El campo 'accesoId' no debe ser 0")
    private Integer accesoId;
    @NotNull(message = "El campo 'tipo' es obligatorio")
    private Integer tipo;
    @NotBlank(message = "El campo 'map' es obligatorio")
    private String map;
    @NotBlank(message = "El campo 'proceso' es obligatorio")
    private String proceso;
    @Valid
    @NotNull(message = "El campo 'usuarioDatos' es obligatorio")
    private UsuarioDatos usuarioDatos;
}
