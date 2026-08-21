package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_payments.ws.asp.request.JwtAuthRequest;
import mx.net.asp.asp_pago_payments.ws.asp.response.JwtAuthEiYuResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class JwtTokenManager {

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    @Value("${ws.auth.host}")
    private String authHost;
    @Value("${ws.auth.service}")
    private String authService;

    @Value("${ws.auth.user}")
    private String wsAuthUser;
    @Value("${ws.auth.pass}")
    private String wsAuthPass;

    private String token;

    public synchronized String getToken() {
        if (token == null) {
            generateToken();
        }
        return token;
    }

    private void generateToken() {

        log.info("Generando nuevo JWT...");

        String url = InvokeRestServiceUtil.getURLRest(authHost, "", authService);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JwtAuthRequest> request =
                new HttpEntity<>(buildAuthRequest(), headers);

        RespuestaDTO respuesta =
                invokeRestServiceUtil.enviarSolicitud(
                        url, request, HttpMethod.POST, null);

        if (respuesta.getCodigo() != 0) {
            throw new IllegalStateException("Error al generar JWT");
        }

        JwtAuthEiYuResponse authResponse =
                gson.fromJson(respuesta.getData(), JwtAuthEiYuResponse.class);

        if (!authResponse.isSuccess() || !authResponse.getData().isSuccess()) {
            throw new IllegalStateException("JWT inválido");
        }

        this.token = authResponse.getData().getToken();

        log.info("JWT generado correctamente");
    }

    /**
     * Se usa cuando el WS devuelve 401 / 403
     */
    public synchronized void forceRefresh() {
        log.warn("Forzando regeneracion de JWT");
        this.token = null;
    }

    private JwtAuthRequest buildAuthRequest() {
        return new JwtAuthRequest(wsAuthUser, wsAuthPass);
    }
}
