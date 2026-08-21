package mx.net.asp.asp_pago_documents.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdoMovtosOBJ {
    private String nombre_cliente;
	private String numero_cuenta;
	private String periodo;
	private String fecha_inicio;
	private String fecha_final;
	private Double saldo_real;
	List<ReporteMovimientosAspPagoOBJ> listaReportesEdoMovtos;
	private String mailFrom;
	private String mailTo;
	private String subject;
	private String mailBody;
}
