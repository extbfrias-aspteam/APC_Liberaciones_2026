package mx.net.asp.asp_pago_documents.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorOCRResponse {
    private DocumentInfo documentInfo;
    private String message;
    private String agentId;
}
