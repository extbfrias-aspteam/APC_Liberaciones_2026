package mx.net.asp.asp_pago_documents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileGCSDTO {
    private String message;
    private String fileName;
    private String storage;
}
