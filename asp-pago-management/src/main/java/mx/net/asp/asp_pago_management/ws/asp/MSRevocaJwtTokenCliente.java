package mx.net.asp.asp_pago_management.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_management.ws.asp.request.RevocaJwtTokenClienteReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class MSRevocaJwtTokenCliente {
    @Value("${ms.jwt.host}")
    private String msJwtHost;
    @Value("${ms.jwt.port}")
    private String msJwtPort;
    @Value("${ms.jwt.revoca.jwt.cliente.service}")
    private String revocaJwtTokenClienteService;
    @Value("${asp.pago.jwt.user}")
    private String msJwtUser;
    @Value("${asp.pago.jwt.pass}")
    private String msJwtPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msJwtUser + ":" + msJwtPass).getBytes()
        );
    }
    
    @Async
    public CompletableFuture<RespuestaDTO> revocaJwtTokenCliente(String telefono) {
        RevocaJwtTokenClienteReq smsRequest = new RevocaJwtTokenClienteReq();
        smsRequest.setTelefono(telefono);
        String url = InvokeRestServiceUtil.getURLRest(msJwtHost, msJwtPort, revocaJwtTokenClienteService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<RevocaJwtTokenClienteReq> requestEntity = new HttpEntity<>(smsRequest, headers);
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null);
    }
}
