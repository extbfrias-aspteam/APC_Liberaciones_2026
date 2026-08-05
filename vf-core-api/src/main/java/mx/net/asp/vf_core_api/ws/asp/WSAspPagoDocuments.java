package mx.net.asp.vf_core_api.ws.asp;

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
public class WSAspPagoDocuments {

    @Value("${ms.documents.host}")
    private String msDocumentsHost;
    @Value("${ms.documents.port}")
    private String msDocumentsPort;
    @Value("${asp.pago.documents.user}")
    private String msDocumentsUser;
    @Value("${asp.pago.documents.pass}")
    private String msDocumentsPass;

    @Value("${ms.documents.genera.reporte.credito}")
    private String generaReporteCredService;
    @Value("${ms.documents.genera.carta.autorizacion}")
    private String generaCartaAutorizacionService;
    @Value("${ms.documents.genera.caratula.credito}")
    private String generaCaratulaCredService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msDocumentsUser + ":" + msDocumentsPass).getBytes()
        );
    }

    public RespuestaDTO generarReporteCred(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaReporteCredService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }

    public RespuestaDTO generarCartaAutorizacion(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaCartaAutorizacionService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }

    public RespuestaDTO generarCaratulaCred(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaCaratulaCredService);
        return invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);
    }
}
