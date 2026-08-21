package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.IzelStiDAO;
import mx.net.asp.asp_pago_payments.dao.MovimientosCajaDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.model.spei.*;
import mx.net.asp.asp_pago_payments.request.ProcesamientoSpeiSimpleReq;
import mx.net.asp.asp_pago_payments.utilerias.CalculosIdentificadores;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresSTI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class SpeiOutgoingService {

    private final IzelStiDAO izelStiDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final AhorroDisposicionService ahorroDisposicionService;
    private final AhorroDepositoService ahorroDepositoService;
    private final MovimientosCajaDAO movimientosCajaDAO;
    private final StringFormatUtils stringFormatUtils;
    private final ValidarAutorizacionUsuarioService validarAutorizacionUsuarioService;

    @Value("${id.usuario.autorizado.spei}")
    private String idUsuarioAutorizado;
    @Value("${id.usuario.requiere.autorizacion.spei}")
    private String idUsuarioRequiereAuth;
    @Value("${empresa.header.spei}")
    private String empresaHeaderSpei;

    @Value("${spei.status.pendiente.autorizar}")
    private Integer statusPendAutorizar;
    @Value("${spei.status.autorizado}")
    private Integer statusAutorizado;

    public RespuestaDTO procesaSpeiSimple(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        RespuestaSVC respuestaSVC;
        try {
            respuesta = llenaDatosSpeiOut(req, speiSimple, datosOrdenanteOBJ);
            if (respuesta.getCodigo() == 0) {
                GeneraSpeiOBJ generaSpeiOBJ = gson.fromJson(respuesta.getData(), GeneraSpeiOBJ.class);
                respuestaSVC = procesar(generaSpeiOBJ);
                log.info("respuestaSVC :::: {}", gson.toJson(respuestaSVC));
                if (respuestaSVC.getErrores().getCodigoError() != 0) {
                    long codigoError = respuestaSVC.getErrores().getCodigoError();
                    respuesta.setCodigo((int) codigoError);
                    respuesta.setMensaje(respuestaSVC.getErrores().getDescError());
                } else {
                    SpeiOutgoing dataSpeiOutgoing = (SpeiOutgoing) respuestaSVC.getBody().getValor("Outgoing");
                    respuesta.setCodigo(0);
                    respuesta.setMensaje("OK");
                    respuesta.setData(gson.toJson(dataSpeiOutgoing));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    public RespuestaSVC procesar(GeneraSpeiOBJ req) {
        RespuestaSVC respuestaSVC = new RespuestaSVC();
        RespuestaSVC claseRespuesta = new RespuestaSVC();
        new RespuestaDTO();
        RespuestaDTO respuesta;
        long codigoRespuesta;
        String observaciones = "";
        log.info("++ DATOS ATERRISADOS EN PROCESO PARA REGISTRO OUTGOING ::: {}", gson.toJson(req));
        try {

            respuesta = izelStiDAO.buscarCuentaSpeiCompleto(req.getCuentaOrdenante());
            if (respuesta.getCodigo() != 0) {
                log.error("Spei::procesar No se encontró la cuenta clabe ordenante");
                return agregarError(respuestaSVC, ErroresSTI.ERR_CLABE_ORD_NO_EXISTE);
            }
            Cuentas pjCuentas = gson.fromJson(respuesta.getData(), Cuentas.class);

            respuesta = izelStiDAO.buscarAplicacionCompleto(Long.valueOf(pjCuentas.getAplicacionId()), Long.valueOf(pjCuentas.getEmpresaId()));
            if (respuesta.getCodigo() != 0) {
                log.error("Spei::procesar No se encontró la aplicación relacionada a la cuenta");
                return agregarError(respuestaSVC, ErroresSTI.ERR_CLAVE_APLICACION_NO_EXISTE);
            }
            Aplicaciones dataAplicacion = gson.fromJson(respuesta.getData(), Aplicaciones.class);
            String appClave = dataAplicacion.getClave();

            RespuestaSVC validacionRespuesta = validaDatosOutgoing(req);
            if (validacionRespuesta.getErrores().getCodigoError() != 0) {
                log.error("Spei::procesar Hubo un error al validar los datos de la transaccion");
                return manejarErrores(respuestaSVC, validacionRespuesta);
            }

            RespuestaSVC autorizacionRespuesta = validarAutorizacionUsuarioService.procesar(req.getFechaCaptura(), req.getUsuarioId(),
                    Long.valueOf(dataAplicacion.getId()), req.getMonto(), req.getHeader().getIdEmpresa());
            if (autorizacionRespuesta.getErrores().getCodigoError() == 0) {
                String codigo = (String) autorizacionRespuesta.getBody().getValor("codigo");
                if ("REQUERIDO".equals(codigo)) {
                    req.setEnvioAutomatico("0");
                    req.setStatusOperacion(statusPendAutorizar);
                    observaciones = "TRANSACCION OK, PENDIENTE AUTORIZAR";
                } else if ("AUTORIZADO".equals(codigo)) {
                    req.setEnvioAutomatico("1");
                    req.setStatusOperacion(statusAutorizado);
                    observaciones = "TRANSACCION OK, AUTORIZACION AUTOMATICA";
                }
            } else {
                log.info("Encontro el usuario con errores :: {}", claseRespuesta.getBody().getValor("codigo"));
                return manejarErrores(respuestaSVC, autorizacionRespuesta);
            }

            if (esTransaccionDuplicada(req)) {
                return agregarError(respuestaSVC, ErroresSTI.ERR_TRANSACCION_DUPLICADA);
            }

            if (esNuloOVacio(req.getReferenciaCobranza())) {
                req.setReferenciaCobranza(String.valueOf(req.getReferenciaNumerica()));
            }
            SpeiOutgoing dataSpeiOutgoing = buildSpeiOutgoing(req, appClave, observaciones);
            log.info("Spei::datos outgoing CoDi:::");
            log.info(imprimeDatosProcesados(dataSpeiOutgoing));

            respuesta = izelStiDAO.registraSpeiOutgoingSti(dataSpeiOutgoing);
            if(respuesta.getCodigo() != 0){
                return agregarError(respuestaSVC, ErroresSTI.ERR_REGISTRO_SPEI_OUT);
            }

            Long idSpeiOut = Long.valueOf(respuesta.getData());
            dataSpeiOutgoing.setId(idSpeiOut);

            log.info("id del registro :: {}", idSpeiOut);
            respuestaSVC.getBody().addValor("id", idSpeiOut);
            respuestaSVC.getBody().addValor("Outgoing", dataSpeiOutgoing);

            if (req.getStatusOperacion().equals(statusAutorizado)) {
                respuestaSVC.getBody().addValor("id", idSpeiOut);
                respuestaSVC.getBody().addValor("codigo", req.getStatusOperacion());
                respuestaSVC.getBody().addValor("valor", ErroresSTI.descError.get(ErroresSTI.TRANSACCION_EXITOSA));
                return respuestaSVC;
            }
            if (req.getStatusOperacion().equals(statusPendAutorizar)) {
                respuestaSVC.getBody().addValor("id", idSpeiOut);
                respuestaSVC.getBody().addValor("codigo", req.getStatusOperacion());
                respuestaSVC.getBody().addValor("valor", ErroresSTI.descError.get(ErroresSTI.MSG_SPEI_PROGRAMADO));
                return respuestaSVC;
            }

        } catch (Exception e) {
            log.error("Spei::procesar Error al generar la transaccion spei");
            errorHandler.handleException(e);
            codigoRespuesta = ErroresSTI.ERR_TRANSACCION_SPEI;
            claseRespuesta.getErrores().addCodigo("STI", codigoRespuesta);
            return claseRespuesta;
        }
        return respuestaSVC;
    }

    private boolean esTransaccionDuplicada(GeneraSpeiOBJ req) {
        return izelStiDAO.buscarSpeiDuplicados(req.getCuentaOrdenante(), req.getCuentaBeneficiario(),
                req.getConceptoPago(), req.getMonto(), req.getInstitucionBenId(), req.getFechaCaptura(),
                req.getReferenciaNumerica().intValue(), req.getClaveRastreo());
    }

    public RespuestaDTO llenaDatosSpeiOut(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            GeneraSpeiOBJ request = new GeneraSpeiOBJ();
            SpeiHeaderWS header = new SpeiHeaderWS();
            int coreId = 0;
            header.setIdEmpresa(Long.parseLong(empresaHeaderSpei));
            request.setHeader(header);
            request.setNombreOrdenante(stringFormatUtils.limitarLongitud(speiSimple.getNombreOrdenante(), 40));
            request.setTipoCuentaOrdenanteId(datosOrdenanteOBJ.getIdTipoCuentaOrdenante().toString());
            request.setCuentaOrdenante(datosOrdenanteOBJ.getClabe());
            request.setRfcOrdenante(speiSimple.getRfcOrdenante());
            request.setNombreBeneficiario(stringFormatUtils.limitarLongitud(speiSimple.getNombreBeneficiario(), 40));
            request.setTipoCuentaBeneficiarioId(req.getIdTipoCuentaBeneficiario().toString());
            request.setCuentaBeneficiario(req.getCuentaBeneficiario());
            request.setRfcBeneficiario("AAAA010203BB1");
            request.setAreaEmite(1);
            request.setTipoPago(req.getIdTipoPago().toString());
            request.setConceptoPago(stringFormatUtils.limitarLongitud(req.getConceptoPago(), 40));
            request.setReferenciaNumerica(Long.valueOf(speiSimple.getReferenciaNumerica()));
            request.setReferenciaCobranza(speiSimple.getReferenciaNumerica());
            request.setInformacionAdicional("");
            request.setClaveRastreo(speiSimple.getClaveRastreo());
            request.setClaveRastreo2("");
            request.setMonto(BigDecimal.valueOf(speiSimple.getMonto()).setScale(2, RoundingMode.HALF_UP));
            request.setIva(new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
            request.setInstitucionBenId(speiSimple.getIdInstitucionBeneficiaria());
            request.setOperacionId(0L);
            request.setCoreId((long) coreId);
            request.setCorreoElectronico(speiSimple.getCorreoOrdenante());
            request.setCorreoBeneficiario("");
            request.setCorreoBeneficiario("");
            if (speiSimple.getRequiereAutorizacion()) {
                request.setUsuarioId(Long.valueOf(idUsuarioRequiereAuth));
            } else {
                request.setUsuarioId(Long.valueOf(idUsuarioAutorizado));
            }
            request.setNombreOrdenanteCfr(request.getNombreOrdenante());
            request.setCuentaOrdenanteCfr(request.getCuentaOrdenante());
            request.setRfcOrdenanteCfr(request.getRfcOrdenante());
            request.setNombreBeneficiarioCfr(request.getNombreBeneficiario());
            request.setCuentaBeneficiarioCfr(request.getCuentaBeneficiario());
            request.setRfcBeneficiarioCfr(request.getRfcBeneficiario());
            request.setConceptoPagoCfr(request.getConceptoPago());
            request.setReferenciaNumericaCfr(request.getReferenciaNumerica().toString());
            request.setReferenciaCobranzaCfr(request.getReferenciaCobranza());
            request.setMontoCfr(request.getMonto().toString());
            request.setIvaCfr(request.getIva().toString());
            request.setStatus("1");
            request.setEnvioAutomatico("0");
            request.setTipoOperacion(1);
            request.setStatusOperacion(0);
            request.setIdDevolucion(0);
            request.setFechaCaptura(Date.from(Instant.now()));
            request.setClaveApp("0");
            request.setTopologia("V");
            request.setVerificado(1);
            request.setClavePago("");
            request.setPrioridad(1);
            request.setProcesado(0);

            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(gson.toJson(request));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public SpeiOutgoing buildSpeiOutgoing(GeneraSpeiOBJ req, String appClave, String observaciones) {
        SpeiOutgoing dataSpeiOutgoing = new SpeiOutgoing();

        dataSpeiOutgoing.setNombreOrdenante(req.getNombreOrdenante());
        dataSpeiOutgoing.setIdTipoCuentaBeneficiario(req.getTipoCuentaBeneficiarioId());
        dataSpeiOutgoing.setIdTipoCuentaOrdenante(req.getTipoCuentaOrdenanteId());
        dataSpeiOutgoing.setCuentaOrdenante(req.getCuentaOrdenante());
        dataSpeiOutgoing.setRfcOrdenante(req.getRfcOrdenanteCfr());
        dataSpeiOutgoing.setNombreBeneficiario(req.getNombreBeneficiario());
        dataSpeiOutgoing.setCuentaBeneficiario(req.getCuentaBeneficiario());
        dataSpeiOutgoing.setRfcBeneficiario(req.getRfcBeneficiarioCfr());
        dataSpeiOutgoing.setConceptoPago(req.getConceptoPago());
        dataSpeiOutgoing.setMonto(req.getMonto());
        dataSpeiOutgoing.setMontoCf(req.getMontoCfr());
        dataSpeiOutgoing.setIva(req.getIva());
        dataSpeiOutgoing.setIvaCf(req.getIvaCfr());
        dataSpeiOutgoing.setReferenciaNumerica(req.getReferenciaNumerica().intValue());
        dataSpeiOutgoing.setReferencia(req.getReferenciaNumericaCfr());
        dataSpeiOutgoing.setIdTipoPago(req.getTipoPago());
        dataSpeiOutgoing.setIdInstitucionBen(req.getInstitucionBenId());
        dataSpeiOutgoing.setStatus(req.getStatus());
        dataSpeiOutgoing.setEnvioAutomatico(req.getEnvioAutomatico());
        dataSpeiOutgoing.setIdTipoOperacion(req.getTipoOperacion());
        dataSpeiOutgoing.setStatusOperacion(req.getStatusOperacion());
        dataSpeiOutgoing.setClaveRastreo(req.getClaveRastreo());
        dataSpeiOutgoing.setIdDevolucion(req.getIdDevolucion());
        dataSpeiOutgoing.setFechaCaptura(req.getFechaCaptura());
        dataSpeiOutgoing.setFhOperacion(Calendar.getInstance().getTime());
        dataSpeiOutgoing.setIdOperacion(req.getOperacionId());
        dataSpeiOutgoing.setUsuarioId(req.getUsuarioId());
        dataSpeiOutgoing.setApp(appClave);
        dataSpeiOutgoing.setEmpresaId(req.getHeader().getIdEmpresa());
        dataSpeiOutgoing.setFechaModificacion(Date.from(Instant.now()));
        dataSpeiOutgoing.setCorreoElectronico(req.getCorreoElectronico());
        dataSpeiOutgoing.setCorreoBeneficiario(req.getCorreoBeneficiario());
        dataSpeiOutgoing.setProcesado(req.getProcesado());
        dataSpeiOutgoing.setClavePago(req.getClavePago());
        dataSpeiOutgoing.setReferenciaCobranza(req.getReferenciaCobranzaCfr());
        dataSpeiOutgoing.setIdAreaEmite(req.getAreaEmite());
        dataSpeiOutgoing.setTopologia(req.getTopologia());
        dataSpeiOutgoing.setCoreId(req.getCoreId());
        dataSpeiOutgoing.setVerificado(req.getVerificado());
        dataSpeiOutgoing.setPrioridad(req.getPrioridad());
        dataSpeiOutgoing.setSucursalId(req.getHeader().getIdSucursal());
        dataSpeiOutgoing.setInfAdicional(req.getInformacionAdicional());
        dataSpeiOutgoing.setObservaciones(stringFormatUtils.limitarLongitud(observaciones.toUpperCase(), 254));
        dataSpeiOutgoing.setFirma(req.getFirma());
        dataSpeiOutgoing.setLlaveAES(req.getLlaveAes());
        dataSpeiOutgoing.setCuentaOrdenanteCf(req.getCuentaOrdenanteCfr());
        dataSpeiOutgoing.setNombreOrdenanteCf(req.getNombreOrdenanteCfr());
        dataSpeiOutgoing.setCuentaBeneficiarioCf(req.getCuentaBeneficiarioCfr());
        dataSpeiOutgoing.setNombreBeneficiarioCf(req.getNombreBeneficiarioCfr());
        dataSpeiOutgoing.setConceptoPagoCf(req.getConceptoPagoCfr());

        // Validaciones condicionales
        Optional.ofNullable(req.getClaveRastreo2()).ifPresent(dataSpeiOutgoing::setClaveRastreo2);
        Optional.ofNullable(req.getMovilOrdenante()).ifPresent(dataSpeiOutgoing::setMovilOrdenante);
        Optional.ofNullable(req.getMovilOrdenanteCfr()).ifPresent(dataSpeiOutgoing::setMovilOrdenanteCfr);
        Optional.ofNullable(req.getDvMovilOrdenante()).ifPresent(dv -> dataSpeiOutgoing.setDvMovilOrdenante(dv.toString()));
        Optional.ofNullable(req.getDvMovilOrdenanteCfr()).ifPresent(dataSpeiOutgoing::setDvMovilOrdenanteCfr);
        Optional.ofNullable(req.getMovilBeneficiario()).ifPresent(dataSpeiOutgoing::setMovilBeneficiario);
        Optional.ofNullable(req.getMovilBeneficiarioCfr()).ifPresent(dataSpeiOutgoing::setMovilBeneficiarioCfr);
        Optional.ofNullable(req.getDvMovilBeneficiario()).ifPresent(dv -> dataSpeiOutgoing.setDvMovilBeneficiario(dv.toString()));
        Optional.ofNullable(req.getDvMovilBeneficiarioCfr()).ifPresent(dataSpeiOutgoing::setDvMovilBeneficiarioCfr);
        Optional.ofNullable(req.getNumFolioEsquemaCoDi()).ifPresent(dataSpeiOutgoing::setNumFolioEsquemaCoDi);
        Optional.ofNullable(req.getNumFolioEsquemaCoDiCfr()).ifPresent(dataSpeiOutgoing::setNumFolioEsquemaCoDiCfr);
        Optional.ofNullable(req.getPagoComision()).ifPresent(dataSpeiOutgoing::setPagoComision);
        Optional.ofNullable(req.getPagoComisionCfr()).ifPresent(dataSpeiOutgoing::setPagoComisionCfr);
        Optional.ofNullable(req.getMontoComision()).ifPresent(dataSpeiOutgoing::setMontoComision);
        Optional.ofNullable(req.getMontoComisionCfr()).ifPresent(dataSpeiOutgoing::setMontoComisionCfr);
        Optional.ofNullable(req.getFhHrLimiteDePago()).ifPresent(dataSpeiOutgoing::setFhHrLimiteDePago);
        Optional.ofNullable(req.getNumSerieCertComercio()).ifPresent(dataSpeiOutgoing::setNumSerieCertComercio);
        Optional.ofNullable(req.getNumSerieCertComercioCfr()).ifPresent(dataSpeiOutgoing::setNumSerieCertComercioCfr);
        Optional.ofNullable(req.getNombreBeneficiario2()).ifPresent(dataSpeiOutgoing::setNombreBeneficiario2);
        Optional.ofNullable(req.getNombreBeneficiario2Cfr()).ifPresent(dataSpeiOutgoing::setNombreBeneficiario2Cfr);
        Optional.ofNullable(req.getTipoCuentaBeneficiario2Id()).ifPresent(dataSpeiOutgoing::setIdTipoCuentaBeneficiario2);
        Optional.ofNullable(req.getCuentaBeneficiario2()).ifPresent(dataSpeiOutgoing::setCuentaBeneficiario2);
        Optional.ofNullable(req.getCuentaBeneficiario2Cfr()).ifPresent(dataSpeiOutgoing::setCuentaBeneficiario2Cfr);
        Optional.ofNullable(req.getRfcBeneficiario2()).ifPresent(dataSpeiOutgoing::setRfcBeneficiario2);
        Optional.ofNullable(req.getRfcBeneficiario2Cfr()).ifPresent(dataSpeiOutgoing::setRfcBeneficiario2Cfr);

        return dataSpeiOutgoing;
    }

    public RespuestaSVC validaDatosOutgoing(GeneraSpeiOBJ req) {

        RespuestaSVC claseRespuesta = new RespuestaSVC();
        CalculosIdentificadores calculosIdentificadores = new CalculosIdentificadores();

        try {
            // Mapa de campos requeridos
            Map<String, String> camposRequeridos = new LinkedHashMap<>();
            camposRequeridos.put(req.getNombreOrdenante(), "Nombre ordenante");
            camposRequeridos.put(req.getTipoCuentaOrdenanteId(), "Id tipo cuenta ordenante");
            camposRequeridos.put(req.getCuentaOrdenante(), "Cuenta ordenante");
            camposRequeridos.put(req.getRfcOrdenante(), "RFC ordenante");
            camposRequeridos.put(req.getNombreBeneficiario(), "Nombre beneficiario");
            camposRequeridos.put(req.getTipoCuentaBeneficiarioId(), "Id tipo cuenta beneficiario");
            camposRequeridos.put(req.getCuentaBeneficiario(), "Cuenta beneficiario");
            camposRequeridos.put(req.getRfcBeneficiario(), "RFC beneficiario");
            camposRequeridos.put(req.getConceptoPago(), "Concepto de pago");
            camposRequeridos.put(req.getMontoCfr(), "Monto");
            camposRequeridos.put(req.getTipoPago(), "Id tipo pago");
            camposRequeridos.put(req.getIvaCfr(), "Iva");
            camposRequeridos.put(req.getReferenciaNumericaCfr(), "Referencia numérica");
            camposRequeridos.put(req.getStatus(), "Estatus");
            camposRequeridos.put(req.getEnvioAutomatico(), "Envío automático");
            camposRequeridos.put(req.getClaveApp(), "Clave de aplicación");

            // Validar campos requeridos
            for (Map.Entry<String, String> entry : camposRequeridos.entrySet()) {
                if (esNuloOVacio(entry.getKey())) {
                    claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, entry.getValue());
                    return claseRespuesta;
                }
            }

            // Validaciones específicas para números y fechas
            if (!esNumeroValido(req.getInstitucionBenId())) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Id institución beneficiario");
                return claseRespuesta;
            }

            if (!esNumeroValido(req.getStatusOperacion())) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Estatus de operación");
                return claseRespuesta;
            }

            if (!esNumeroValido(req.getIdDevolucion())) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Id de devolución");
                return claseRespuesta;
            }

            if (req.getFechaCaptura() == null) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Fecha captura");
                return claseRespuesta;
            }

            if (!esNumeroValido(req.getUsuarioId())) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Id usuario");
                return claseRespuesta;
            }

            if (!esNumeroValido(req.getHeader().getIdEmpresa())) {
                claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.CAMPOS_REQUERIDOS, "Id empresa");
                return claseRespuesta;
            }

            // Validación de cuenta ordenante
            if (!validarCuentaOrdenante(req.getCuentaOrdenante(), req.getTipoCuentaOrdenanteId(), calculosIdentificadores, claseRespuesta)) {
                return claseRespuesta;
            }

        } catch (Exception e) {
            log.error("Error en validaSpei", e);
            claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.ERR_VALIDACION_CRITICO, e);
            return claseRespuesta;
        }

        return claseRespuesta;
    }

    // Metodo para validar si una cadena es nula o vacía
    private boolean esNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    // Metodo para validar números enteros y longitudes
    private boolean esNumeroValido(Number valor) {
        return valor != null && valor.longValue() >= 0L;
    }

    // Metodo para validar la cuenta ordenante según su tipo
    private boolean validarCuentaOrdenante(String cuentaOrdenante, String idTipoCuentaOrdenante,
                                           CalculosIdentificadores calculosIdentificadores,
                                           RespuestaSVC claseRespuesta) {
        if (esNuloOVacio(cuentaOrdenante)) {
            claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.ERR_CLABE_ORDENATE);
            return false;
        }

        if (!calculosIdentificadores.DigitoVerificadorValido(cuentaOrdenante)) {
            claseRespuesta.getErrores().addCodigo("STI", ErroresSTI.ERR_CLABE_ORDENATE);
            return false;
        }

        return true;
    }

    private RespuestaSVC agregarError(RespuestaSVC respuestaSVC, Long codigoError) {
        respuestaSVC.getErrores().addCodigo("STI", codigoError);
        return respuestaSVC;
    }

    private RespuestaSVC manejarErrores(RespuestaSVC respuestaSVC, RespuestaSVC errores) {
        respuestaSVC.getErrores().setAddErrores(errores.getErrores().getErrores());
        return respuestaSVC;
    }

    private String imprimeDatosProcesados(SpeiOutgoing dataSpeiOutgoing) {

        String dataSpeiOutgoingStr = "nombreOrdenante :" + dataSpeiOutgoing.getNombreOrdenante() +
                "|" +
                "idTipoCuentaBeneficiario :" + dataSpeiOutgoing.getIdTipoCuentaBeneficiario() +
                "|" +
                "idTipoCuentaOrdenante :" + dataSpeiOutgoing.getIdTipoCuentaOrdenante() +
                "|" +
                "cuentaOrdenante :" + dataSpeiOutgoing.getCuentaOrdenante() +
                "|" +
                "rfcOrdenante :" + dataSpeiOutgoing.getRfcOrdenante() +
                "|" +
                "nombreBeneficiario :" + dataSpeiOutgoing.getNombreBeneficiario() +
                "|" +
                "idTipoCuentaBeneficiario :" + dataSpeiOutgoing.getIdTipoCuentaBeneficiario() +
                "|" +
                "cuentaBeneficiario :" + dataSpeiOutgoing.getCuentaBeneficiario() +
                "|" +
                "rfcBeneficiario :" + dataSpeiOutgoing.getRfcBeneficiario() +
                "|" +
                "conceptoPago :" + dataSpeiOutgoing.getConceptoPago() +
                "|" +
                "monto :" + dataSpeiOutgoing.getMonto() +
                "|" +
                "montoCf :" + dataSpeiOutgoing.getMontoCf() +
                "|" +
                "iva :" + dataSpeiOutgoing.getIva() +
                "|" +
                "ivaCf :" + dataSpeiOutgoing.getIvaCf() +
                "|" +
                "ref :" + dataSpeiOutgoing.getReferenciaNumerica() +
                "|" +
                "referenciaNumerica :" + dataSpeiOutgoing.getReferencia() +
                "|" +
                "idTipoPago :" + dataSpeiOutgoing.getIdTipoPago() +
                "|" +
                "idInstitucionBen :" + dataSpeiOutgoing.getIdInstitucionBen() +
                "|" +
                "status :" + dataSpeiOutgoing.getStatus() +
                "|" +
                "envioAutomatico :" + dataSpeiOutgoing.getEnvioAutomatico() +
                "|" +
                "tipoOperacionId :" + dataSpeiOutgoing.getIdTipoOperacion() +
                "|" +
                "statusOperacion :" + dataSpeiOutgoing.getStatusOperacion() +
                "|" +
                "claveRastreo :" + dataSpeiOutgoing.getClaveRastreo() +
                "|" +
                "claveRastreo2 :" + dataSpeiOutgoing.getClaveRastreo2() +
                "|" +
                "idDevolucion:" + dataSpeiOutgoing.getIdDevolucion() +
                "|" +
                "fechaCaptura :" + dataSpeiOutgoing.getFechaCaptura() +
                "|" +
                "fechaOperacion :" + dataSpeiOutgoing.getFhOperacion() +
                "|" +
                "idOperacion :" + dataSpeiOutgoing.getIdOperacion() +
                "|" +
                "usuarioId :" + dataSpeiOutgoing.getUsuarioId() +
                "|" +
                "appClave :" + dataSpeiOutgoing.getApp() +
                "|" +
                "empresaId :" + dataSpeiOutgoing.getEmpresaId() +
                "|" +
                "fechaModificacion" + dataSpeiOutgoing.getFechaModificacion() +
                "|" +
                "correoElectronico :" + dataSpeiOutgoing.getCorreoElectronico() +
                "|" +
                "correoBeneficiario :" + dataSpeiOutgoing.getCorreoBeneficiario() +
                "|" +
                "procesado :" + dataSpeiOutgoing.getProcesado() +
                "|" +
                "clavePago :" + dataSpeiOutgoing.getClavePago() +
                "|" +
                "referenciaCobranza :" + dataSpeiOutgoing.getReferenciaCobranza() +
                "|" +
                "idAreaEmite :" + dataSpeiOutgoing.getIdAreaEmite() +
                "|" +
                "topologia :" + dataSpeiOutgoing.getTopologia() +
                "|" +
                "coreId :" + dataSpeiOutgoing.getCoreId() +
                "|" +
                "verificado :" + dataSpeiOutgoing.getVerificado() +
                "|" +
                "prioridad :" + dataSpeiOutgoing.getPrioridad() +
                "|" +
                "sucursalId :" + dataSpeiOutgoing.getSucursalId() +
                "|" +
                "infAdicional :" + dataSpeiOutgoing.getInfAdicional() +
                "|" +
                "observaciones :" + dataSpeiOutgoing.getObservaciones() +
                "|" +
                "firma :" + dataSpeiOutgoing.getFirma() +
                "|" +
                "llaveAES :" + dataSpeiOutgoing.getLlaveAES() +
                "|" +
                "cuentaOrdenanteCf :" + dataSpeiOutgoing.getCuentaOrdenanteCf() +
                "|" +
                "nombreOrdenanteCf :" + dataSpeiOutgoing.getNombreOrdenanteCf() +
                "|" +
                "cuentaBeneficiarioCf :" + dataSpeiOutgoing.getCuentaBeneficiarioCf() +
                "|" +
                "nombreBeneficiarioCf :" + dataSpeiOutgoing.getNombreBeneficiarioCf() +
                "|" +
                "conceptoPagoCf :" + dataSpeiOutgoing.getConceptoPagoCf() +
                "|" +
                "movilOrdenante :" + dataSpeiOutgoing.getMovilOrdenante() +
                "|" +
                "movilOrdenanteCfr :" + dataSpeiOutgoing.getMovilOrdenanteCfr() +
                "|" +
                "dvMovilOrdenante :" + dataSpeiOutgoing.getDvMovilOrdenante() +
                "|" +
                "dvMovilOrdenanteCfr :" + dataSpeiOutgoing.getDvMovilOrdenanteCfr() +
                "|" +
                "movilBeneficiario :" + dataSpeiOutgoing.getMovilBeneficiario() +
                "|" +
                "dvMovilBeneficiario :" + dataSpeiOutgoing.getDvMovilBeneficiario() +
                "|" +
                "dvMovilBeneficiarioCfr :" + dataSpeiOutgoing.getDvMovilBeneficiarioCfr() +
                "|" +
                "movilBeneficiarioCfr :" + dataSpeiOutgoing.getMovilBeneficiarioCfr() +
                "|" +
                "numFolioEsquemaCoDi :" + dataSpeiOutgoing.getNumFolioEsquemaCoDi() +
                "|" +
                "numFolioEsquemaCoDiCfr :" + dataSpeiOutgoing.getNumFolioEsquemaCoDiCfr() +
                "|" +
                "pagoComision :" + dataSpeiOutgoing.getPagoComision() +
                "|" +
                "pagoComisionCfr :" + dataSpeiOutgoing.getPagoComisionCfr() +
                "|" +
                "montoComision :" + dataSpeiOutgoing.getMontoComision() +
                "|" +
                "montoComisionCfr :" + dataSpeiOutgoing.getMontoComisionCfr() +
                "|" +
                "fhHrLimiteDePago :" + dataSpeiOutgoing.getFhHrLimiteDePago();

        return dataSpeiOutgoingStr;

    }
}