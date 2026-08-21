package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioOBJ {
	private Long id;
	private Integer cuentaId;
	private String cuentaah;
	private String nombreBeneficiario;
	private Integer relacionId;
	private Integer porcentaje;
	private Boolean detalleEstatus;
}
