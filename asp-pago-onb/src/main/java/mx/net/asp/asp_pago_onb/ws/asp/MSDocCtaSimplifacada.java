package mx.net.asp.asp_pago_onb.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.ws.asp.request.GeneraDocCtaSimplificadaReq;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.utilerias.InvokeRestServiceUtil;
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
public class MSDocCtaSimplifacada {

    @Value("${ms.documents.host}")
    private String msDocumentsHost;
    @Value("${ms.documents.port}")
    private String msDocumentsPort;
    @Value("${ms.documents.genera.doc.cta.simplificada}")
    private String generaDocCtaSimpService;
    @Value("${asp.pago.documents.user}")
    private String msDocumentsUser;
    @Value("${asp.pago.documents.pass}")
    private String msDocumentsPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msDocumentsUser + ":" + msDocumentsPass).getBytes()
        );
    }

    public RespuestaDTO generarDocumentacionCuentaSimplificada(String cuentaah, boolean subirDoc) {
        // Construir el cuerpo de la solicitud
        new RespuestaDTO();
        RespuestaDTO respuesta;
        GeneraDocCtaSimplificadaReq generaDocRequest = new GeneraDocCtaSimplificadaReq();
        generaDocRequest.setCuentaah(cuentaah);
        generaDocRequest.setSubirDoc(subirDoc);
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaDocCtaSimpService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<GeneraDocCtaSimplificadaReq> requestEntity = new HttpEntity<>(generaDocRequest, headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

