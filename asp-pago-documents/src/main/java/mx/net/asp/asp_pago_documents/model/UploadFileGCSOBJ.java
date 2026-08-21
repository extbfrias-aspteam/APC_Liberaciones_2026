package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileGCSOBJ {
    private Long id_storage;
    private String message;
    private String objectName;
    private String fileName;
    private String categoria;
    private String identificador;
    private String storage;
}
