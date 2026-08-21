package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.utilerias.ValidacionTelefonoCondicional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidacionTelefonoCondicional
public class ValidaTelefonoReq {
	@NotBlank(message = "El campo 'telefono' es obligatorio")
	@Pattern(regexp = "^\\d{10}$", message = "El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
	private String telefono;
    @NotBlank(message = "El campo 'token' es obligatorio")
	private String token;
	@NotNull(message = "El campo 'validaCuenta' es obligatorio")
	private Integer validaCuenta;
	private String curp;
	private String idDispositivo;
}
