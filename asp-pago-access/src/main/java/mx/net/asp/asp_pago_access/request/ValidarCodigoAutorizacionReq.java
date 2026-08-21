package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarCodigoAutorizacionReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'codigo' es obligatorio")
    private String codigo;
    @NotBlank(message = "El campo 'usuarioId' es obligatorio")
    private String usuarioId;
//    @NotBlank(message = "El campo 'cuentaah' es obligatorio")
//    private String cuentaah;
    @NotNull(message = "El campo 'servicioId' es obligatorio")
    @Min(value = 1, message = "El campo 'moduloId' no debe ser 0")
    private Integer servicioId;
    private Boolean biometrico;
}
