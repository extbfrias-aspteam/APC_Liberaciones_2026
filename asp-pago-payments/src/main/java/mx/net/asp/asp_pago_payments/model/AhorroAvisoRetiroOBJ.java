package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroAvisoRetiroOBJ {
    private Integer avisoId;
    private String cuenta;
    private Date fechaRetiro;
    private Double monto;
    private String estatus;
    private Integer autorizadoPor;
    private Integer creadoPor;
    private Timestamp fechaCrecion;
    private Integer eliminadoPor;
    private Timestamp fechaEliminacion;
    private String sucursal;
    private Integer region;
    private Integer modificadoPor;
    private Timestamp fechaModificacion;
    private Integer movimientoId;
    private String tipoTran;
    private Date fechaRevision;
}
