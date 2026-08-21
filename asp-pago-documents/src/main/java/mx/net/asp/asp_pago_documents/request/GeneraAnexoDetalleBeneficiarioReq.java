package mx.net.asp.asp_pago_documents.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraAnexoDetalleBeneficiarioReq {
    @NotBlank(message = "El campo 'cuentaah' es obligatorio")
    private String cuentaah;
    private Boolean subirDoc;
}
