package mx.net.asp.asp_pago_management.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraRteEdoMto {
    private HeaderWS header;
    private String cuenta;
	private Long fechaInicial;
	private Long fechaFinal;
    private Boolean subirDoc;
}
