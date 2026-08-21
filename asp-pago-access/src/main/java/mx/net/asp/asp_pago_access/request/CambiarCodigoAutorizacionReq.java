package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarCodigoAutorizacionReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
//    @NotBlank(message = "El campo 'codigoAnterior' es obligatorio")
//    private String codigoAnterior;
    @NotBlank(message = "El campo 'codigoNuevo' es obligatorio")
    private String codigoNuevo;
    @NotBlank(message = "El campo 'usuarioId' es obligatorio")
    private String usuarioId;
//    @NotBlank(message = "El campo 'cuentaah' es obligatorio")
//    private String cuentaah;
    private Boolean biometrico;
}
