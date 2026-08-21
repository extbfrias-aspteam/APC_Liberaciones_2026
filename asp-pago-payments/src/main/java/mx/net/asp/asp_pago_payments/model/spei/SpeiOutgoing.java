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
public class SpeiOutgoing  implements Serializable 
{ 
	@Serial
    private static final long serialVersionUID = 1L;
	/*IDENTIFICADOR DE LA TABLA*/
	private Long id;
	/*NOMBRE DE LA PERSONA FISICA O MORAL QUE ORDENA EL ENVIO DE PAGO*/
	private String nombreOrdenante;
	/*CATEGORIA DE LA CUENTA A LA QUE SE AFECTURA EL CARGO POR LA TRANSFERENCIA ELECTRONICA DE FONDOS, (VALORES: 3 TARJETA DEBITO Y CLABE 40)*/
	private String idTipoCuentaOrdenante;
	/*CUENTA DONDE SERAN CARGDOS LOS FONDOS*/
	private String cuentaOrdenante;
	/*RFC DEL ORDENANTE DEL SPEI*/
	private String rfcOrdenante;
	/*NOMBRE DE LA PERSONA FISICA O MORAL RECEPTORA DEL PAGO*/
	private String nombreBeneficiario;
	/*CATEGORIA DE LA CUENTA A LA QUE SE EFECTURA EL ABONO POR LA TRANSFERENCIA ELECTRONICA DE FONDOS*/
	private String idTipoCuentaBeneficiario;
	/*ESTA CUENTA DEBERA ESTAR LIGADA AL CAMPO TIPO DE CUENTA DEL BENEFICIARIO, DONDE SON ABONADOS LOS FONDOS*/
	private String cuentaBeneficiario;
	/*RFC DEL BENEFICIARIO*/
	private String rfcBeneficiario;
	/*DESCRIPCION DEL MOTIVO POR EL QUE EL ORDENANTE HACE EL PAGO AL BENEFICIARIO*/
	private String conceptoPago;
	/*MONTO DE LA OPERACION*/
	private BigDecimal monto;
	private String montoCf;
	/*IVA QUE CORRESPONDE A LA OPERACION  (ES REQUERIDO SE ENVIA EN CERO)*/
	private BigDecimal iva;
	private String ivaCf;
	/*DATO DE REFERENCIA NUMERICA DEL SPEI*/
	private Integer referenciaNumerica;
	/*DATO DE REFERENCIA NUMERICA ENCRYPTADA DEL SPEI*/
	private String referencia;
	/*CORRESPONDE AL IDENTIFICADOR DEL TIPO DE PAGO DE ACUERDO AL CATALOGO DE TIPOS DE PAGO DEL SPEI  (VALORES: 1=TERCEROS, 5=PARTICIPANTES)*/
	private String idTipoPago;
	/*TIPO DE OPERACION (RESERVADO ASP)*/
	private Integer idTipoOperacion;
	/*STATUS (POR DEFAULT 0 POR ENVIAR)*/
	private String status;
	/*STATUS DE ENVIO (0 O 1)*/
	private String envioAutomatico;
	/*ESTATUS DE LA OPERACION CORRESPONDE AL CATALOGO DE ESTATUS DE ENVIO*/
	private Integer statusOperacion;
	/*IDENTIFICADOR DE CADA ORDEN DE TRANSFERENCIA DEFINIDA POR CADA PARTICIPANTE�*/
	private String claveRastreo;
	private String claveRastreo2;
	/*CLAVE SPEI DE LA INSTITUCION RECEPTORA DEL PAGO*/
	private Integer idInstitucionBen;
	/*FECHA DE OPERACION DE LA TRANSACCION*/
	private Date fechaCaptura;
	/*FECHA DE OPERACION */
	private Date fhOperacion;
	/*ID DE DEVOLUCION DE LA TRANSACCION*/
	private Integer idDevolucion;
	/*ALMACEN EL IDENTIFICADOR RELEACIONADO A LA OPERACION  (EN AHORRO GUARDA EL ID DEL MOVIMIENTO CAJA)*/
	private Long idOperacion;
	/*REFERENCIA DE COBRANZA DEL SPEI*/
	private String referenciaCobranza;
	/*AREA EMITE ID*/
	private Integer idAreaEmite;
	/*TOPOLOGIA T*/
	private String topologia;
	/* STATUS DE VERIFICADO */
	private Integer verificado;
	/*Cadena para clave del pago*/
	private String clavePago;
	private Integer prioridad;
	/*INFORMACION ADICIONAL*/
	private String infAdicional;
	/*IDENTIFICADOR DE LA APLICACION O MODULO QUE REALIZA LA OPERACION  (CREDITO, AHORRO,ETC)*/
	private String app;
	/*ID DEL USUARIO QUE REALIZO LA ULTIMA MODIFICACION*/
	private Long modificadoPor;
	/*FECHA DE LA ULTIMA MODIFICACION*/
	private Date fechaModificacion;
	/*USUARIO QUE REALIZO LA OPERACION*/
	private Long usuarioId;
	/*ID DE LA SUCURSAL */
	private Long sucursalId;
	/*ID DE LA EMPRESA */
	private Long empresaId;
	private String usuario;
	/*CORREO ELECTRONICO  */
	private String correoElectronico;
	/*CORREO DEL BENEFICIARIO*/ 
	private String correoBeneficiario;
	/*INDICA SI YA FUE PROCESADO POR CONECTA*/
	private Integer procesado;
	/*ID PARA RELACION A UNA TABLA OUTGOIN AJENA A ESTA DB*/
	private Long coreId;
	/*OBSERVACIONES DEL SPEI*/
	private String observaciones;
	/*FIRMA DE LOS CAMPOS*/
	private String firma;
	/*LLAVE DE CIFRADO*/
	private String llaveAES;
	/*CUENTA ORDENANTE CIFRADA*/
	private String cuentaOrdenanteCf;
	/*NOMBRE ORDENANTE CIFRADA*/
	private String nombreOrdenanteCf;
	/*CUENTA BENEFICIARIO CIFRADA*/
	private String cuentaBeneficiarioCf;
	/*NOMBRE BENEFICIARIO CIFRADA*/
	private String nombreBeneficiarioCf;
	/*NOMBRE BENEFICIARIO CIFRADA*/
	private String conceptoPagoCf;
	/*TELEFONO MOVIL CODI ORDENANTE*/
	private String movilOrdenante;
	/*TELEFONO MOVIL CODI ORDENANTE CIFRADO*/
	private String movilOrdenanteCfr;
	/*DIGITO VERIFICADOR DEL MOVIL ORDENANTE*/
	private String dvMovilOrdenante;
	/*DIGITO VERIFICADOR DEL MOVIL ORDENANTE CIFRADO*/
	private String dvMovilOrdenanteCfr;
	/*TELEFONO MOVIL CODI BENEFICIARIO*/
	private String movilBeneficiario;
	/*TELEFONO MOVIL CODI BENEFICIARIO CIFRADO*/
	private String movilBeneficiarioCfr;
	/*DIGITO VERIFICADOR DEL MOVIL BENEFICIARIO*/
	private String dvMovilBeneficiario;
	/*DIGITO VERIFICADOR DEL MOVIL BENEFICIARIO CIFRADO*/
	private String dvMovilBeneficiarioCfr;
	/*FOLIO DE MENSAJE DE COBRO CODI*/
	private String numFolioEsquemaCoDi;
	/*FOLIO DE MENSAJE DE COBRO CODI CIFRADO*/
	private String numFolioEsquemaCoDiCfr;
	/*INDICADOR DE QUIEN PAGARA LA COMISION*/
	private String pagoComision;
	/*INDICADOR DE QUIEN PAGARA LA COMISION CIFRADO*/
	private String pagoComisionCfr;
	/*MONTO DE LA COMISION*/
	private String montoComision;
	/*MONTO DE LA COMISION CIFRADA*/
	private String montoComisionCfr;
	/*FECHA LIMITE DE PAGO*/
	private String fhHrLimiteDePago;
	/*NUMERO DE SERIE DEL COMERCIO ELECTRONICO EMISOR DEL COBRO*/
	private String numSerieCertComercio;
	/*NUMERO DE SERIE DEL COMERCIO ELECTRONICO EMISOR DEL COBRO CIFRADO*/
    private String numSerieCertComercioCfr;
    /*NOMBRE BENEFICIRIO 2*/
    private String nombreBeneficiario2;
    /*NOMBRE BENEFICIARIO 2 CIFRADO*/
    private String nombreBeneficiario2Cfr;
    /*TIPO CUENTA BENEFICIARIO 2*/
    private String idTipoCuentaBeneficiario2;
    /*CUENTA BENEFICIARIO 2*/
    private String cuentaBeneficiario2;
    /*CUENTA BENEFICIARIO 2 CIFRADO*/
    private String cuentaBeneficiario2Cfr;
    /*RFC BENEFICIARIO 2*/
    private String rfcBeneficiario2;
    /*RFC BENEFICIARIO 2 CIFRADO*/
    private String rfcBeneficiario2Cfr;
}


