package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetControlOBJ {
    private Long id;
    private String personaId;
    private String deviceId;
    private String tipoControl;
    private Integer intentos;
    private LocalDateTime fechaInicioVentana;
    private Boolean bloqueado;
    private LocalDateTime fechaBloqueo;
}