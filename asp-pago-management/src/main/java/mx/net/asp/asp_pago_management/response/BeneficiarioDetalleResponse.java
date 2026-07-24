package mx.net.asp.asp_pago_management.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioDetalleResponse {
	private String nombre;
	private String parentesco;
}