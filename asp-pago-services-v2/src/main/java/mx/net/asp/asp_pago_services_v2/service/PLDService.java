package mx.net.asp.asp_pago_services_v2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dao.PldDAO;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PLDService {

    private final PldDAO pldDAO;
    private final ErrorHandler errorHandler;
    private final Environment env;

    public RespuestaDTO obtenerIngresosMensuales() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerIngresosMensuales();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerAhorrosMensuales() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerAhorrosMensuales();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerOcupaciones() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerOcupaciones();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerFrecuencia() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerFrecuencia();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerObjetivos() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerObjetivos();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerActividades() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerActividades();
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerGirosByActividadId(Integer actividadId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerGirosByActividadId(actividadId);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
    public RespuestaDTO obtenerFormularioPLD(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = pldDAO.obtenerFormularioPld(cuentaah);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
    public RespuestaDTO guardarFormularioPLD(SubirNivelCuentaRemotaReq form) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            respuesta = pldDAO.obtenerFormularioPld(form.getCuentaah());
            if (respuesta.getCodigo() != -100) { // El codigo -100 significa que no tiene un formulario guardado
                
                // Error "La cuenta ya tiene registrado un formulario PLD"
                if (respuesta.getCodigo() == 0){
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("Se encontró formulario PLD de la cuenta: " + form.getCuentaah());
                }
                return respuesta;
            }
            
            respuesta = pldDAO.guardarFormularioPLD(form);
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
}