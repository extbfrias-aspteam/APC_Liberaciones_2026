package mx.net.asp.asp_pago_documents.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadBucketOCRResponse {
    private List<UploadBucketOCRFile> fileList;
    private String message;
    private String agentId;
}
