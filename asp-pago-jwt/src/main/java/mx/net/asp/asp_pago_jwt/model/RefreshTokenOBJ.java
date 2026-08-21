package mx.net.asp.asp_pago_jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenOBJ {
    private Long id;
    private String token;
    private String telefono;
    private String dispositivoId;
    private String tenantId;
    private String idCanal;
    private String estatus;
}
