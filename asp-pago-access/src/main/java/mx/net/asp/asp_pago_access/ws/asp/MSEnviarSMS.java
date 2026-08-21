package mx.net.asp.asp_pago_access.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_access.ws.asp.request.EnviaSMSReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class MSEnviarSMS {
    @Value("${ms.notification.host}")
    private String msNotificationHost;
    @Value("${ms.notification.port}")
    private String msNotificationPort;
    @Value("${asp.pago.notification.user}")
    private String msNotificationUser;
    @Value("${asp.pago.notification.pass}")
    private String msNotificationPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msNotificationUser + ":" + msNotificationPass).getBytes()
        );
    }
    
    @Async
    public CompletableFuture<RespuestaDTO> enviarSMS(String celular, String mensaje, String personaId, String operacion, BigInteger idToken, String urlDestino) {
        EnviaSMSReq smsRequest = new EnviaSMSReq();
        smsRequest.setCelular(celular);
        smsRequest.setMensaje(mensaje);
        smsRequest.setPersonaId(personaId);
        smsRequest.setOperacion(operacion);
        smsRequest.setCodigoId(idToken.intValue());
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, urlDestino);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<EnviaSMSReq> requestEntity = new HttpEntity<>(smsRequest, headers);
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null);
    }
}
