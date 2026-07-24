package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosDocProbatorio{
	private String entidadRegistro; 
	private String tomo; 
	private String claveMunicipioRegistro; 
	private String anioReg;
	private String claveEntidadRegistro; 
	private String foja; 
	private String numActa;
	private String libro; 
	private String municipioRegistro;
}