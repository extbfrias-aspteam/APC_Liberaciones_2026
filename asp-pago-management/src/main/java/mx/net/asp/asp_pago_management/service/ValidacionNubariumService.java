package mx.net.asp.asp_pago_management.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import mx.net.asp.asp_pago_management.dao.PlasticosDAO;
import mx.net.asp.asp_pago_management.model.ConfiguracionASPPagoOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.WSValidaCurpNubarium;
import mx.net.asp.asp_pago_management.ws.asp.response.ValidaCurpNubResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidacionNubariumService {

    private final SolicitanteDAO solicitanteDAO;
    private final PlasticosDAO plasticosDAO;
    private final WSValidaCurpNubarium wsValidaCurpNubarium;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${concepto.consulta.nubarium}")
    private String conceptoConsultaNub;

    public RespuestaDTO validaCurpNub(String curp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("Se valida si es solicitante certificado:: [{}]", curp);

            respuesta = solicitanteDAO.buscarSolicitanteCertificadoByCurp(curp);
            if (respuesta.getCodigo() == 0) {
                log.info("## Solicitante certificado... se omite validación con Nubarium");
                return respuestaExitoBasica(respuesta);
            }

            log.info("## Solicitante no certificado... se procede a validar Curp [{}] con Nubarium", curp);
            ConfiguracionASPPagoOBJ confAspPagoOBJ = plasticosDAO.obtenerValorConfAspPagoByConcepto(conceptoConsultaNub);

            if (confAspPagoOBJ == null) {
                log.warn("No se logró encontrar el concepto de consulta a Nubarium");
                return respuestaExitoBasica(respuesta);
            }

            if ("DISABLED".equals(confAspPagoOBJ.getValor())) {
                log.info("<--- CONSULTA A NUBARIUM DESHABILITADA --->");
                return respuestaExitoBasica(respuesta);
            }

            log.info("<--- CONSULTA A NUBARIUM HABILITADA --->");
            respuesta = wsValidaCurpNubarium.validaCurpNub(curp);

            if (respuesta.getCodigo() == 0) {
                ValidaCurpNubResponse resCurp = gson.fromJson(respuesta.getData(), ValidaCurpNubResponse.class);
                return RespuestaUtils.respuestaExito(respuesta, gson.toJson(resCurp));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO respuestaExitoBasica(RespuestaDTO respuesta) {
        Map<String, String> mapCurp = Map.of(
                "codigoMensaje", "0",
                "codigoValidacion", "0",
                "mensaje", "OK",
                "estatus", "OK"
        );
        return RespuestaUtils.respuestaExito(respuesta, gson.toJson(mapCurp));
    }

}