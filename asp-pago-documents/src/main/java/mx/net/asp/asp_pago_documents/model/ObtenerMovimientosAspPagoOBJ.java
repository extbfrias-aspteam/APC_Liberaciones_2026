package mx.net.asp.asp_pago_documents.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObtenerMovimientosAspPagoOBJ {
    private long id;
	private String titulo;
	private String descripcion;
	private String concepto;
	private BigDecimal monto;
	private String fecha;
	private String tipo;
	private BigDecimal saldo;
	private String cveMovimiento;
	private RespuestaDetalleOBJ detalleMovimiento;
}
