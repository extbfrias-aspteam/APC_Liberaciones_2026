package mx.net.asp.asp_pago_access.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroCodAutOBJ {
    private Integer id;
    private String personaId;
    private Integer intentos;
    private Boolean bloqueado;
    private Integer usuarioModificacion;
}
