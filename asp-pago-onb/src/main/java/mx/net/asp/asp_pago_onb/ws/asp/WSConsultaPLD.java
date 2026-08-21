package mx.net.asp.asp_pago_onb.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.ws.asp.request.ConsultaPldReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSConsultaPLD {

    @Value("${ws.servicios.pld.host}")
    private String wsServiciosPldHost;
    @Value("${ws.servicios.pld.port}")
    private String wsServiciosPldPort;
    @Value("${ws.servicios.pld.consulta.pld}")
    private String consultaPldService;
    @Value("${servicios.pld.user}")
    private String wsServiciosPldUser;
    @Value("${servicios.pld.pass}")
    private String wsServiciosPldPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsServiciosPldUser + ":" + wsServiciosPldPass).getBytes()
        );
    }

    public RespuestaDTO consultaPld(ConsultaPldReq req) {
        // Construir el cuerpo de la solicitud
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String url = InvokeRestServiceUtil.getURLRest(wsServiciosPldHost, wsServiciosPldPort, consultaPldService);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", getAuthorizationHeader());
            // Configurar la solicitud con los encabezados
            String coded = URLEncoder.encode(gson.toJson(req), StandardCharsets.UTF_8);
            HttpEntity<String> requestEntity = new HttpEntity<>(coded, headers);
            return invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}

