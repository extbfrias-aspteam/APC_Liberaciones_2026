package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetSessionOBJ {
    private Long id;
    private String id_session;
    private String personaId;
    private String deviceId;
    private LocalDateTime fecha_creacion;
    private LocalDateTime fecha_expira;
    private Boolean activo;
    private Boolean usada;
}
