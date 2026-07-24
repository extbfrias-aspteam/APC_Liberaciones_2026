package mx.net.asp.asp_pago_management.model;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AhorroContrato {
	
	private Integer ahorroContratoId;
	private Integer actividadId;
	private Integer asociacionId;
	private String centroTrabajo;
	private Integer coloniaId;
	private String contrato;
	private String correoEdocuenta;
	private Integer creadoPor;
	private String ctaContable;
	private String cuenta;
	private String cuentaClabe;
	private String cuentaDestinoCap;
	private String cuentaDestinoRen;
	private String cuentaPadre;
	private Integer cveDestino;
	private String domicilio;
	private Integer errores;
	private String estatus;
	private Integer externaBanco;
	private String externaClabe;
	private String externaCuenta;
	private String externaTarjeta;
	private Date fechaApertura;
	private Date fechaCancelacion;
	private Timestamp fechaCreacion;
	private Date fechaDeposito;
	private Timestamp fechaModificacion;
	private Double gat;
	private Integer giroId;
	private Timestamp horaBloqueo;
	private Timestamp horaPrimerError;
	private Integer idComoEntero;
	private Double ingresos;
	private Double metaAportacion;
	private Integer metaDestinoId;
	private Date metaFecha;
	private Double metaMonto;
	private String metaMotivo;
	private String metaPeriodo;
	private Integer modificadoPor;
	private Integer monedaId;
	private Double montoApertura;
	private Double montoMaxAhorro;
	private Integer numBloqueo;
	private String numeroCasa;
	private Integer ocupacionId;
	private Integer oficialId;
	private String pin;
	private String pinAuto;
	private Timestamp pinFechaCambio;
	private Integer pinUsuarioCambio;
	private String provCentroTrabajo;
	private Integer provOcupacionId;
	private String provPuesto;
	private String provRecId;
	private Double provRecIngresos;
	private Double provRecMontoMaxAhorro;
	private Integer provRecRelId;
	private String puesto;
	private String referencia;
	private Integer rendimientoId;
	private Double requiereIdentificador;
	private String respaldoMd5;
	private Double saldo;
	private Integer statusBloqueo;
	private String sucursalApertura;
	private Integer tipoAhorroId;
	private String titularId;
	private String solicitante;
	private String solicitanteNombre;
	private Integer sucursalId;
	private String rfc;
	private String telefonoCoDi;
	private String fechaNacimiento;
	
}
