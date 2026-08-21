package mx.net.asp.asp_pago_onb.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.ValidaFotoIneOBJ;
import mx.net.asp.asp_pago_onb.request.ValidaFotoIneReq;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSValidaFotoIne {

    @Value("${ws.servicios.valida.foto.ine.host}")
    private String wsServiciosValidaFotoIneHost;
    @Value("${ws.servicios.valida.foto.ine.reconocimiento.facial}")
    private String reconocimientoFacial;
    @Value("${servicios.valida.foto.ine.user}")
    private String wsServiciosValidaFotoIneUser;
    @Value("${servicios.valida.foto.ine.pass}")
    private String wsServiciosValidaFotoInePass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final ErrorHandler errorHandler;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsServiciosValidaFotoIneUser + ":" + wsServiciosValidaFotoInePass).getBytes()
        );
    }

    public RespuestaDTO validaFotoIne(ValidaFotoIneReq req) {
        // Construir el cuerpo de la solicitud
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ValidaFotoIneOBJ validaFotoIneOBJ = new ValidaFotoIneOBJ();
            validaFotoIneOBJ.setCaptura(req.getCaptura());
            validaFotoIneOBJ.setCredencial(req.getCredencial());
            validaFotoIneOBJ.setTipo(req.getTipo());
            validaFotoIneOBJ.setLimiteInferior(req.getLimiteInferior());

            String url = InvokeRestServiceUtil.getURLRest(wsServiciosValidaFotoIneHost, null, reconocimientoFacial);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", getAuthorizationHeader());
            // Configurar la solicitud con los encabezados
            HttpEntity<ValidaFotoIneOBJ> requestEntity = new HttpEntity<>(validaFotoIneOBJ, headers);
            return invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}

