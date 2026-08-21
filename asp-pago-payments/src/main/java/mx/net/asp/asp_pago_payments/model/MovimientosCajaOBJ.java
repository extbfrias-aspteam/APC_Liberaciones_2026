package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientosCajaOBJ {
    private Integer movimientoId;
    private Integer cajaId;
    private Integer cajeroId;
    private Date fecha;
    private Integer tipoMovId;
    private Integer monedaId;
    private Double monto;
    private Double tipoCambio;
    private Integer formaPago;
    private String cuenta;
    private String control;
    private Integer creadoPor;
    private Timestamp fechaCreacion;
    private Integer modificadoPor;
    private Timestamp fechaModificacion;
    private Integer bancoId;
    private String cheque;
    private String estatus;
    private String obs;
    private String poliza;
    private Integer referenciaMovimientoId;
    private Integer bancoOrigen;
    private Date fechaDeposito;
    private String polizaProvision;
    private String transaccionId;
    private String tarjetaOperativaOd;
    private Integer regionId;
    private Long idImportacion;
    private Double monedaValor;
    private String monedaNombre;
    private Date monedaFechaValor;
    private Double montoOriginal;
    private Double montoDestino;
    private String monedaError;
    private Double latitud;
    private Double longitud;
}
