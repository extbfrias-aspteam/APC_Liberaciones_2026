package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenCambioPassOBJ {
    private Integer id;
    private String token;
    private String cuenta;
    private String correo;
    private Timestamp fechaGenerado;
    private Timestamp fechaExpira;
    private Integer estatus;
    private String folio;
}
