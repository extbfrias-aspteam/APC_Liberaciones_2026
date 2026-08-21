package mx.net.asp.asp_pago_jwt.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.request.*;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class BaseService {

    private final Environment env;
    private final ErrorHandler errorHandler;

    public RespuestaDTO baseTest(BaseReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(req.getRequest());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}
