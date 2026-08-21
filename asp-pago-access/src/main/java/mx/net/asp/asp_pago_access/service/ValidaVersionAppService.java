package mx.net.asp.asp_pago_access.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.CoDiDAO;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.request.ValidarVersionAppReq;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresVersionApp;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class ValidaVersionAppService {

    private final CoDiDAO codiDAO;
    private final ErrorHandler errorHandler;

    public RespuestaDTO validaVersionApp(ValidarVersionAppReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = codiDAO.obtenerVersionAppActiva(req.getPlataforma());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresVersionApp.ERROR_VALIDA_VERSION,
                        ErroresVersionApp.descError.get(ErroresVersionApp.ERROR_VALIDA_VERSION));
            }

            log.info("### req version app:: {}", req.getVersionApp());
            log.info("### BD version app:: {}", respuesta.getData());
            if (req.getVersionApp().equals(respuesta.getData())) {
                RespuestaUtils.respuestaExito(respuesta);
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresVersionApp.VERSION_NUEVA,
                        ErroresVersionApp.getMensajeError(ErroresVersionApp.VERSION_NUEVA, respuesta.getData()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}
