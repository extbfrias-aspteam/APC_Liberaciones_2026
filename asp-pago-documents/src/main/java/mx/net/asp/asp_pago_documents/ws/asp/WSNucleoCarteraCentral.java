package mx.net.asp.asp_pago_documents.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AttachmentOBJ;
import mx.net.asp.asp_pago_documents.model.ExpedienteOBJ;
import mx.net.asp.asp_pago_documents.model.HeaderWS;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.InvokeRestServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSNucleoCarteraCentral {

    @Value("${ws.NcCarteraWs.host}")
    private String wsNcCarteraWsHost;
    @Value("${ws.NcCarteraWs.port}")
    private String wsNcCarteraWsPort;
    @Value("${ws.NcCarteraWs.sube.documentos.service}")
    private String wsNcCarteraWsSubeDocService;
    @Value("${ws.NcCarteraWs.user}")
    private String wsNcCarteraWsUser;
    @Value("${ws.NcCarteraWs.pass}")
    private String wsNcCarteraWsPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsNcCarteraWsUser + ":" + wsNcCarteraWsPass).getBytes()
        );
    }

    @Async
    public CompletableFuture<RespuestaDTO> subirDocumentos(String cuenta, String solicitanteId, List<AttachmentOBJ> attachmentsList, List<Map<String, Object>> archivosNombres, HeaderWS header) {
        // URL del servicio REST
        String url = InvokeRestServiceUtil.getURLRest(wsNcCarteraWsHost, wsNcCarteraWsPort, wsNcCarteraWsSubeDocService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", getAuthorizationHeader());

        // Crear el cuerpo de la solicitud multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Agregar los parámetros adicionales
        body.add("header", gson.toJson(header));
        body.add("cuenta", cuenta);
        body.add("modulo", "AHORRO");
        body.add("ruta", solicitanteId + "/" + "AHORRO" + "/" + cuenta);
        body.add("persona_id", solicitanteId);

        // Procesar los attachments y agregarlos al cuerpo
        for (int i = 0; i < attachmentsList.size(); i++) {
            AttachmentOBJ attachment = attachmentsList.get(i);
            Map<String, Object> archivoInfo = archivosNombres.get(i);

            // Crear el objeto ExpedienteOBJ
            ExpedienteOBJ expedienteObj = new ExpedienteOBJ();
            expedienteObj.setExtension(String.valueOf(archivoInfo.get("nombre")).split("\\.")[1]);  // Extensión
            expedienteObj.setNombre(String.valueOf(archivoInfo.get("nombre")).split("\\.")[0]);    // Nombre
            expedienteObj.setIdentificador_documento((int) archivoInfo.get("id"));                 // ID del documento
            expedienteObj.setDocumento("");                                                       // Sin contenido adicional

            // Agregar el expediente serializado al cuerpo
            body.add("expediente", gson.toJson(expedienteObj));

            // Crear un recurso para el archivo y agregarlo al cuerpo
            ByteArrayResource resource = new ByteArrayResource(attachment.getTheAttachmentBytes()) {
                @Override
                public String getFilename() {
                    return String.valueOf(archivoInfo.get("nombre")); // Usar el nombre del archivo
                }
            };
            // Crear los encabezados para este archivo
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(attachment.getTheContentType()));  // Establecer el Content-Type
            // Crear un HttpEntity con los encabezados y el recurso
            HttpEntity<ByteArrayResource> entity = new HttpEntity<>(resource, fileHeaders);
            // Agregar el archivo al cuerpo de la solicitud
            body.add("file", entity);
        }

        // Si la lista solo tiene 1 elemento, agregar un expediente vacío
        if (attachmentsList.size() == 1) {
            body.add("expediente", new ExpedienteOBJ()); // Agrega un objeto vacío
        }

        // Construir la solicitud HTTP con los encabezados y el cuerpo
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Se realiza esto porque el servicio que se consume siempre va a contestar codigo 200 aunque haya fallado,
        // por lo que la verdadera respuesta se esta guardando en el campo data (codigo, mensaje, data)

        // Enviar la solicitud asincrónicamente
        // Mapear el campo "data" que contiene la verdadera respuesta del servicio al objeto RespuestaDTO
        // Crear un RespuestaDTO con información de error
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null)
                .thenApply(response -> {
                    try {
                        // Mapear el campo "data" que contiene la verdadera respuesta del servicio al objeto RespuestaDTO
                        if (response.getCodigo() == 0) {
                            return gson.fromJson(response.getData(), RespuestaDTO.class);
                        } else {
                            // Crear un RespuestaDTO con información de error
                            RespuestaDTO errorRespuesta = new RespuestaDTO();
                            errorRespuesta.setCodigo(response.getCodigo());
                            errorRespuesta.setMensaje(response.getMensaje());
                            return errorRespuesta;
                        }
                    } catch (Exception e) {
                        log.error("Error procesando la respuesta de {}: ", wsNcCarteraWsSubeDocService);
                        errorHandler.handleException(e);
                        RespuestaDTO errorRespuesta = new RespuestaDTO();
                        errorRespuesta.setCodigo(response.getCodigo());
                        errorRespuesta.setMensaje(response.getMensaje());
                        return errorRespuesta;
                    }
                });
    }
}

