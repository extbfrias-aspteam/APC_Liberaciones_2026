package mx.net.asp.asp_pago_onb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.PlasticosDAO;
import mx.net.asp.asp_pago_onb.dao.SeguridadDAO;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.ConfiguracionASPPagoOBJ;
import mx.net.asp.asp_pago_onb.model.RespuestaReconocimientoFacialServiceReqOBJ;
import mx.net.asp.asp_pago_onb.request.ValidaFotoIneReq;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresValidaFotoIne;
import mx.net.asp.asp_pago_onb.ws.asp.WSValidaFotoIne;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;


@Service
@Log4j2
@RequiredArgsConstructor
public class ValidaFotoIneService {

    private final WSValidaFotoIne wsValidaFotoIne;
    private final PlasticosDAO plasticosDAO;
    private final SeguridadDAO seguridadDAO;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${version.app}")
    private String versionApp;
    @Value("${validacion.estandar}")
    private String validacionEstandar;
    @Value("${umbral.aceptacion.facial}")
    private String umbralAceptacionFacial;

    public RespuestaDTO validaFotoIne(ValidaFotoIneReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsValidaFotoIne.validaFotoIne(req);
            if (respuesta.getCodigo() == 0) {
                RespuestaReconocimientoFacialServiceReqOBJ parsedResponse = gson.fromJson(respuesta.getData(), RespuestaReconocimientoFacialServiceReqOBJ.class);

                if ("OK".equals(parsedResponse.getEstatus())) {

                    ConfiguracionASPPagoOBJ confAspPagoOBJ = plasticosDAO.obtenerValorConfAspPagoByConcepto(umbralAceptacionFacial);
        
                    if(parsedResponse.getSimilitud() >= Integer.valueOf(confAspPagoOBJ.getValor())) {
                        log.info("Validación facial exitosa, similitud >= umbral.");
                    } else {
                        log.warn("Validación facial fallida: similitud {} < umbral {}", parsedResponse.getSimilitud(), Integer.valueOf(confAspPagoOBJ.getValor()));

                        RespuestaUtils.asignarError(respuesta,
                                ErroresValidaFotoIne.ERROR_DEBAJO_DE_UMBRAL,
                                ErroresValidaFotoIne.getMensajeError(ErroresValidaFotoIne.ERROR_DEBAJO_DE_UMBRAL, parsedResponse.getSimilitud(), Integer.valueOf(confAspPagoOBJ.getValor())));
                    }
        
                } else {
                    log.error("Servicio de ReconocimientoFacial devolvio error de negocio: {}", parsedResponse.getMensaje());
                    RespuestaUtils.asignarError(respuesta,
                            ErroresValidaFotoIne.ERROR_NEGOCIO_SERVICIO,
                            ErroresValidaFotoIne.getMensajeError(ErroresValidaFotoIne.ERROR_NEGOCIO_SERVICIO, parsedResponse.getMensaje()));
                }
        
                registrarLogValidaFotoIne(req, parsedResponse, parsedResponse.getEstatus());
                log.info("Proceso completado con codigo={}, mensaje={}", respuesta.getCodigo(), respuesta.getMensaje());
            } else {
                log.error("Error en el servicio de validar foto INE, codigo: {}, mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private void registrarLogValidaFotoIne(ValidaFotoIneReq req, RespuestaReconocimientoFacialServiceReqOBJ parsedResponse, String estatus) {
        try {
            seguridadDAO.registraLogValidaFotoIne(
                req.getCurp(),
                gson.toJson(parsedResponse),
                estatus,
                validacionEstandar,
                9
            );
            log.info("Registro en seguridadDAO completado para curp: {}", req.getCurp());
        } catch (Exception e) {
            log.error("Error al registrar log en seguridadDAO para curp: {}", req.getCurp(), e);
        }
    }
}