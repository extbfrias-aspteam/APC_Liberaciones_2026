package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoOBJ {
    private String fecha;
	private String detalle;
	private String cargo;
	private String abono;
	private String saldo;
}
