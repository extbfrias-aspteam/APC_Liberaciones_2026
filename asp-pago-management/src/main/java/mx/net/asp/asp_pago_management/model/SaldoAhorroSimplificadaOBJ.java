package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaldoAhorroSimplificadaOBJ {
	private Double saldoAhorro;
	private Double saldoAcumulado;
	private Double saldoMensualDisponible;
}
