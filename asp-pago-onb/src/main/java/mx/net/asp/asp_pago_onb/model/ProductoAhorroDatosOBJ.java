package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAhorroDatosOBJ {
	private Integer id;
	private Integer producto_id;
	private Integer concepto_id;
	private Integer estatus_id;
	private String valor;
	private String clave;
}
