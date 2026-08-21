package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInReq {
//    @NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;

    @NotBlank(message = "El campo 'telefono' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
    private String telefono;
    @NotBlank(message = "El campo 'data' es obligatorio")
    private String data;
    @NotNull(message = "El campo 'idCanal' es obligatorio")
    @Min(value = 1, message = "El campo 'idCanal' no debe ser 0")
    private Integer idCanal;
}
