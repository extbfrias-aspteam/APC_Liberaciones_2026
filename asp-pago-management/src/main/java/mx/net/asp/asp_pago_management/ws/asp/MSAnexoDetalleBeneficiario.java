package mx.net.asp.asp_pago_management.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.GeneraReporteEstadoMovimientoReq;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
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
public class MSAnexoDetalleBeneficiario {

    @Value("${ms.documents.host}")
    private String msDocumentsHost;
    @Value("${ms.documents.port}")
    private String msDocumentsPort;
    @Value("${ms.documents.genera.anexo.detalle.beneficiario}")
    private String generaDocCtaSimpService;
    @Value("${asp.pago.documents.user}")
    private String msDocumentsUser;
    @Value("${asp.pago.documents.pass}")
    private String msDocumentsPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msDocumentsUser + ":" + msDocumentsPass).getBytes()
        );
    }

    public RespuestaDTO generarAnexoDetalleBeneficiario(String cuentaah, boolean subirDoc) {
        // Construir el cuerpo de la solicitud
        GeneraReporteEstadoMovimientoReq generaDocRequest = new GeneraReporteEstadoMovimientoReq();
        generaDocRequest.setCuentaah(cuentaah);
        generaDocRequest.setSubirDoc(subirDoc);
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaDocCtaSimpService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<GeneraReporteEstadoMovimientoReq> requestEntity = new HttpEntity<>(generaDocRequest, headers);
        return invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
    }
}

