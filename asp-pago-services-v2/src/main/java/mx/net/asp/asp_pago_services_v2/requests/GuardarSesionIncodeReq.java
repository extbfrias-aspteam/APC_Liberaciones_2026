package mx.net.asp.asp_pago_services_v2.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_services_v2.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardarSesionIncodeReq {
//	@NotNull(message = "El campo 'header' es obligatorio")
	private HeaderWS header;
	@NotBlank(message = "El campo 'sesionIncodeId' es obligatorio")
	private String sesionIncodeId;
	@NotBlank(message = "El campo 'token' es obligatorio")
	private String token;
	@NotBlank(message = "El campo 'cuentaah' es obligatorio")
	private String cuentaah;
	@NotNull(message = "El campo 'giroId' es obligatorio")
	@Min(value = 1, message = "El campo 'moduloId' no debe ser 0")
	private Integer moduloId;
}
