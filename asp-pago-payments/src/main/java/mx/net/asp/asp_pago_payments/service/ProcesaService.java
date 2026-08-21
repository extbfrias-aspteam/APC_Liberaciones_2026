package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.AhorroDAO;
import mx.net.asp.asp_pago_payments.dao.CoDiDAO;
import mx.net.asp.asp_pago_payments.dao.NucleoCentralDAO;
import mx.net.asp.asp_pago_payments.dao.ProcreaDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_payments.model.SpeiSimpleOBJ;
import mx.net.asp.asp_pago_payments.request.ProcesaReq;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresProcesa;
import mx.net.asp.asp_pago_payments.ws.asp.WSFradudeCheck;
import mx.net.asp.asp_pago_payments.ws.asp.WSProcesaVF;
import mx.net.asp.asp_pago_payments.ws.asp.response.PagoServicioResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProcesaService {

    private final CoDiDAO codiDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final Gson gson;
    private final StringFormatUtils stringFormatUtils;
    private final ErrorHandler errorHandler;
    private final EnviaSMSService enviaSMSService;
    private final OperacionLockService operacionLockService;
    private final WSProcesaVF wsProcesaVF;
    private final WSFradudeCheck wsFradudeCheck;

    public RespuestaDTO procesaPagoServiciosInicial(ProcesaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer idOpPendiente = 0;
        try {
            respuesta = OperacionLockService.validaOperacionPendiente(req.getMap().getCuenta(), codiDAO, errorHandler);
            if (respuesta.getCodigo() == 0) {
                idOpPendiente = respuesta.getData() != null ? Integer.valueOf(respuesta.getData()) : null;
                respuesta = procesaPagoServicios(req);
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
                    codiDAO.finalizaOperacionEjecutada(idOpPendiente);
                } catch (Exception e) {
                    errorHandler.handleException(e);
                }
            }
        }
        return respuesta;
    }

    public RespuestaDTO procesaPagoServicios(ProcesaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        PosGlobalCuentaOBJ cuentaOrigen;
        try {
            cuentaOrigen = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(req.getMap().getCuenta());
            if (cuentaOrigen == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresProcesa.CUENTA_NOT_FOUND,
                        ErroresProcesa.descError.get(ErroresProcesa.CUENTA_NOT_FOUND));
            }

            respuesta = validaCuentaOrigen(cuentaOrigen.getCuentaah(), cuentaOrigen.getTipoCuenta());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            respuesta = wsProcesaVF.procesaPagoVF(req);
            if (respuesta.getCodigo() == 0) {
                //deserealizar respuesta de ventanilla facil
                PagoServicioResponse respuestaVF = gson.fromJson(respuesta.getData(), PagoServicioResponse.class);
                if (respuestaVF == null)
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresGenerales.ERROR_INTERNO,
                            ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

                SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
                speiSimple.setCuentaOrigen(req.getMap().getCuenta());
                speiSimple.setTipoCuentaOrigen(cuentaOrigen.getTipoCuenta());
                speiSimple.setIdClienteOrigen(cuentaOrigen.getPersonaId());
                speiSimple.setTienePlasticoOrigen(speiSimple.getTienePlasticoOrigen());
                speiSimple.setTipoCuentaDestino("VF");
                speiSimple.setMonto(Double.parseDouble(req.getMap().getMonto()));

                enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, false, "VF");

                return RespuestaUtils.respuestaExito(respuesta, gson.toJson(respuestaVF));
            } else {
                //Caso para saldo insuficiente
                if (respuesta.getCodigo() == 1 &&
                        respuesta.getMensaje().toLowerCase().contains("saldo")) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresProcesa.SALDO_INSUFICIENTE,
                            ErroresProcesa.descError.get(ErroresProcesa.SALDO_INSUFICIENTE));
                }

                SpeiSimpleOBJ speiSimple = getSpeiSimpleOBJ(req, cuentaOrigen);

                enviaSMSService.procesaSMSSinDestino(speiSimple.getCuentaOrigen(), speiSimple, true, "VF");

                return RespuestaUtils.asignarError(respuesta,
                        ErroresProcesa.DEVOLUCION_CUENTA_ORIGEN,
                        ErroresProcesa.getMensajeError(ErroresProcesa.DEVOLUCION_CUENTA_ORIGEN, respuesta.getMensaje()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validaCuentaOrigen(String cuentaah, String tipoCuenta) {
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
                        ErroresProcesa.ERROR_ESTATUS_CUENTA,
                        ErroresProcesa.getMensajeError(ErroresProcesa.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresProcesa.CUENTA_ORIGEN_BLOQUEADA)));
            }
            if ("C".equals(estatus) || "2".equals(estatus)) {
                log.warn("No se pudo realizar el pago, la cuenta origen esta cancelada.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresProcesa.ERROR_ESTATUS_CUENTA,
                        ErroresProcesa.getMensajeError(ErroresProcesa.ERROR_ESTATUS_CUENTA,
                                String.valueOf(ErroresProcesa.CUENTA_ORIGEN_CANCELADA)));
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    @NotNull
    private static SpeiSimpleOBJ getSpeiSimpleOBJ(ProcesaReq req, PosGlobalCuentaOBJ cuentaOrigen) {
        SpeiSimpleOBJ speiSimple = new SpeiSimpleOBJ();
        speiSimple.setCuentaOrigen(req.getMap().getCuenta());
        speiSimple.setTipoCuentaOrigen(cuentaOrigen.getTipoCuenta());
        speiSimple.setIdClienteOrigen(cuentaOrigen.getPersonaId());
        speiSimple.setTienePlasticoOrigen(speiSimple.getTienePlasticoOrigen());
        speiSimple.setCuentaDestino(req.getMap().getCuenta());
        speiSimple.setTipoCuentaDestino(cuentaOrigen.getTipoCuenta());
        speiSimple.setIdClienteDestino(cuentaOrigen.getPersonaId());
        speiSimple.setTienePlasticoDestino(speiSimple.getTienePlasticoOrigen());
        speiSimple.setMonto(Double.parseDouble(req.getMap().getMonto()));
        return speiSimple;
    }

    private String obtenerEstatusCuenta(String cuentaah, String tipoCuenta) {
        if ("AHORRO".equals(tipoCuenta)) {
            return procreaDAO.obtenerEstatusCuentaAh(cuentaah);
        } else {
            Integer estatus = ahorroDAO.obtenerEstatusCuentaAh(cuentaah);
            return estatus != null ? String.valueOf(estatus) : null;
        }
    }
}