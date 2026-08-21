package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarCodigoReq {
    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
    private String telefono;
    private String codigo;
    private boolean evaluaProveedor;
}