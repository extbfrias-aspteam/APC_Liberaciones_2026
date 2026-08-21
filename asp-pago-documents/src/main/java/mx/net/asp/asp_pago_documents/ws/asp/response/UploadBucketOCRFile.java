package mx.net.asp.asp_pago_documents.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadBucketOCRFile {
    private String fileUri;
    private String mimeType;
    private boolean wasFused;
    private int originalFileCount;
}
