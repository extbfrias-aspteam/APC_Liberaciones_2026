package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInDataCifOBJ {
//    @NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    private String telefono;
    private String password;
    private String token;
    private String dispositivoId;
    private String latitud;
    private String longitud;
    private Integer idCanal;
}
