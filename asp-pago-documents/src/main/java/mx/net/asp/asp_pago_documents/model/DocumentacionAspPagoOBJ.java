package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentacionAspPagoOBJ {
    private Long id;
    private String categoria;        // biometria, contratos, etc
    private String identificador;    // CURP o equivalente
    private String nombreArchivo;    // nombre_archivo
    private String rutaArchivo;      // ruta_archivo
    private String storageTipo;      // LOCAL | BUCKET
    private String estado;           // GUARDADO_LOCAL | ERROR_GUARDADO_LOCAL | GUARDADO_BUCKET
    private String origen;           // ONBOARDING | POST-LOGIN
    private String uuidPeticion;
}
