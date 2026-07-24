package mx.net.asp.asp_pago_management.ws.asp;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ReporteEstadoMovimientoOBJ;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_management.ws.asp.request.GeneraRteEdoMto;

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
public class MSReporteEstadoMovimiento {

    @Value("${ms.documents.host}")
    private String msDocumentsHost;
    @Value("${ms.documents.port}")
    private String msDocumentsPort;
    @Value("${ms.documents.genera.rte.edo.movimiento}")
    private String generaRteEdoMovimiento;
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

    public RespuestaDTO generarReporteEdoMovimientos(ReporteEstadoMovimientoOBJ req, boolean subirDoc) {
        // Construir el cuerpo de la solicitud
        GeneraRteEdoMto generaDocRequest = new GeneraRteEdoMto();
        generaDocRequest.setCuenta(req.getCuenta());
        generaDocRequest.setFechaInicial(req.getFechaInicial());
        generaDocRequest.setFechaFinal(req.getFechaFinal());
        generaDocRequest.setSubirDoc(subirDoc);
        String url = InvokeRestServiceUtil.getURLRest(msDocumentsHost, msDocumentsPort, generaRteEdoMovimiento);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<GeneraRteEdoMto> requestEntity = new HttpEntity<>(generaDocRequest, headers);
        return invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
    }
}

