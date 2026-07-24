package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaSaldoAhorroV2Req {
	private HeaderWS header;
	@NotBlank(message = "El campo 'cuenta' es obligatorio")
	private String cuenta;
	private Integer idCanal;
}
