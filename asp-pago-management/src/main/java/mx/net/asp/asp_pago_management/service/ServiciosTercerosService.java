package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.ComisionistasDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ServiciosTercerosOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresServiciosTerceros;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ServiciosTercerosService {

    private final ComisionistasDAO comisionistasDAO;
    private final Environment env;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public RespuestaDTO obtenerListadoServiciosTerceros() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<ServiciosTercerosOBJ> serviciosTercerosList = comisionistasDAO.obtenerListadoServiciosTerceros();
            if (serviciosTercerosList != null && !serviciosTercerosList.isEmpty()) {
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(serviciosTercerosList));
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresServiciosTerceros.SERVICIOS_NOT_FOUND,
                        ErroresServiciosTerceros.descError.get(ErroresServiciosTerceros.SERVICIOS_NOT_FOUND));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerListadoRecargasTelefonicas() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<ServiciosTercerosOBJ> serviciosTercerosList = comisionistasDAO.obtenerListadoRecargasTelefonicas();
            if (serviciosTercerosList != null && !serviciosTercerosList.isEmpty()) {
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(serviciosTercerosList));
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresServiciosTerceros.RECARGAS_NOT_FOUND,
                        ErroresServiciosTerceros.descError.get(ErroresServiciosTerceros.RECARGAS_NOT_FOUND));
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