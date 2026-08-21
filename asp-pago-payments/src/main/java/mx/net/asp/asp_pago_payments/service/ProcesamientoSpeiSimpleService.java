package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.request.ProcesamientoSpeiSimpleReq;
import mx.net.asp.asp_pago_payments.response.ProcesamientoSpeiSimpleResponse;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.ws.asp.MSConsultaSaldo;
import mx.net.asp.asp_pago_payments.ws.asp.WSFradudeCheck;
import mx.net.asp.asp_pago_payments.ws.asp.request.FraudeCheckReq;
import mx.net.asp.asp_pago_payments.ws.asp.response.ConsultaSaldoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProcesamientoSpeiSimpleService {

    private final SolicitanteDAO solicitanteDAO;
    private final CoDiDAO codiDAO;
    private final SeguridadDAO seguridadDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final Gson gson;
    private final StringFormatUtils stringFormatUtils;
    private final ErrorHandler errorHandler;
    private final IzelStiDAO izelStiDAO;
    private final AhorroTransaccionService ahorroTransaccionService;
    private final EnviaSMSService enviaSMSService;
    private final MSConsultaSaldo msConsultaSaldo;
    private final WSFradudeCheck wsFradudeCheck;

    @Value("${id.tipo.cuenta.clabe}")
    private Integer idTipoCuentaClabe;
    @Value("${id.tipo.cuenta.cuenta}")
    private Integer idTipoCuentaCuenta;
    @Value("${id.tipo.cuenta.tarjeta}")
    private Integer idTipoCuentaTarjeta;

    @Value("${id.clave.banco.asp}")
    private Integer idClaveBancoAsp;
    @Value("${auth.spei.required}")
    private Boolean authSpei;

    @Value("${version.app}")
    private String versionApp;

    @Value("${minutos.sesion.valida}")
    private Integer minutosSesionValida;
    @Value("${segundos.por.traspaso}")
    private String segundosPorTraspaso;
    @Value("${id.retiro.pago.credito.ahorro}")
    private Integer idMovPagoCredito;

    @Value("${trx.monto.max.sin.favorito}")
    private Double montoMaximoSinFavorito;
    @Value("${trx.minutos.espera.favorito}")
    private Integer minutosEsperaFavorito;

    @Value("${valida.sesion.activa}")
    private Boolean validaSesionActiva;

    public RespuestaDTO procesamientoSpeiSimpleInicial(ProcesamientoSpeiSimpleReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer idOpPendiente = 0;
        try {
            respuesta = OperacionLockService.validaOperacionPendiente(req.getCuentaAhorro(), codiDAO, errorHandler);
            if (respuesta.getCodigo() == 0) {
                idOpPendiente = respuesta.getData() != null ? Integer.valueOf(respuesta.getData()) : null;
                respuesta = procesamientoSpeiSimple(req);
            } else
                return respuesta;
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            if (idOpPendiente != null && idOpPendiente > 0) {
                try {
                    // Finaliza Operacion Ejecutada
                    codiDAO.finalizaOperacionEjecutadaWithObs(idOpPendiente, respuesta.getMensaje());
                } catch (Exception e) {
                    errorHandler.handleException(e);
                }
            }
        }
        return respuesta;
    }

    public RespuestaDTO procesamientoSpeiSimple(ProcesamientoSpeiSimpleReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String uuidGlobal = UUID.randomUUID().toString();
        try {
            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(req.getPersonaIdOrdenante());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            // Obtener datos del ordenante
            SolicitanteOBJ solicitanteOBJOrdenante = solicitanteDAO.obtenerInfoBasicaSolicitanteById(req.getPersonaIdOrdenante());
            if (solicitanteOBJOrdenante == null) {
                log.info("No se pudo procesar el pago, no se encontro la informacion del ordenante.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.SOLICITANTE_ORDENANTE_NOT_FOUND,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.SOLICITANTE_ORDENANTE_NOT_FOUND));
            }

            PosGlobalCuentaOBJ datosOrdenanteOBJ = nucleoCentralDAO.obtenerCuentaPosicionGlobalByPersonaIdCuentaAh(req.getPersonaIdOrdenante(), req.getCuentaAhorro());
            if (datosOrdenanteOBJ == null) {
                log.info("No se pudo procesar el pago, no se encontro informacion de la cuenta del ordenante.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.POS_GLOBAL_ORDENANTE_NOT_FOUND,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.POS_GLOBAL_ORDENANTE_NOT_FOUND));
            }

            datosOrdenanteOBJ.setNombreOrdenante(stringFormatUtils.limpiarCaracteresEspeciales(solicitanteOBJOrdenante.getNombre()));
            datosOrdenanteOBJ.setIdTipoCuentaOrdenante(idTipoCuentaClabe);
            datosOrdenanteOBJ.setIdInstitucionOrdenante(idClaveBancoAsp);

            // Definir nombre beneficiario
            if (!StringUtils.isBlank(req.getPersonaIdBeneficiario())) {
                SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(req.getPersonaIdBeneficiario());
                if (solicitanteOBJ != null) {
                    req.setNombreBeneficiario(solicitanteOBJ.getNombre());
                }
            }

            //limpiamos los datos
            req.setConceptoPago(stringFormatUtils.limpiarCaracteresEspeciales(req.getConceptoPago()));
            req.setNombreBeneficiario(stringFormatUtils.limpiarCaracteresEspeciales(req.getNombreBeneficiario()));
            req.setNombreBeneficiario(stringFormatUtils.limitarLongitud(req.getNombreBeneficiario(), 40));

            log.info("## DATOS DE SPEI SIMPLE LIMPIOS:: {}", gson.toJson(req));

            //valida lista negra cta ord
            if (codiDAO.validaEstaEnListaNegra(req.getCuentaAhorro())) {
                return procesarCuentaEnListaNegra(req.getCuentaAhorro(), "Cuenta ahorro encontrada en lista negra", req, datosOrdenanteOBJ);
            }

            if (codiDAO.validaEstaEnListaNegra(datosOrdenanteOBJ.getClabe())) {
                return procesarCuentaEnListaNegra(datosOrdenanteOBJ.getClabe(), "Cuenta ordenante encontrada en lista negra", req, datosOrdenanteOBJ);
            }

            if (codiDAO.validaEstaEnListaNegra(req.getCuentaBeneficiario())) {
                return procesarCuentaEnListaNegra(req.getCuentaBeneficiario(), "Cuenta beneficiaria encontrada en lista negra", req, datosOrdenanteOBJ);
            }

            //valida saldo
            respuesta = validaSaldo(req.getCuentaAhorro(), req.getMonto());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            Long referenciaSpei = Long.valueOf(izelStiDAO.speiReferenciaSti());
            log.info("## REFERENCIA SPEI GENERADA:: {}", referenciaSpei);
            req.setReferenciaNumericaSpei(referenciaSpei);
            req.setClaveRastreo(stringFormatUtils.generaClaveRastreo(referenciaSpei));
            log.info("## CLAVE DE RASTREO GENERADA:: {}", req.getClaveRastreo());

            //valida fraude
            FraudeCheckReq fraudeCheckReq = wsFradudeCheck.buildFraudeCheckTransaction(
                    datosOrdenanteOBJ.getPersonaId(),
                    datosOrdenanteOBJ.getClabe(), "",
                    String.valueOf(req.getMonto()),
                    String.valueOf(req.getReferenciaNumerica()),
                    String.valueOf(req.getIdInstitucionBeneficiario()),
                    req.getCuentaBeneficiario(),
                    req.getNombreBeneficiario(), "", "", "",
                    req.getClaveRastreo());

            respuesta = wsFradudeCheck.validaTrxFraudulenta(fraudeCheckReq);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            Set<Integer> tiposCuentaValidos = Set.of(idTipoCuentaClabe, idTipoCuentaCuenta, idTipoCuentaTarjeta);
            Set<Integer> tiposCuentaValidosSpei = Set.of(idTipoCuentaClabe, idTipoCuentaTarjeta);

            //valida tipo de pago (Traspaso, Spei, Pago de Crédito)
            if (req.getIdInstitucionBeneficiario().equals(idClaveBancoAsp)) {
                if (tiposCuentaValidos.contains(req.getIdTipoCuentaBeneficiario())) {
                    //TRASPASO MISMO BANCO
                    log.info("****** TRASPASO MISMO BANCO ******");
                    respuesta = enviaTraspaso(req, uuidGlobal, datosOrdenanteOBJ);
                    if (respuesta.getCodigo() == 5200) {
                        //PAGO DE CREDITO
                        log.info("****** PAGO DE CREDITO ******");
                        respuesta = procesaPagoCredito(req, uuidGlobal, datosOrdenanteOBJ);
                    }
                } else {
                    log.info("Favor de proporcionar un tipo de cuenta válido.");
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA,
                            ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA));
                }
            } else if (tiposCuentaValidosSpei.contains(req.getIdTipoCuentaBeneficiario())) {
                //SPEI
                log.info("****** SPEI ******");
                respuesta = enviaSpei(req, uuidGlobal, datosOrdenanteOBJ);
            } else {
                log.info("Favor de proporcionar un tipo de cuenta válido.");
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO enviaTraspaso(ProcesamientoSpeiSimpleReq req, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SolicitanteOBJ solicitanteOBJ;
        PosGlobalCuentaOBJ cuentaDestino = null;
        try {
            if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaClabe)) {
                //TRASPASO MEDIANTE CLABE INTERBANCARIA
                log.info("Se busca la cuenta mediante la cuenta clabe: {}", req.getIdTipoCuentaBeneficiario());
                cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaClabe(req.getCuentaBeneficiario());
            } else if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaTarjeta)) {
                //TRASPASO MEDIANTE NUMERO DE TARJETA
                log.info("Se busca la cuenta mediante la tarjeta virtual: {}", req.getIdTipoCuentaBeneficiario());
                cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaPrincipal(req.getCuentaBeneficiario());
                if (cuentaDestino == null) {
                    log.info("No se encontro tarjeta virtual, se procede a buscar la cuenta clabe mediante la tarjeta fisica: {}", req.getIdTipoCuentaBeneficiario());
                    cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaAdicional(req.getCuentaBeneficiario());
                }
            } else if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaCuenta)) {
                //TRASPASO MEDIANTE NUMERO TELEFONICO ASP PAGO
                log.info("Se busca la cuenta mediante la cuenta: {}", req.getIdTipoCuentaBeneficiario());
                cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(req.getCuentaBeneficiario());
            }
            String uuidIndividual = UUID.randomUUID().toString();
            if (cuentaDestino != null) {
                //validar si es cuenta de CERO
                if (!"AHORRO".equals(cuentaDestino.getTipoCuenta())) {
                    //validamos si la cuenta es tipo cliente CUENTA_BLU
                    if ("CUENTA_BLU".equals(cuentaDestino.getTipoCliente()) && req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaClabe)) {
                        log.info("*** Tipo cliente encontrado: CUENTA_BLU");
                        log.info("---> Se procesa como traspaso hacia EiYu");
                        return procesaEnviaTraspasoEiYu(req, uuidGlobal, datosOrdenanteOBJ);
                    }
                }
                log.info("****** TRANSFERENCIA ******");
                respuesta = procesaEnviaTraspasoASP(req, cuentaDestino, uuidGlobal, datosOrdenanteOBJ);
            } else if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaClabe)) {
                respuesta = procesaEnviaTraspasoEiYu(req, uuidGlobal, datosOrdenanteOBJ);
            } else {
                if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaTarjeta)) {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND,
                            ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND, "(" + idTipoCuentaTarjeta + ")"));
                } else if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaCuenta)) {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND,
                            ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND, "(" + idTipoCuentaCuenta + ")"));
                } else {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND,
                            ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND, "(" + idTipoCuentaClabe + ")"));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesaEnviaTraspasoASP(ProcesamientoSpeiSimpleReq req, PosGlobalCuentaOBJ cuentaDestino, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //valida beneficiario
            respuesta = validaFavoritoCuentaDestino(req.getCuentaAhorro(), req.getCuentaBeneficiario(), req.getMonto());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            // valida si la cuenta beneficiaria esta bloqueada para transacciones
            respuesta = nucleoCentralDAO.obtenerCatCanalByCuentaClabe(cuentaDestino.getClabe(),
                    "DEP_TRANS_CODI", "APP_PAGA_FACIL");
            if (respuesta.getCodigo() == 0) {
                log.info("LA CUENTA BENEFICIARIA ESTA BLOQUEADA PARA ESTA TRANSACCION");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.OPERACION_CUENTA_DESTINO_BLOQUEADA,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.OPERACION_CUENTA_DESTINO_BLOQUEADA));
            }

            respuesta = llenaDatosTransferencia(req, cuentaDestino, 1, false, datosOrdenanteOBJ);
            if (respuesta.getCodigo() == 0) {
                SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);

                respuesta = validaCuentaOrigen(req.getCuentaAhorro(), speiSimple.getCuentaOrigen(), speiSimple.getTipoCuentaOrigen());
                if (respuesta.getCodigo() != 0)
                    return respuesta;

                respuesta = validaCuentaDestino(speiSimple.getCuentaDestino(), speiSimple.getTipoCuentaDestino());
                if (respuesta.getCodigo() != 0)
                    return respuesta;

                if (!cuentaDestino.getTipoCuenta().equals("AHORRO")) {
                    if (cuentaDestino.isTienePlastico()) {
                        speiSimple.setTienePlasticoDestino("SI");
                        speiSimple.setPanDestino(cuentaDestino.getTarjetaVirtual());
                    } else {
                        speiSimple.setTienePlasticoDestino("NO");
                        speiSimple.setPanDestino("");
                    }
                }
                TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                speiSimple.setDatos(datos);
                respuesta = ahorroTransaccionService.procesaTraspasoAsp(speiSimple, uuidGlobal);
                if (respuesta.getCodigo() == 0) {
                    enviaSMSService.procesaSMSTraspaso(speiSimple, false);
                } else if (respuesta.getCodigo() == ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN) {
                    enviaSMSService.procesaSMSTraspaso(speiSimple, true);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesaEnviaTraspasoEiYu(ProcesamientoSpeiSimpleReq req, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Buscar como Pago de credito
            PosGlobalCreditoOBJ posicionGlobalCredito = nucleoCentralDAO.obtenerCreditoPosicionGlobalByCuentaClabe(req.getCuentaBeneficiario());
            if (posicionGlobalCredito != null) {
                //Buscar como pago de credito
                return new RespuestaDTO(5200, "Se buscara como pago de credito", null);
            }

            log.info("---> Se tomara como cuenta de Traspaso EiYu");

            //valida beneficiario
            respuesta = validaFavoritoCuentaDestino(req.getCuentaAhorro(), req.getCuentaBeneficiario(), req.getMonto());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            respuesta = llenaDatosTransferencia(req, null, 1, true, datosOrdenanteOBJ);
            if (respuesta.getCodigo() == 0) {
                SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);

                respuesta = validaCuentaOrigen(req.getCuentaAhorro(), speiSimple.getCuentaOrigen(), speiSimple.getTipoCuentaOrigen());
                if (respuesta.getCodigo() != 0)
                    return respuesta;

                TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                speiSimple.setDatos(datos);
                respuesta = ahorroTransaccionService.procesaTraspasoEiYu(req, speiSimple, uuidGlobal, datosOrdenanteOBJ);
                if (respuesta.getCodigo() == 0) {
                    enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, false, "TRASPASO");
                } else if (respuesta.getCodigo() == ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN) {
                    enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, true, "TRASPASO");
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO enviaSpei(ProcesamientoSpeiSimpleReq req, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //valida beneficiario
            respuesta = validaFavoritoCuentaDestino(req.getCuentaAhorro(), req.getCuentaBeneficiario(), req.getMonto());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            respuesta = llenaDatosTransferencia(req, null, 2, false, datosOrdenanteOBJ);
            if (respuesta.getCodigo() == 0) {
                SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);
                // Validar sesion activa
                if (!"AHORRO".equals(speiSimple.getTipoCuentaOrigen()) && validaSesionActiva) {
                    SesionLoginOBJ sesionLogin = seguridadDAO.obtenerSesionActivaAspPagoByPersonaId(speiSimple.getIdClienteOrigen());

                    Instant fechaLogin = sesionLogin.getFecha_login();
                    Instant fechaActual = Instant.now();

                    log.info("*** fechaActual UTC :: {}", fechaActual);
                    log.info("*** fechaLogin UTC :: {}", fechaLogin);

                    if (fechaLogin.isAfter(fechaActual)) {
                        log.warn("+++ Fecha futura detectada en sesiones +++");
                    }

                    if (!fechaLogin.isAfter(fechaActual.minusSeconds(minutosSesionValida * 60))) {
                        log.info("El solicitante no ha iniciado sesion en los ultimos {} minutos, {}", minutosSesionValida, speiSimple.getIdClienteOrigen());
                        codiDAO.insertarMovimientoSospechoso(req, "El solicitante no ha iniciado sesion en los ultimos " + minutosSesionValida + " minutos.", datosOrdenanteOBJ);

                        ProcesamientoSpeiSimpleResponse speiSimpleResp = new ProcesamientoSpeiSimpleResponse();
                        speiSimpleResp.setEstatusOperacion("El pago fue realizado");
                        speiSimpleResp.setBeneficiario(speiSimple.getNombreBeneficiario());
                        speiSimpleResp.setMonto(speiSimple.getMonto());
                        speiSimpleResp.setReferencia(speiSimple.getReferenciaNumerica().toString());
                        speiSimpleResp.setClaveRastreo("");

                        return RespuestaUtils.respuestaExito(respuesta, "El pago fue realizado", gson.toJson(speiSimpleResp));
                    }
                }

                respuesta = validaCuentaOrigen(req.getCuentaAhorro(), speiSimple.getCuentaOrigen(), speiSimple.getTipoCuentaOrigen());
                if (respuesta.getCodigo() != 0)
                    return respuesta;

                TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                speiSimple.setDatos(datos);
                respuesta = ahorroTransaccionService.procesaSpei(req, speiSimple, uuidGlobal, datosOrdenanteOBJ);
                if (respuesta.getCodigo() == 0) {
                    enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, false, "SPEI");
                } else if (respuesta.getCodigo() == ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN) {
                    enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, true, "SPEI");
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaPagoCredito(ProcesamientoSpeiSimpleReq req, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        PosGlobalCreditoOBJ posicionGlobalCredito;
        try {
            if (req.getIdTipoCuentaBeneficiario().equals(idTipoCuentaClabe)) {
                //TRASPASO MEDIANTE CLABE INTERBANCARIA
                log.info("Se busca el credito mediante la cuenta clabe: {}", req.getIdTipoCuentaBeneficiario());
                posicionGlobalCredito = nucleoCentralDAO.obtenerCreditoPosicionGlobalByCuentaClabe(req.getCuentaBeneficiario());
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_ID_TIPO_CUENTA));
            }

            String uuidIndividual = UUID.randomUUID().toString();
            if (posicionGlobalCredito != null) {

                //VALIDA QUE NO TENGA PAGOS DE CREDITO EN LOS ULTIMOS (ConceptosUtil.SEGUNDOS_POR_TRASP) SEGUNDOS
                if ("AHORRO".equals(datosOrdenanteOBJ.getTipoCuenta())) {
                    //VALIDA EN MOVIMIENTOS CAJA
                    if (procreaDAO.esPagoCreditoProcreaReciente(req.getCuentaAhorro(), req.getMonto(),
                            idMovPagoCredito, segundosPorTraspaso)) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresPSpeiSimple.ERROR_ESPERA_PARA_NUEVO_ENVIO,
                                ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESPERA_PARA_NUEVO_ENVIO, segundosPorTraspaso));
                    }
                } else {
                    //VALIDA EN AHORRO.AHTRANSACCIONES
                    if (ahorroDAO.esPagoCreditoCFReciente(req.getCuentaAhorro(), req.getMonto(), "ASPPC", segundosPorTraspaso)) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresPSpeiSimple.ERROR_ESPERA_PARA_NUEVO_ENVIO,
                                ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESPERA_PARA_NUEVO_ENVIO, segundosPorTraspaso));
                    }
                }

                if (!posicionGlobalCredito.getEstadocr().equals("P")) {
                    respuesta = llenaDatosPagoCredito(req, posicionGlobalCredito.getContratocr(), datosOrdenanteOBJ);
                    if (respuesta.getCodigo() == 0) {
                        SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);

                        respuesta = validaCuentaOrigen(req.getCuentaAhorro(), speiSimple.getCuentaOrigen(), speiSimple.getTipoCuentaOrigen());
                        if (respuesta.getCodigo() != 0)
                            return respuesta;

                        TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                        speiSimple.setDatos(datos);
                        respuesta = ahorroTransaccionService.procesaPagoCredito(speiSimple, posicionGlobalCredito);
                        if (respuesta.getCodigo() == 0) {
                            enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaDestino(), speiSimple, false, "CREDITO");
                        } else if (respuesta.getCodigo() == ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN) {
                            //SMS de devolucion
                            enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaDestino(), speiSimple, true, "CREDITO");
                        }
                    }
                } else {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.CREDITO_LIQUIDADO,
                            ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.CREDITO_LIQUIDADO));
                }
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.CUENTA_DESTINO_NOT_FOUND, "(52)"));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO llenaDatosTransferencia(ProcesamientoSpeiSimpleReq req, PosGlobalCuentaOBJ cuentaDestino, Integer tipoMovimiento, boolean esCuentaEiYu, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
        try {
            Integer referencia = req.getReferenciaNumerica();
            boolean asignaReferencia = Objects.isNull(referencia) || referencia <= 0;
            if (asignaReferencia) {
                int numeroReferencia = izelStiDAO.referenciaQR();
                String numeroReferenciaTMP = String.format("%06d", numeroReferencia);
                numeroReferenciaTMP = "1" + numeroReferenciaTMP;
                log.info("numeroReferenciaTMP :: {}", numeroReferenciaTMP);
                speiSimple.setReferenciaNumerica(numeroReferenciaTMP);
            } else
                speiSimple.setReferenciaNumerica(String.valueOf(req.getReferenciaNumerica()));

            speiSimple.setReferenciaNumericaSpei(req.getReferenciaNumericaSpei());
            speiSimple.setClaveRastreo(req.getClaveRastreo());

            if (esCuentaEiYu || tipoMovimiento.equals(2)) {

                if (StringUtils.isBlank(datosOrdenanteOBJ.getClabe())) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.ERROR_CLABE_EMPTY,
                            ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_CLABE_EMPTY,
                                    String.valueOf(ErroresPSpeiSimple.ERROR_CLABE_EMPTY)));
                }

                speiSimple.setCuentaDestino(req.getCuentaBeneficiario());
                String tipoCuentaDestino = esCuentaEiYu
                        ? "EIYU"
                        : "SPEI";
                speiSimple.setRequiereAutorizacion(authSpei);
                speiSimple.setTipoCuentaDestino(tipoCuentaDestino);
            } else {
                //speiSimple.setClaveRastreo("");
                speiSimple.setCuentaDestino(cuentaDestino.getCuentaah());
                speiSimple.setTipoCuentaDestino(cuentaDestino.getTipoCuenta());
                speiSimple.setIdClienteDestino(cuentaDestino.getPersonaId());
            }

            if (!datosOrdenanteOBJ.getTipoCuenta().equals("AHORRO")) {
                if (datosOrdenanteOBJ.isTienePlastico()) {
                    speiSimple.setTienePlasticoOrigen("SI");
                    speiSimple.setPanOrigen(datosOrdenanteOBJ.getTarjetaVirtual());
                } else {
                    speiSimple.setTienePlasticoOrigen("NO");
                    speiSimple.setPanOrigen("");
                }
            }

            speiSimple.setCuentaOrigen(datosOrdenanteOBJ.getCuentaah());
            speiSimple.setTipoCuentaOrigen(datosOrdenanteOBJ.getTipoCuenta());

            String institucionOrdenante = "";
            String institucionBeneficiario = "";

            List<CatInstiOBJ> institucionesList = izelStiDAO.obtenerListadoInstituciones();
            if (institucionesList != null && !institucionesList.isEmpty()) {
                for (CatInstiOBJ inst : institucionesList) {
                    if (datosOrdenanteOBJ.getIdInstitucionOrdenante().toString().equals(inst.getClave_institucion().toString())) {
                        institucionOrdenante = inst.getNombreCorto();
                    }
                    if (req.getIdInstitucionBeneficiario().toString()
                            .equals(inst.getClave_institucion().toString())) {
                        institucionBeneficiario = inst.getNombreCorto();
                    }
                }
            } else {
                log.error("Ocurrio un Error al consultar el catalogo Instituciones");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS));
            }

            if (StringUtils.isBlank(institucionOrdenante) || StringUtils.isBlank(institucionBeneficiario)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS));
            }

            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
            String conceptoOrigen = generaConceptoOrigen(req, speiSimple, institucionBeneficiario, formatoImporte);
            String conceptoDestino = generaConceptoDestino(req, speiSimple, institucionOrdenante, formatoImporte, datosOrdenanteOBJ);

            speiSimple.setConceptoOrigen(conceptoOrigen);
            speiSimple.setConceptoDestino(conceptoDestino);
            speiSimple.setFecha(Date.from(Instant.now()));
            speiSimple.setMonto(req.getMonto());
            speiSimple.setUsuarioId(0);
            speiSimple.setIdClienteOrigen(datosOrdenanteOBJ.getPersonaId());
            speiSimple.setLatitud(req.getHeader().getLatitud());
            speiSimple.setLongitud(req.getHeader().getLongitud());
            speiSimple.setNombreOrdenante(datosOrdenanteOBJ.getNombreOrdenante());
            speiSimple.setNombreBeneficiario(req.getNombreBeneficiario());
            speiSimple.setIdInstitucionOrdenante(datosOrdenanteOBJ.getIdInstitucionOrdenante());
            speiSimple.setIdInstitucionBeneficiaria(req.getIdInstitucionBeneficiario());

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(datosOrdenanteOBJ.getPersonaId());
            if (solicitanteOBJ != null) {
                speiSimple.setCorreoOrdenante(solicitanteOBJ.getCorreo());
                speiSimple.setRfcOrdenante(solicitanteOBJ.getRfc());
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS));
                return respuesta;
            }

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(speiSimple));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO llenaDatosPagoCredito(ProcesamientoSpeiSimpleReq req, String credito, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
        PosGlobalCuentaOBJ cuentaOrigen;
        try {

            if (!datosOrdenanteOBJ.getTipoCuenta().equals("AHORRO")) {
                if (datosOrdenanteOBJ.isTienePlastico()) {
                    speiSimple.setTienePlasticoOrigen("SI");
                    speiSimple.setPanOrigen(datosOrdenanteOBJ.getTarjetaVirtual());
                } else {
                    speiSimple.setTienePlasticoOrigen("NO");
                    speiSimple.setPanOrigen("");
                }
            }

            Integer referencia = req.getReferenciaNumerica();
            boolean asignaReferencia = Objects.isNull(referencia) || referencia <= 0;
            if (asignaReferencia) {
                int numeroReferencia = izelStiDAO.referenciaQR();
                String numeroReferenciaTMP = String.format("%06d", numeroReferencia);
                numeroReferenciaTMP = "1" + numeroReferenciaTMP;
                log.info("numeroReferenciaTMP :: {}", numeroReferenciaTMP);
                speiSimple.setReferenciaNumerica(numeroReferenciaTMP);
            } else
                speiSimple.setReferenciaNumerica(String.valueOf(req.getReferenciaNumerica()));

            speiSimple.setCuentaOrigen(datosOrdenanteOBJ.getCuentaah());
            speiSimple.setTipoCuentaOrigen(datosOrdenanteOBJ.getTipoCuenta());

            String conceptoOrigen = "Pago al contrato de credito " + credito;

            speiSimple.setConceptoOrigen(conceptoOrigen);
            speiSimple.setFecha(Date.from(Instant.now()));
            speiSimple.setMonto(req.getMonto());
            speiSimple.setUsuarioId(0);
            speiSimple.setIdClienteOrigen(datosOrdenanteOBJ.getPersonaId());
            speiSimple.setLatitud(req.getHeader().getLatitud());
            speiSimple.setLongitud(req.getHeader().getLongitud());
            speiSimple.setNombreOrdenante(datosOrdenanteOBJ.getNombreOrdenante());
            speiSimple.setNombreBeneficiario(req.getNombreBeneficiario());
            speiSimple.setTipoCuentaDestino("CREDITO");
            speiSimple.setCuentaDestino(credito);

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(datosOrdenanteOBJ.getPersonaId());
            if (solicitanteOBJ != null) {
                speiSimple.setCorreoOrdenante(solicitanteOBJ.getCorreo());
                speiSimple.setRfcOrdenante(solicitanteOBJ.getRfc());
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.DATOS_TRANSACCION_INCOMPLETOS));
                return respuesta;
            }

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(speiSimple));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validarRegistroCoDi(RegistroCoDiOBJ regCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.REGISTRO_CODI_NOT_FOUND,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.VERSION_APP_ORIGEN,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.VERSION_APP_ORIGEN));
            }

            if (regCoDiOBJ.getBloqueado() == 1) {
                log.info("*** Registro CoDi Bloqueado");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.REG_CUENTA_ORIGEN_BLOQUEADA,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.REG_CUENTA_ORIGEN_BLOQUEADA));
            } else if (regCoDiOBJ.getBloqueosTemporales() > 0) {
                log.info("*** Registro CoDi Bloqueado Temporalmente");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.REG_CUENTA_ORIGEN_BLOQUEADA_TEMPORAL,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.REG_CUENTA_ORIGEN_BLOQUEADA_TEMPORAL));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO validaSaldo(String cuentaah, Double monto) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = msConsultaSaldo.consultarSaldo(cuentaah);
            if (respuesta.getCodigo() == 0) {
                ConsultaSaldoResponse saldoObj = gson.fromJson(respuesta.getData(), ConsultaSaldoResponse.class);
                if (saldoObj.getSaldo() >= monto) {
                    RespuestaUtils.respuestaExito(respuesta);
                } else {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.SALDO_INSUFICIENTE,
                            ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.SALDO_INSUFICIENTE));
                }
            } else if (respuesta.getCodigo() == ErroresGenerales.ERROR_WS) {
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_CONSULTA_SALDO,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_CONSULTA_SALDO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validaCuentaOrigen(String cuentaah, String cuentaOrd, String tipoCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Obtenemos el estado de la cuenta según el tipo
            String estatus = obtenerEstatusCuenta(cuentaah, tipoCuenta);

            // Si el estatus es null, hay un error interno
            if (estatus == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }

            // Validamos si la cuenta está bloqueada o cancelada
            if ("B".equals(estatus) || "3".equals(estatus)) {
                log.warn("No se pudo realizar el pago, la cuenta origen esta bloqueada.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresPSpeiSimple.CUENTA_ORIGEN_BLOQUEADA)));
            }
            if ("C".equals(estatus) || "2".equals(estatus)) {
                log.warn("No se pudo realizar el pago, la cuenta origen esta cancelada.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresPSpeiSimple.CUENTA_ORIGEN_CANCELADA)));
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private RespuestaDTO validaCuentaDestino(String cuentaah, String tipoCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Obtenemos el estado de la cuenta según el tipo
            String estatus = obtenerEstatusCuenta(cuentaah, tipoCuenta);

            // Si el estatus es null, hay un error interno
            if (estatus == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }

            // Validamos si la cuenta está bloqueada o cancelada
            if ("B".equals(estatus) || "3".equals(estatus)) {
                log.warn("No se pudo realizar el pago, la cuenta destino esta bloqueada.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresPSpeiSimple.CUENTA_ORIGEN_BLOQUEADA)));
            }
            if ("C".equals(estatus) || "2".equals(estatus)) {
                log.warn("No se pudo realizar el pago, la cuenta destino esta cancelada.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresPSpeiSimple.CUENTA_ORIGEN_CANCELADA)));
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private String obtenerEstatusCuenta(String cuentaah, String tipoCuenta) {
        if ("AHORRO".equals(tipoCuenta)) {
            return procreaDAO.obtenerEstatusCuentaAh(cuentaah);
        } else {
            Integer estatus = ahorroDAO.obtenerEstatusCuentaAh(cuentaah);
            return estatus != null ? String.valueOf(estatus) : null;
        }
    }

    private String generaConceptoOrigen(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, String
            institucionBeneficiario, NumberFormat formatoImporte) {
        return String.format(
                "PAGO ENVIADO A %s PARA %s POR UN MONTO DE %s CTA. BENEFICIARIA %s REF. %s CLAVE DE RASTREO: %s CONCEPTO: %s",
                institucionBeneficiario, req.getNombreBeneficiario(), formatoImporte.format(req.getMonto()),
                speiSimple.getCuentaDestino(), speiSimple.getReferenciaNumerica(), speiSimple.getClaveRastreo(),
                req.getConceptoPago()
        );
    }

    private String generaConceptoDestino(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, String
            institucionOrdenante, NumberFormat formatoImporte, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        return String.format(
                "PAGO RECIBIDO DE %s POR ORDEN DE %s POR UN MONTO DE %s CTA. ORDENANTE %s REF. %s CLAVE DE RASTREO: %s CONCEPTO: %s",
                institucionOrdenante, datosOrdenanteOBJ.getNombreOrdenante(), formatoImporte.format(req.getMonto()),
                speiSimple.getCuentaOrigen(), speiSimple.getReferenciaNumerica(), speiSimple.getClaveRastreo(),
                req.getConceptoPago()
        );
    }

    private RespuestaDTO procesarCuentaEnListaNegra(String cuenta, String mensaje, ProcesamientoSpeiSimpleReq req, PosGlobalCuentaOBJ datosOrdenanteOBJ) {

        RespuestaDTO respuesta = new RespuestaDTO();
        log.info("{}: {}", mensaje, cuenta);
        codiDAO.insertarMovimientoSospechoso(req, mensaje, datosOrdenanteOBJ);

        ProcesamientoSpeiSimpleResponse speiSimpleResp = new ProcesamientoSpeiSimpleResponse();
        speiSimpleResp.setEstatusOperacion("El pago fue realizado");
        speiSimpleResp.setBeneficiario(req.getNombreBeneficiario());
        speiSimpleResp.setMonto(req.getMonto());
        speiSimpleResp.setReferencia(req.getReferenciaNumerica().toString());
        speiSimpleResp.setClaveRastreo("");

        return RespuestaUtils.respuestaExito(respuesta, "El pago fue realizado", gson.toJson(speiSimpleResp));
    }

    private RespuestaDTO validaFavoritoCuentaDestino(String cuentaOrdenante, String cuentaBeneficiario, Double monto) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            FavoritoOBJ favorito = codiDAO.obtenerFavoritoPorCuentaBeneficiario(
                    cuentaOrdenante,
                    cuentaBeneficiario
            );

            //NO es favorito → validar monto máximo
            if (favorito == null) {
                log.warn("No se encontro favorito");
                if (monto > montoMaximoSinFavorito) {
                    log.warn("Monto {} supera el máximo permitido {} para cuenta no favorita",
                            monto, montoMaximoSinFavorito);

                    NumberFormat formatoMXN = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

                    return RespuestaUtils.asignarError(respuesta,
                            ErroresPSpeiSimple.ERROR_MONTO_MAX_SIN_FAVORITO,
                            ErroresPSpeiSimple.getMensajeError(
                                    ErroresPSpeiSimple.ERROR_MONTO_MAX_SIN_FAVORITO,
                                    formatoMXN.format(montoMaximoSinFavorito)
                            )
                    );
                }

                return RespuestaUtils.respuestaExito(respuesta);
            }

            Instant fechaRegistro = favorito.getFecha_creacion();
            Instant fechaActual = Instant.now();

            log.info("*** fechaActual UTC :: {}", fechaActual);
            log.info("*** fechaRegistro UTC :: {}", fechaRegistro);

            if (fechaRegistro.isAfter(fechaActual)) {
                log.warn("+++ Fecha futura detectada en registro de favorito +++");
            } else if (!fechaRegistro.isBefore(fechaActual.minusSeconds(minutosEsperaFavorito * 60))) {
                log.warn("Favorito {} aún no cumple el tiempo mínimo de {} minutos",
                        favorito.getId(), minutosEsperaFavorito);

                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_TIEMPO_MINIMO_FAVORITO,
                        ErroresPSpeiSimple.getMensajeError(
                                ErroresPSpeiSimple.ERROR_TIEMPO_MINIMO_FAVORITO,
                                String.valueOf(minutosEsperaFavorito)
                        )
                );
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }
}