package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntradaConsultarMovimientosConcentradoOBJ {
	private String cuentaASP;
	private String fechaInicial;
	private String fechaFinal;
}
