package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingEjecutadoOBJ {
	private Integer id;
	private String telefono;
	private String curp;
	private String rfc;
	private Integer completado;
	private String observacion;
	private Timestamp fechaCreacion;
	private Timestamp fechaModificacion;
}
