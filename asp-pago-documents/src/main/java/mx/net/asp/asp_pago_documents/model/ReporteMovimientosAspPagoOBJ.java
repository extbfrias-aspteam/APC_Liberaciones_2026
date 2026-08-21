package mx.net.asp.asp_pago_documents.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMovimientosAspPagoOBJ {
    private String fecha;
	private List<ObtenerMovimientosAspPagoOBJ> MovimientosAspPago;
}
