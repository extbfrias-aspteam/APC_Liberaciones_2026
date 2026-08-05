package mx.net.asp.vf_core_api.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.request.LoginAuthReq;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.ws.asp.request.ValidaJWTReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSAspPagoNotification {

    @Value("${ms.notification.host}")
    private String msNotificationHost;
    @Value("${ms.notification.port}")
    private String msNotificationPort;
    @Value("${asp.pago.notification.user}")
    private String msNotificationUser;
    @Value("${asp.pago.notification.pass}")
    private String msNotificationPass;

    @Value("${ms.notification.enviar.sms.service}")
    private String enviaSMSService;
    @Value("${ms.notification.enviar.sms..masivo.service}")
    private String enviaSMSMasivoService;
    @Value("${ms.notification.enviar.sms.service}")
    private String enviaEmailService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msNotificationUser + ":" + msNotificationPass).getBytes()
        );
    }

    public RespuestaDTO enviarSMS(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, enviaSMSService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }

    public RespuestaDTO enviarSMSMasivo(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, enviaSMSMasivoService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }

    public RespuestaDTO enviarEmail(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, enviaEmailService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }
}
