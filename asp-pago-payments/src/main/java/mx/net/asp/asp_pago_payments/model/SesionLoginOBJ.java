package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionLoginOBJ {
    private Integer id;
    private String personaId;
    private Instant fecha_login;
    private Timestamp fecha_cierre;
    private String estatus;
    private String latitud;
    private String longitud;
}
