package mx.net.asp.vf_core_api.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import mx.net.asp.vf_core_api.ws.asp.WSAspPagoJWT;
import mx.net.asp.vf_core_api.request.LoginAuthReq;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AspPagoJwtService {

    private final WSAspPagoJWT wsAspPagoJWT;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    public ServiceResponse loginAuth(LoginAuthReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsAspPagoJWT.loginAuth(req);
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
