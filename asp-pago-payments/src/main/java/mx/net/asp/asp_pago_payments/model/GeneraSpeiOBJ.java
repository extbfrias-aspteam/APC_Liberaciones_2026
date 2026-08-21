package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraSpeiOBJ {
	private SpeiHeaderWS header;
	private String nombreOrdenante;
	private String nombreOrdenanteCfr;
	private String tipoCuentaOrdenanteId;
	private String cuentaOrdenante;
	private String cuentaOrdenanteCfr;
	private String rfcOrdenante;
	private String rfcOrdenanteCfr;
	private String nombreBeneficiario;
	private String nombreBeneficiarioCfr;
	private String tipoCuentaBeneficiarioId;
	private String cuentaBeneficiario;
	private String cuentaBeneficiarioCfr;
	private String rfcBeneficiario;
	private String rfcBeneficiarioCfr;
	private Integer areaEmite;
	private String tipoPago;
	private String conceptoPago;
	private String conceptoPagoCfr;
	private Long referenciaNumerica;
	private String referenciaNumericaCfr;
	private String referenciaCobranza;
	private String referenciaCobranzaCfr;
	private String informacionAdicional;
	private String claveRastreo;
	private BigDecimal monto;
	private String montoCfr;
	private BigDecimal iva;
	private String ivaCfr;
	private Integer institucionBenId;
	private Long operacionId;
	private Long coreId;
	private String correoElectronico;
	private String correoBeneficiario;
	private Long usuarioId;
	private String firma;
	private String llaveAes;
	private String movilOrdenante;
	private String movilOrdenanteCfr;
	private Integer dvMovilOrdenante;
	private String dvMovilOrdenanteCfr;
	private String movilBeneficiario;
	private String movilBeneficiarioCfr;
	private Integer dvMovilBeneficiario;
	private String dvMovilBeneficiarioCfr;
	private String numFolioEsquemaCoDi;
	private String numFolioEsquemaCoDiCfr;
	private String pagoComision;
	private String pagoComisionCfr;
	private String montoComision;
	private String montoComisionCfr;
	private String fhHrLimiteDePago;
	private String numSerieCertComercio;
	private String numSerieCertComercioCfr;
	private String nombreBeneficiario2;
	private String nombreBeneficiario2Cfr;
	private String tipoCuentaBeneficiario2Id;
	private String cuentaBeneficiario2;
	private String cuentaBeneficiario2Cfr;
	private String rfcBeneficiario2;
	private String rfcBeneficiario2Cfr;
	private String status;
	private String envioAutomatico;
	private Integer tipoOperacion;
	private Integer statusOperacion;
	private Integer idDevolucion;
	private Date fechaCaptura;
	private String claveApp;
	private String clavePago;
	private Integer verificado;
	private String topologia;
	private Integer prioridad;
	private Integer procesado;
	private String claveRastreo2;
}
