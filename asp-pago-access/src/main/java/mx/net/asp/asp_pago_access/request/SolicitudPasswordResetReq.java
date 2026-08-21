package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPasswordResetReq {
    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
    private String telefono;
    @NotBlank(message = "El campo 'device_id' es obligatorio")
    private String device_id;
}
