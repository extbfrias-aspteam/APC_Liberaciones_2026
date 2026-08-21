package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroCoDiOBJ {
    private Integer id;
    private String cuenta;
    private String solicitanteId;
    private String registroNuevaApp;
    private String password;
    private String idHardware;
    private String telefono_validado;
    private Boolean activo;
    private Integer bloqueado;
    private Timestamp fechaBloqueo;
    private Integer intentos;
    private Integer bloqueosTemporales;
}
