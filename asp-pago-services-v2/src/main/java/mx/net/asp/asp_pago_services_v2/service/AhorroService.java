package mx.net.asp.asp_pago_services_v2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dao.AhorroDAO;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AhorroService {

    private final AhorroDAO ahorroDAO;
    private final ErrorHandler errorHandler;
    private final Environment env;

    public RespuestaDTO obtenerNivelCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = ahorroDAO.obtenerNivelCuentaAh(cuentaah);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
    public RespuestaDTO actualziarNivelCuentaAh(String cuentaah, String nivelCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = ahorroDAO.actualizarNivelCuentaAh(cuentaah, nivelCuenta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
    public RespuestaDTO registraBitacoraNcBitacora(String dato, String observaciones, String claveTipoBitacora) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = ahorroDAO.registraBitacoraNcBitacora(dato, observaciones, claveTipoBitacora);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
}
