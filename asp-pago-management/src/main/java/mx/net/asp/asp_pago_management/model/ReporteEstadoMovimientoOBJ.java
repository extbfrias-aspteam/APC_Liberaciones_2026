package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteEstadoMovimientoOBJ {
    private String cuenta;
	private Long fechaInicial;
	private Long fechaFinal;
}
