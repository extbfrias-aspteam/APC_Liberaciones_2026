package mx.net.asp.asp_pago_jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimsTokenOBJ {
    private String telefono;
    private String dispositivoId;
    private String tenantId;
    private Integer idCanal;
}
