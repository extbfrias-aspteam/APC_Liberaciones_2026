package mx.net.asp.asp_pago_jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenOBJ {
    private Long id;
    private Long refreshTokenId;
    private String token;
    private String telefono;
    private String dispositivoId;
    private String tenantId;
    private String idCanal;
    private String estatus;
}
