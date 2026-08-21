package mx.net.asp.asp_pago_onb.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_onb.utilerias.ValidacionDeCorreoCondicional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidacionDeCorreoCondicional
public class ValidacionDeCorreoReq {
    private Long id;

    @NotBlank(message = "El campo 'correoElectronico' es obligatorio")
    private String correoElectronico;

    private String token;
    @NotBlank(message = "El campo 'rfc' es obligatorio")
    private String rfc;
    @NotBlank(message = "El campo 'curp' es obligatorio")
    @Size(min = 18, max = 18, message = "El campo 'curp' debe tener 18 caracteres.")
    private String curp;

    @NotBlank(message = "El campo 'accion' es obligatorio")
    @Pattern(regexp = "^[0-2]$", message = "El campo 'accion' debe ser un dígito entre 0 y 2")
    private String accion;

    private String estado;
    private String nombre;
}
