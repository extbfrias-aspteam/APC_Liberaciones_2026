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
public class WSVFCoreCredito_GENERACION {

    @Value("${ms.vf.core.credito.host}")
    private String msVFCoreCreditoHost;
    @Value("${ms.vf.core.credito.port}")
    private String msVFCoreCreditoPort;
    @Value("${vf.core.credito.user}")
    private String msVFCoreCreditoUser;
    @Value("${vf.core.credito.pass}")
    private String msVFCoreCreditoPass;

    @Value("${ms.generar.envia.anexo.service}")
    private String msGenerarEnviaAnexoService;
    @Value("${ms.generar.referencia.service}")
    private String msGenerarReferenciaService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreCreditoUser + ":" + msVFCoreCreditoPass).getBytes()
        );
    }

    public RespuestaDTO generaEnviaAnexoAWS(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msGenerarEnviaAnexoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO generarReferencia(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msGenerarReferenciaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }
}