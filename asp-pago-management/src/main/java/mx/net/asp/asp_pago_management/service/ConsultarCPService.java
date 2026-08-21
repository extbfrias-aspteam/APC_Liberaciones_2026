package mx.net.asp.asp_pago_management.service;

import java.util.List;

import mx.net.asp.asp_pago_management.dao.PublicCeroDAO;
import mx.net.asp.asp_pago_management.model.ConsultaCPOBJ;
import mx.net.asp.asp_pago_management.response.ConsultaDomicilioResponse;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresConsultaCP;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresConsultaDomicilio;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;

@Service
@Log4j2
@AllArgsConstructor
public class ConsultarCPService {

    private final SolicitanteDAO solicitanteDAO;
    private final PublicCeroDAO publicCeroDAO;
    private final Environment env;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    public RespuestaDTO consultaCP(String cp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<ConsultaCPOBJ> coloniasList = publicCeroDAO.consultaColoniasByCp(cp);
            if (coloniasList != null && !coloniasList.isEmpty()) {
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(coloniasList));
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresConsultaCP.CP_NOT_FOUND,
                        ErroresConsultaCP.getMensajeError(ErroresConsultaCP.CP_NOT_FOUND, cp));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO consultaDomicilio(String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = solicitanteDAO.obtenerDomicilioSolicitanteV2(solicitanteId);
            if (respuesta.getCodigo() == 0) {
                ConsultaDomicilioResponse domicilioResponse = new ConsultaDomicilioResponse();
                domicilioResponse.setDomicilio(respuesta.getData());
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(domicilioResponse));
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresConsultaDomicilio.DOMICILIO_NOT_FOUND,
                        ErroresConsultaDomicilio.getMensajeError(ErroresConsultaDomicilio.DOMICILIO_NOT_FOUND, solicitanteId));
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
