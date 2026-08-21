package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;
import mx.net.asp.asp_pago_access.utilerias.ValidacionGeneraPinOTP;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidacionGeneraPinOTP
public class GeneraPinOTPReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'clave' es obligatorio")
    private String clave;
    @NotBlank(message = "El campo 'valor' es obligatorio")
    private String valor;
    @NotNull(message = "El campo 'usuarioId' es obligatorio")
    private Integer usuarioId;
}
