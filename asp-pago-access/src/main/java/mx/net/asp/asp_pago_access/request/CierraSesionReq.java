package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CierraSesionReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'personaId' es obligatorio")
    private String personaId;
}
