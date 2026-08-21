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
public class ValidarCurpCuentaReq {
	private HeaderWS header;
	@NotBlank(message = "El campo 'cuentaah' es obligatorio")
	private String cuentaah;
	@NotBlank(message = "El campo 'curp' es obligatorio")
	private String curp;
}
