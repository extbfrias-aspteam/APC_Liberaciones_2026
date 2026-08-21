package mx.net.asp.asp_pago_jwt.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_jwt.model.HeaderWS;
import mx.net.asp.asp_pago_jwt.utilerias.validation.LoginAuthConstraint;

@Data
@NoArgsConstructor
@AllArgsConstructor
@LoginAuthConstraint
public class LoginAuthReq {
    private HeaderWS header;
    private String telefono;
    private String dispositivoId;
    private String tenantId;
    @NotBlank(message = "El campo 'path' es obligatorio")
    private String path;
    @NotNull(message = "El campo 'idcanal' es obligatorio")
    @Min(value = 1, message = "El campo 'idcanal' no debe ser 0")
    private Integer idCanal;
}