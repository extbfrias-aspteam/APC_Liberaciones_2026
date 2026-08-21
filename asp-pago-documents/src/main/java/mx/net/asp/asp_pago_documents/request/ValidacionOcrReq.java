package mx.net.asp.asp_pago_documents.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionOcrReq {
    @NotBlank(message = "El campo 'id' es obligatorio")
    private String id;
    @NotBlank(message = "El campo 'idReverso' es obligatorio")
    private String idReverso;
    @NotBlank(message = "El campo 'fotoSelfie' es obligatorio")
    private String fotoSelfie;
}
