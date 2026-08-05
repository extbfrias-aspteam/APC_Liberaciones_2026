package mx.net.asp.vf_core_api.service.vf_core_credito;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import mx.net.asp.vf_core_api.ws.asp.vf_credito_ws.WSVFCoreCredito_VALIDACIONES;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class VFCoreCreditoService_VALIDACIONES {

    private final WSVFCoreCredito_VALIDACIONES wsVFCoreCreditoValidaciones;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    public ServiceResponse validarCredito(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoValidaciones.validarCredito(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse validarDisposicionDeLinea(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoValidaciones.validarDisposicionDeLinea(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse validarSolicitud(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoValidaciones.validarSolicitud(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}