package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionIncodeOBJ {
	private String sesionIncodeId;
	private Boolean activo;
	private String estatus;
}
