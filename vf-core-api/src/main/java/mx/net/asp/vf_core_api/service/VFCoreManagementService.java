package mx.net.asp.vf_core_api.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import mx.net.asp.vf_core_api.ws.asp.WSVFCoreManagement;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class VFCoreManagementService {

    private final WSVFCoreManagement wsvfCoreManagement;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    public ServiceResponse validarReferencia(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.validarReferencia(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse siguienteSecuencia(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.siguienteSecuencia(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarSolicitanteCompleto(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.buscarSolicitanteCompleto(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarColoniasPorRegion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.buscarColoniasPorRegion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarAgente(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.buscarAgente(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaCanales(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.consultaCanales(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, gson.toJson(respuesta), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaCanalCuenta(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.consultaCanalCuenta(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, gson.toJson(respuesta), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse live() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsvfCoreManagement.live();
            return RespuestaUtils.evaluaRespuesta(respuesta, gson.toJson(respuesta), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}