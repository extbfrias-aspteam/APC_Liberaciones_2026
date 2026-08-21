package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.request.GenerarAbonoReq;
import mx.net.asp.asp_pago_payments.response.GenerarAbonoResponse;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerarAbono;
import mx.net.asp.asp_pago_payments.ws.asp.WSFradudeCheck;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class GenerarAbonoService {

    private final CoDiDAO codiDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final Gson gson;
    private final StringFormatUtils stringFormatUtils;
    private final ErrorHandler errorHandler;
    private final AhorroTransaccionEiYuService ahorroTransaccionEiYuService;
    private final EnviaSMSService enviaSMSService;
    private final OperacionLockService operacionLockService;
    private final WSFradudeCheck wsFradudeCheck;

    public RespuestaDTO generarAbonoInicial(GenerarAbonoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer idOpPendiente = 0;
        try {
            respuesta = OperacionLockService.validaOperacionPendiente(req.getCuentaOrigen(), codiDAO, errorHandler);
            if (respuesta.getCodigo() == 0) {
                idOpPendiente = respuesta.getData() != null ? Integer.valueOf(respuesta.getData()) : null;
                respuesta = generarAbono(req);
            }
            log.info("Respuesta original:: {}", respuesta);
            GenerarAbonoResponse finalResp = getGenerarAbonoResponse(req, respuesta);
            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(gson.toJson(finalResp));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            if (idOpPendiente != null && idOpPendiente > 0) {
                try {
                    // Finaliza Operacion Ejecutada
                    codiDAO.finalizaOperacionEjecutada(idOpPendiente);
                } catch (Exception e) {
                    errorHandler.handleException(e);
                }
            }
        }
        return respuesta;
    }

    public RespuestaDTO generarAbono(GenerarAbonoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String uuidGlobal = UUID.randomUUID().toString();
        PosGlobalCuentaOBJ cuentaDestino;
        PosGlobalCreditoOBJ posicionGlobalCredito = null;
        try {
            if (req.getCuentaDestino().length() == 18) {
                //TRASPASO MEDIANTE CLABE INTERBANCARIA
                cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaClabe(req.getCuentaDestino());
                if (cuentaDestino == null) {
                    log.info("---> Se tomara como como pago de credito");
                    log.info("Se busca el credito mediante la cuenta clabe: {}", req.getCuentaDestino());
                    posicionGlobalCredito = nucleoCentralDAO.obtenerCreditoPosicionGlobalByCuentaClabe(req.getCuentaDestino());
                    if (posicionGlobalCredito == null)
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresGenerarAbono.CUENTA_DESTINO_NOT_FOUND,
                                ErroresGenerarAbono.descError.get(ErroresGenerarAbono.CUENTA_DESTINO_NOT_FOUND));
                }

                if (cuentaDestino != null) {
                    respuesta = validaCuentaDestino(cuentaDestino.getCuentaah(), cuentaDestino.getTipoCuenta());
                    if (respuesta.getCodigo() != 0)
                        return respuesta;

                    if (req.getCveMovimiento().equals("DEV")) {
                        log.info("<---- DEVOLUCION TRASPASO ASP ENTRE CUENTAS (EiYu) ---->");
                        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
                        speiSimple.setCuentaOrigen(cuentaDestino.getCuentaah());
                        speiSimple.setTipoCuentaOrigen(cuentaDestino.getTipoCuenta());
                        speiSimple.setIdClienteOrigen(cuentaDestino.getPersonaId());
                        speiSimple.setConceptoOrigen(extraerConcepto(req.getConcepto()));
                        speiSimple.setReferenciaNumerica(req.getRefNum());
                        speiSimple.setClaveRastreo(req.getCveRastreo());
                        speiSimple.setFecha(Date.from(Instant.now()));
                        speiSimple.setMonto(req.getMonto().doubleValue());
                        speiSimple.setUsuarioId(0);
                        speiSimple.setLatitud((double) 0);
                        speiSimple.setLongitud((double) 0);
                        TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                        speiSimple.setDatos(datos);

                        respuesta = ahorroTransaccionEiYuService.procesaDevolucionEiYu(speiSimple, uuidGlobal, req.getCuentaDestino());

                        speiSimple.setTipoCuentaOrigen("EIYU");
                        speiSimple.setCuentaDestino(cuentaDestino.getCuentaah());
                        speiSimple.setTipoCuentaDestino(cuentaDestino.getTipoCuenta());
                        speiSimple.setIdClienteDestino(cuentaDestino.getPersonaId());
                        speiSimple.setTienePlasticoDestino(cuentaDestino.isTienePlastico() ? "SI" : "NO");
                        if (respuesta.getCodigo() == 0) {
                            enviaSMSService.procesaSMSSinOrigen(speiSimple, true, "TRASPASO");
                        }
                    } else {
                        log.info("<---- TRASPASO ASP ENTRE CUENTAS (EiYu) ---->");
                        respuesta = llenaDatosTransferencia(req, cuentaDestino);
                        if (respuesta.getCodigo() == 0) {
                            SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);
                            TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                            speiSimple.setDatos(datos);
                            respuesta = ahorroTransaccionEiYuService.procesaTraspasoEiYu(speiSimple, uuidGlobal);
                            if (respuesta.getCodigo() == 0) {
                                enviaSMSService.procesaSMSSinOrigen(speiSimple, false, "TRASPASO");
                            }
                        }
                    }
                } else {
                    if (posicionGlobalCredito == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresGenerarAbono.CUENTA_DESTINO_NOT_FOUND,
                                ErroresGenerarAbono.descError.get(ErroresGenerarAbono.CUENTA_DESTINO_NOT_FOUND));
                    }

                    log.info("****** PAGO DE CREDITO (EiYu) ******");
                    respuesta = llenaDatosPagoCredito(req, posicionGlobalCredito.getContratocr());
                    if (respuesta.getCodigo() == 0) {
                        SpeiSimpleOBJ speiSimple = gson.fromJson(respuesta.getData(), SpeiSimpleOBJ.class);
                        TransaccionesCuentasDatosOBJ datos = new TransaccionesCuentasDatosOBJ();
                        speiSimple.setDatos(datos);
                        respuesta = ahorroTransaccionEiYuService.procesaPagoCredito(speiSimple, posicionGlobalCredito);
                    }
                }
            } else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerarAbono.ERROR_CLABE,
                        ErroresGenerarAbono.descError.get(ErroresGenerarAbono.ERROR_CLABE));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO llenaDatosTransferencia(GenerarAbonoReq req, PosGlobalCuentaOBJ cuentaDestino) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
        try {
            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));

            speiSimple.setCuentaOrigen(req.getCuentaOrigen());
            speiSimple.setCuentaDestino(cuentaDestino.getCuentaah());
            speiSimple.setTipoCuentaDestino(cuentaDestino.getTipoCuenta());
            speiSimple.setTipoCuentaOrigen("EIYU");
            speiSimple.setIdClienteDestino(cuentaDestino.getPersonaId());
            speiSimple.setFecha(Date.from(Instant.now()));
            speiSimple.setMonto(req.getMonto().doubleValue());
            speiSimple.setUsuarioId(0);
            speiSimple.setLatitud((double) 0);
            speiSimple.setLongitud((double) 0);
            speiSimple.setNombreOrdenante(req.getNombreOrdenante());
            speiSimple.setTienePlasticoDestino(cuentaDestino.isTienePlastico() ? "SI" : "NO");
            speiSimple.setIdInstitucionOrdenante(90659);
            speiSimple.setIdInstitucionBeneficiaria(90659);
            speiSimple.setClaveRastreo(req.getCveRastreo());
            speiSimple.setReferenciaNumerica(req.getRefNum());

            String conceptoDestino = generaConceptoDestino(req, speiSimple, "ASP INTEGRA OPC", formatoImporte);
            speiSimple.setConceptoDestino(conceptoDestino);

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(speiSimple));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO llenaDatosPagoCredito(GenerarAbonoReq req, String credito) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
        try {
            speiSimple.setReferenciaNumerica(req.getRefNum());
            speiSimple.setCuentaOrigen(req.getCuentaOrigen());
            speiSimple.setTipoCuentaOrigen("");
            String conceptoOrigen = "Pago al contrato de credito " + credito;
            speiSimple.setConceptoOrigen(conceptoOrigen);
            speiSimple.setFecha(Date.from(Instant.now()));
            speiSimple.setMonto(req.getMonto().doubleValue());
            speiSimple.setUsuarioId(0);
            speiSimple.setIdClienteOrigen("");
            speiSimple.setTienePlasticoOrigen("NO");
            speiSimple.setPanOrigen("");
            speiSimple.setLatitud((double) 0);
            speiSimple.setLongitud((double) 0);
            speiSimple.setNombreOrdenante(req.getNombreOrdenante());
            speiSimple.setTipoCuentaDestino("CREDITO");
            speiSimple.setTipoCuentaDestino(credito);

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(speiSimple));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
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
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerarAbono.CUENTA_DESTINO_BLOQUEADA,
                        ErroresGenerarAbono.descError.get(ErroresGenerarAbono.CUENTA_DESTINO_BLOQUEADA));
            }
            if ("C".equals(estatus) || "2".equals(estatus)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerarAbono.CUENTA_DESTINO_CANCELADA,
                        ErroresGenerarAbono.descError.get(ErroresGenerarAbono.CUENTA_DESTINO_CANCELADA));
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

    private static String generaConceptoDestino(GenerarAbonoReq req, SpeiSimpleOBJ speiSimple, String institucionOrdenante, NumberFormat formatoImporte) {
        return String.format(
                "PAGO RECIBIDO DE %s POR ORDEN DE %s POR UN MONTO DE %s CTA. ORDENANTE %s REF. %s CLAVE DE RASTREO: %s CONCEPTO: %s",
                institucionOrdenante, req.getNombreOrdenante(), formatoImporte.format(req.getMonto()),
                speiSimple.getCuentaOrigen(), speiSimple.getReferenciaNumerica(), speiSimple.getClaveRastreo(),
                req.getConcepto()
        );
    }

    private static String extraerConcepto(String conceptoDestino) {
        String clave = "CONCEPTO: ";
        int indice = conceptoDestino.indexOf(clave);

        if (indice != -1) {
            return conceptoDestino.substring(indice + clave.length()).trim();
        }

        return conceptoDestino; // Retorna el valor del concepto del request si no se encuentra "CONCEPTO: "
    }


    @NotNull
    private static GenerarAbonoResponse getGenerarAbonoResponse(GenerarAbonoReq req, RespuestaDTO respuesta) {
        GenerarAbonoResponse finalResp = new GenerarAbonoResponse();
        if (req.getCveMovimiento().equals("DEV")) {
            if (respuesta.getCodigo() == 0) {
                finalResp.setEstado(3);
                finalResp.setDescripcion("Retornado");
                finalResp.setRespuesta("Solicitud procesada con exito");
                finalResp.setError("");
            } else {
                finalResp.setEstado(2);
                finalResp.setDescripcion("Rechazado");
                finalResp.setRespuesta("Solicitud rechazada");
                finalResp.setError(respuesta.getMensaje());
            }
            finalResp.setClave("DEV");
        } else {
            if (respuesta.getCodigo() == 0) {
                finalResp.setEstado(2);
                finalResp.setDescripcion("Exitoso");
                finalResp.setRespuesta("Solicitud procesada con exito");
                finalResp.setError("");
            } else {
                finalResp.setEstado(6);
                finalResp.setDescripcion("Rechazado");
                finalResp.setRespuesta("Solicitud rechazada");
                finalResp.setError(respuesta.getMensaje());
            }
            finalResp.setClave("TRASP");
        }
        return finalResp;
    }
}