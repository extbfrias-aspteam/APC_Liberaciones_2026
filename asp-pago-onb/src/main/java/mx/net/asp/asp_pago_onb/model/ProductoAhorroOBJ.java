package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAhorroOBJ {
	private Integer id;
	private String clave;
	private String descripcion;
	private Double montoMinimo;
	private Double montoMax;
	private String fechaActivacion;
	private Integer estatusId;
	private Integer monedaId;
	private Integer claveCnbv;
	private Integer tipoProductoAhorroId;
}
