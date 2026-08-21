package mx.net.asp.asp_pago_documents.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_documents.model.ExpedienteOBJ;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpedienteReq {
	private String cuenta;
	private ExpedienteOBJ expediente;
	private String ruta;
	private String usuario_creacion;
	private String modulo;
}
