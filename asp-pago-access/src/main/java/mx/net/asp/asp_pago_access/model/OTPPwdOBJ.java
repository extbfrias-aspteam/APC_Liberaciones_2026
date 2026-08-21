package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPPwdOBJ {
    private Long id;
    private String personaId;
    private String otpHash;
    private String deviceId;
    private LocalDateTime fecha_creacion;
    private LocalDateTime fecha_expira;
    private Boolean activo;
}
