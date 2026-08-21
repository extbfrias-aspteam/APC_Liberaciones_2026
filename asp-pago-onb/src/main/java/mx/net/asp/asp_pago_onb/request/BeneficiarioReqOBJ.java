package mx.net.asp.asp_pago_onb.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioReqOBJ {
@NotBlank(message = "El campo 'nombre' es obligatorio")
    private String nombre;
    @NotBlank(message = "El campo 'primer_apellido' es obligatorio")
    private String primer_apellido;
    @NotBlank(message = "El campo 'segundo_apellido' es obligatorio")
    private String segundo_apellido;
    @NotNull(message = "El campo 'parentesco' es obligatorio")
    @Min(value = 1, message = "El campo 'parentesco' no debe ser 0")
    private Integer parentesco;
    @NotNull(message = "El campo 'porcentaje' es obligatorio")
    @Min(value = 100, message = "El campo 'porcentaje' debe ser igual a 100")
    @Max(value = 100, message = "El campo 'porcentaje' debe ser igual a 100")
    private Double porcentaje;
}
