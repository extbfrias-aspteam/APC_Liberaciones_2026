package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroDisposicionesOBJ {
    private Integer id;
    private String cuenta;
    private Date fecha;
    private Double monto;
    private Integer formaPagoId;
    private Integer bancoId;
    private String cheque;
    private Integer movtoId;
    private Integer creadoPor;
    private Timestamp fechaCreacion;
    private Integer modificadoPor;
    private Timestamp fechaModificacion;
    private Integer avisoId;
    private Integer transaccionId;
    private Integer tarjetaOperativaId;
    private Integer app;
    private Integer transaccionVersionId;

}
