package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistraTokenHuellaReq {
    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'celular' debe contener exactamente 10 dígitos numéricos")
    private String telefono;
    @NotBlank(message = "El campo 'data' es obligatorio")
    private String data;
}
