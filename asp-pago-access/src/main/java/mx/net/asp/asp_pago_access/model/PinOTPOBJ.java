package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PinOTPOBJ {
    private Long id;
    private String personaId;
    private String pin_otp;
    private Boolean activo;
    private Timestamp fechaCreacion;
    private Timestamp fechaExpira;
}
