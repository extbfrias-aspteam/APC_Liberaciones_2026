package mx.net.asp.vf_core_api.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.request.LoginAuthReq;
import mx.net.asp.vf_core_api.ws.asp.request.ValidaJWTReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSAspPagoJWT {

    @Value("${ms.jwt.host}")
    private String msJwtHost;
    @Value("${ms.jwt.port}")
    private String msJwtPort;
    @Value("${asp.pago.jwt.user}")
    private String msJwtUser;
    @Value("${asp.pago.jwt.pass}")
    private String msJwtPass;

    @Value("${ms.jwt.valida.jwt.service}")
    private String validaJwtService;
    @Value("${ms.jwt.login.auth.service}")
    private String loginAuthService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msJwtUser + ":" + msJwtPass).getBytes()
        );
    }

    public RespuestaDTO loginAuth(LoginAuthReq req) {
        String url = InvokeRestServiceUtil.getURLRest(msJwtHost, msJwtPort, loginAuthService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }

    public RespuestaDTO validaJwtToken(ValidaJWTReq req) {
        String url = InvokeRestServiceUtil.getURLRest(msJwtHost, msJwtPort, validaJwtService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }
}
