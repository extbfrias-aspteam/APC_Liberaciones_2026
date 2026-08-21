package mx.net.asp.asp_pago_jwt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantIdOBJ {
    private Boolean activo;
    private String tenantId;
    private Integer idCanal;
}
