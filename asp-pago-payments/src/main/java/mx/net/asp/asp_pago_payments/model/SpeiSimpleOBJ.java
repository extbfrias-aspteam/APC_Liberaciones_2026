package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeiSimpleOBJ {
	private String cuentaOrigen;
	private String nombreOrdenante;
	private String cuentaDestino;
	private String nombreBeneficiario;
	private Date fecha;
	private Double monto;
	private Integer usuarioId;
	private String conceptoOrigen;
	private String conceptoDestino;
	private String tipoCuentaOrigen;
	private String tipoCuentaDestino;
	private String panOrigen;
	private String panDestino;
	private String tienePlasticoOrigen;
	private String tienePlasticoDestino;
	private Integer idCuentaOrigen;
	private Integer idCuentaDestino;
	private String idClienteOrigen;
	private String idClienteDestino;
	private TransaccionesCuentasDatosOBJ datos;
	private Boolean operacionSpei;
	private String claveRastreo;
	private String cuentaReferencia;
	private Integer idSpei;
	private Double latitud;
	private Double longitud;
	private String referenciaNumerica;
	private String claveMovimiento;
	private Long referenciaNumericaSpei;
	private Boolean requiereAutorizacion;
	private Integer idInstitucionOrdenante;
	private Integer idInstitucionBeneficiaria;
	private String rfcOrdenante;
	private String correoOrdenante;
}
