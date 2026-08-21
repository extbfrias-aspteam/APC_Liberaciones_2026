package mx.net.asp.asp_pago_jwt.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_jwt.utilerias.validation.ValidaJWTConstraint;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidaJWTConstraint
public class ValidaJWTReq {
    private String jwtToken;
    @NotBlank(message = "El campo 'endpoint' es obligatorio")
    private String endpoint;
    private String personaId;
    private String telefono;
    private Integer idCanal;
}