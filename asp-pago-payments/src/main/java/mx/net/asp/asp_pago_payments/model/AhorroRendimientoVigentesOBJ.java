package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroRendimientoVigentesOBJ {
    private Integer rendimientoVigenteId;
    private String cuenta;
    private Integer tipoAhorroId;
    private Integer periodicidad;
    private Integer plazo;
    private String tipoTasa;
    private String calculoIva;
    private Double tasaInt;
    private Integer tasaId;
    private Integer base;
    private String formula;
    private String tipoCorte;
    private String deposito;
    private Integer numDias;
    private Integer diasGracia;
    private String ctaContable;
    private Date vigenciaDesde;
    private Date vigenciaHasta;
    private String condicionesRetiros;
    private String condicionesApertura;
    private Integer creadoPor;
    private Timestamp fechaCreacion;
    private Integer modificadoPor;
    private Timestamp fechaModificacion;
    private Integer rendimientoId;
    private Double gat;
    private Integer tipoCapitalizarId;
    private Integer noDisposicion;
    private String estatus;
    private Integer tipo;
    private Double capital;
    private Date fechaInicio;
    private Date fechaFinal;
    private Double interes;
    private Date fechaDeposito;
}