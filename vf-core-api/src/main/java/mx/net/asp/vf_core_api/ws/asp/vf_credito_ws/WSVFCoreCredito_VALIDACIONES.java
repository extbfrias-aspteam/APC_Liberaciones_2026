package mx.net.asp.vf_core_api.ws.asp.vf_credito_ws;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSVFCoreCredito_VALIDACIONES {

    @Value("${ms.vf.core.credito.host}")
    private String msVFCoreCreditoHost;
    @Value("${ms.vf.core.credito.port}")
    private String msVFCoreCreditoPort;
    @Value("${vf.core.credito.user}")
    private String msVFCoreCreditoUser;
    @Value("${vf.core.credito.pass}")
    private String msVFCoreCreditoPass;

    @Value("${ms.validar.credito.service}")
    private String msValidarCreditoService;
    @Value("${ms.validar.disposicion.linea.service}")
    private String msValidarDispLineaService;
    @Value("${ms.validar.solicitud.service}")
    private String msValidarSolicitudService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreCreditoUser + ":" + msVFCoreCreditoPass).getBytes()
        );
    }

    public RespuestaDTO validarCredito(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msValidarCreditoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO validarDisposicionDeLinea(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msValidarDispLineaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO validarSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msValidarSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }
}