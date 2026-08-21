package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaCurpResponseOBJ {
	private String estatus;
	private String codigoValidacion;
	private String curp;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String sexo;
	private String fechaNacimiento;
	private String paisNacimiento;
	private String estadoNacimiento;
	private Integer docProbatorio;
	private DatosDocProbatorio datosDocProbatorio;
	private String estatusCurp;
	private String codigoMensaje;
}
