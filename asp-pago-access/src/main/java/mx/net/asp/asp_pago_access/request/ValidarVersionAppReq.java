package mx.net.asp.asp_pago_access.request;

import jakarta.validation.constraints.Max;
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
public class ValidarVersionAppReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'versionApp' es obligatorio")
    private String versionApp;
    @NotNull(message = "El campo 'plataforma' no puede ser nula")
    @Min(value = 1, message = "El valor mínimo permitido es 1")
    @Max(value = 2, message = "El valor máximo permitido es 2")
    private Integer plataforma;
}
