package mx.net.asp.asp_pago_documents.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaDetalleOBJ {
    private String emisor;
	private String beneficiario;
	private String origen;
	private String destino;
	private String bancoOrigen;
	private String bancoDestino;
	private List<DetalleMovOBJ> detalle;
}
