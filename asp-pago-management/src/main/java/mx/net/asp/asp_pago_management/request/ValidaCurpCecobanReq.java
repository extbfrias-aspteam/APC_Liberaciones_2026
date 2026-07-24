package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaCurpCecobanReq {
    // @NotBlank(message = "El campo 'cuenta' es obligatorio")
    // private String cuenta;
    @NotBlank(message = "El campo 'curp' es obligatorio")
    @Size(min = 18, max = 18, message = "El campo 'curp' debe tener exactamente 18 caracteres")
    private String curp;
    @NotBlank(message = "El campo 'apellidoPaterno' es obligatorio")
    private String apellidoPaterno;
    @NotBlank(message = "El campo 'nombre' es obligatorio")
	private String nombre;
    @NotBlank(message = "El campo 'sexo' es obligatorio")
    @Pattern(regexp = "^[HM]$", message = "El campo 'sexo' debe ser 'H' o 'M'")
	private String sexo;
    @NotBlank(message = "El campo 'fechaNacimiento' es obligatorio")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "El campo 'fechaNacimiento' debe tener el formato dd/MM/yyyy")
	private String fechaNacimiento;
    @NotBlank(message = "El campo 'entidadFederativaNacimiento' es obligatorio")
    @Size(min = 2, max = 2, message = "El campo 'entidadFederativaNacimiento' debe tener exactamente 2 caracteres")
	private String entidadFederativaNacimiento;
    private String apellidoMaterno;
}
