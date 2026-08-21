package mx.net.asp.asp_pago_services_v2.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.AttachmentOBJ;
import mx.net.asp.asp_pago_services_v2.ws.asp.request.EnviaEmailReq;
import mx.net.asp.asp_pago_services_v2.utilerias.InvokeRestServiceUtil;
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
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class MSEnviarEmail {

    @Value("${ms.notification.host}")
    private String msNotificationHost;
    @Value("${ms.notification.port}")
    private String msNotificationPort;
    @Value("${ms.notification.enviar.mail.service}")
    private String mailService;
    @Value("${ms.notification.enviar.mail.attachment.service}")
    private String mailAttachmentService;
    @Value("${asp.pago.notification.user}")
    private String msNotificationUser;
    @Value("${asp.pago.notification.pass}")
    private String msNotificationPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msNotificationUser + ":" + msNotificationPass).getBytes()
        );
    }

    @Async
    public CompletableFuture<RespuestaDTO> enviarCorreo(String destinatario, String asunto, String contenido) {
        // Construir el cuerpo de la solicitud
        EnviaEmailReq emailRequest = new EnviaEmailReq();
        emailRequest.setDestinatario(destinatario);
        emailRequest.setAsunto(asunto);
        emailRequest.setContenido(contenido);
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, mailService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<EnviaEmailReq> requestEntity = new HttpEntity<>(emailRequest, headers);
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null);
    }

    @Async
    public CompletableFuture<RespuestaDTO> enviarCorreoWithAttachment(String destinatario, String asunto, String contenido, List<AttachmentOBJ> atachmentsList) {
        // Construir el cuerpo de la solicitud
        // URL del microservicio de correo electrónico
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, mailAttachmentService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", getAuthorizationHeader());
        // Crear el cuerpo del formulario multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("destinatario", destinatario);
        body.add("asunto", asunto);
        body.add("contenido", contenido);
        for (AttachmentOBJ atachment : atachmentsList) {
            // Crear un ByteArrayResource con los bytes del archivo
            ByteArrayResource resource = new ByteArrayResource(atachment.getTheAttachmentBytes()) {
                @Override
                public String getFilename() {
                    return atachment.getTheFilename();  // Usar el nombre del archivo especificado
                }
            };
            // Crear los encabezados para este archivo
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(atachment.getTheContentType()));  // Establecer el Content-Type
            // Crear un HttpEntity con los encabezados y el recurso
            HttpEntity<ByteArrayResource> entity = new HttpEntity<>(resource, fileHeaders);
            // Agregar el archivo al cuerpo de la solicitud
            body.add("archivos", entity);
        }
        // Configurar la solicitud con el encabezado y cuerpo multipart
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null);
    }

    @Async
    public CompletableFuture<RespuestaDTO> enviarCorreoWithOneAttachment(String destinatario, String asunto, String contenido, AttachmentOBJ attachmentOBJ) {
        // Construir el cuerpo de la solicitud
        // URL del microservicio de correo electrónico
        String url = InvokeRestServiceUtil.getURLRest(msNotificationHost, msNotificationPort, mailAttachmentService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", getAuthorizationHeader());
        // Crear el cuerpo del formulario multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("destinatario", destinatario);
        body.add("asunto", asunto);
        body.add("contenido", contenido);
        // Crear un ByteArrayResource con los bytes del archivo
        ByteArrayResource resource = new ByteArrayResource(attachmentOBJ.getTheAttachmentBytes()) {
            @Override
            public String getFilename() {
                return attachmentOBJ.getTheFilename();  // Usar el nombre del archivo especificado
            }
        };
        // Crear los encabezados para este archivo
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(attachmentOBJ.getTheContentType()));  // Establecer el Content-Type
        // Crear un HttpEntity con los encabezados y el recurso
        HttpEntity<ByteArrayResource> entity = new HttpEntity<>(resource, fileHeaders);
        // Agregar el archivo al cuerpo de la solicitud
        body.add("archivos", entity);
        // Configurar la solicitud con el encabezado y cuerpo multipart
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return invokeRestServiceUtil.enviarSolicitudAsync(url, requestEntity, HttpMethod.POST, null);
    }
}

