package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.CoDiDAO;
import mx.net.asp.asp_pago_onb.dao.PlasticosDAO;
import mx.net.asp.asp_pago_onb.dao.ProcreaDAO;
import mx.net.asp.asp_pago_onb.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_onb.model.ValidacionDeCorreoOBJ;
import mx.net.asp.asp_pago_onb.model.ValidacionesNubariumOBJ;
import mx.net.asp.asp_pago_onb.request.ValidacionDeCorreoReq;
import mx.net.asp.asp_pago_onb.response.RegistroCuentaSimplificadaResponse;
import mx.net.asp.asp_pago_onb.utilerias.CifradoUtil;
import mx.net.asp.asp_pago_onb.utilerias.CorreoUtils;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresValidacionCorreo;
import mx.net.asp.asp_pago_onb.ws.asp.MSEnviarEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidacionNubariumAsync {

    private final ProcreaDAO procreaDAO;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Async
    public CompletableFuture<Void> registrarValidacionNubarium(RespuestaDTO respuesta, String tipo, String dato, String codigoValidacion) {
        try {
            // 1. Convertir la respuesta a lista de objetos
            Type registrosList = new TypeToken<List<RegistroCuentaSimplificadaResponse>>() {
            }.getType();
            List<RegistroCuentaSimplificadaResponse> responseList = gson.fromJson(respuesta.getData(), registrosList);

            if (responseList != null && !responseList.isEmpty()) {
                // 2. Obtener el solicitanteId del primer elemento
                String solicitanteId = responseList.get(0).getPersona_id();

                // 3. Crear el objeto ValidacionesNubariumOBJ
                ValidacionesNubariumOBJ validaciones = new ValidacionesNubariumOBJ();
                validaciones.setTipoConsulta(tipo);
                validaciones.setSolicitante(solicitanteId);
                validaciones.setDato(dato);
                validaciones.setPorcentajeCoincidencia(100);
                validaciones.setCodigoValidacion(codigoValidacion);
                validaciones.setResultado("S");

                // 4. Registrar mediante el DAO
                procreaDAO.registrarValidacionNubarium(validaciones);
            } else {
                log.warn("No se encontraron registros en la respuesta para registrar validacion Nubarium.");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return CompletableFuture.completedFuture(null);
    }
}