package mx.net.asp.asp_pago_access.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.CoDiDAO;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.PropertiesInicialesOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class PropertiesInicialesService {

    private final CoDiDAO codiDAO;
    private final Environment env;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public RespuestaDTO obtenerListadoPropertiesIniciales() {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<PropertiesInicialesOBJ> propIniList = codiDAO.obtenerListadoPropertiesIniciales();
            if (propIniList != null && !propIniList.isEmpty())
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(propIniList));
            else
                return RespuestaUtils.asignarError(respuesta,
                        -100,
                        "Lo sentimos, no se encontró listado de datos iniciales.");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}