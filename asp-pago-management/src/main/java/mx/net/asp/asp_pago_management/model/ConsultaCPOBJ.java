package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaCPOBJ {
	private String estado;
	private String municipio;
	private String localidad;
	private Long coloniaId;
	private String colonia;
	private String cp;
}
