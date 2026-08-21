package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RespuestaSVC {
	private RespuestaBodySVC body;
	private RespuestaErrorSVC errores;

	public RespuestaSVC(){
		body = new RespuestaBodySVC();
		errores = new RespuestaErrorSVC();
	}
}
