package mx.net.asp.asp_pago_payments.model;

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
    private String telefonoCoDi;
    private String password;
    private String idHardware;
    private String telefono_validado;
    private String folioContrato;
    private Boolean activo;
    private Integer bloqueado;
    private Timestamp fechaBloqueo;
    private Integer intentos;
    private Integer bloqueosTemporales;
    private Integer estatus;
}
