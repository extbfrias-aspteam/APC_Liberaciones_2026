package mx.net.asp.asp_pago_documents.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidatorOCRFileItem {
    private String fileUri;
    private String mimeType;
}
