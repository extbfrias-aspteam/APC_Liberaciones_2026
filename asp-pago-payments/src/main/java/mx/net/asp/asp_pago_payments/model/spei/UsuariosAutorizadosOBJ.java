package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuariosAutorizadosOBJ implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String estatusId;
    private Date fechaBaja;
    private String motivoBajaId;
    private Date fechaCreacion;
    private String empresaId;
    private BigDecimal montoInferior;
    private BigDecimal montoSuperior;
    private String modulo;
    private String autorizado;
    private Date fechaIni;
    private Date fechaFin;
    private String codigo;
    private String nombreUsuario;

}
