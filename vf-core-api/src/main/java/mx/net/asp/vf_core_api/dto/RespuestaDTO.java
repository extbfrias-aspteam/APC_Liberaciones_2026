package mx.net.asp.vf_core_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class RespuestaDTO {
	// Variable para indicar el codigo de respuesta
	private int codigo;
	// Variable para indicar el mensaje de respuesta
	private String mensaje;
	// Varieble para regresar los datos de respuesta
	private String data;
}