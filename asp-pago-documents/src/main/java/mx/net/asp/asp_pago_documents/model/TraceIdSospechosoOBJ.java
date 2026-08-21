package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceIdSospechosoOBJ {

	private String traceId;
    private String traceIdSigned;
    private String endpoint;
    private String ipCliente;
    private String observaciones;
}
