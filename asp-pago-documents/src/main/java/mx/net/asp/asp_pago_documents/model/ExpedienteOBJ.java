package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteOBJ {
	private String nombre;
	private String extension;
	private String documento;
	private Integer identificador_documento;
	private String observaciones;
}
