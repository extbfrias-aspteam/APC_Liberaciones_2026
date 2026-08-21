package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendimientoOBJ {
	private Integer id;
	private double porcentaje;
	private double gatNominal;
	private double gatReal;
	private Integer periodicidad;
}
