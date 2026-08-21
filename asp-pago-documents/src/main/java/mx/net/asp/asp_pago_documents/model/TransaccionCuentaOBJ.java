package mx.net.asp.asp_pago_documents.model;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionCuentaOBJ {
    private Boolean resultado;
	private String cuenta;
	private Date fechaInicio;
	private Date fechaFin;
}
