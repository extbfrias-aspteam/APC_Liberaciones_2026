package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeticionCambioPassReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'correo' es obligatorio")
    private String correo;
    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
    private String telefono;
}
