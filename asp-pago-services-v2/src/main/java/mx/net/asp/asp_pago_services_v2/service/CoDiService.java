package mx.net.asp.asp_pago_services_v2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dao.CoDiDAO;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CoDiService {

    private final CoDiDAO codiDAO;
    private final ErrorHandler errorHandler;
    private final Environment env;

    public RespuestaDTO obtenerRegistroCoDi(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = codiDAO.obtenerRegistroCoDiByCuentaAh(cuentaah);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
}
