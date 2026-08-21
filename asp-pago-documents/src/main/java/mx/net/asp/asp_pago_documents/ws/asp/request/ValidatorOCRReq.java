package mx.net.asp.asp_pago_documents.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorOCRReq {
    private String requestType;
    private String requestId;
    private String documentType;
    private String requestSource;
    private List<ValidatorOCRFileItem> fileList;
}
