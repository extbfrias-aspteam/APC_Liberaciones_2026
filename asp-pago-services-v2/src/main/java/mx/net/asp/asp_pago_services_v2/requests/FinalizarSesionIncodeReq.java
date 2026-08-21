package mx.net.asp.asp_pago_services_v2.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_services_v2.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarSesionIncodeReq {
	private HeaderWS header;
	@NotBlank(message = "El campo 'sesionIncodeId' es obligatorio")
	private String sesionIncodeId;
	@NotBlank(message = "El campo 'cuentaah' es obligatorio")
	private String cuentaah;
	@NotBlank(message = "El campo 'estatus' es obligatorio")
	private String estatus;
}
