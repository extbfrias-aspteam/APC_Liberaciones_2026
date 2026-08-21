package mx.net.asp.asp_pago_documents.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraDocCtaInversionReq {
    @NotBlank(message = "El campo 'cuentaInversion' es obligatorio")
    private String cuentaInversion;
    private Boolean subirDoc;
}
