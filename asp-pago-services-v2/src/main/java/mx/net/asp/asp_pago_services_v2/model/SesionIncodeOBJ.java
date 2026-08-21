package mx.net.asp.asp_pago_services_v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionIncodeOBJ {
	private String sesionIncodeId;
	private Integer moduloId;
	private String moduloDescripcion;
	private String token;
}
