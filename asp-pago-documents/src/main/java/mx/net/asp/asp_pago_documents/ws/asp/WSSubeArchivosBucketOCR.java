package mx.net.asp.asp_pago_documents.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_documents.ws.asp.request.ValidatorOCRReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSSubeArchivosBucketOCR {

    @Value("${ws.bucket.files.host}")
    private String wsBucketFilesHost;
    @Value("${ws.bucket.subir.archivos.service}")
    private String subirArchivosBucketService;
    @Value("${ws.bucket.obtener.ocr.service}")
    private String obtenerOcrBucketService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;

    @Value("${api.key.bucket}")
    private String xApiKeyBucket;

    public RespuestaDTO subeArchivosBucket(String requestType, String storageOption, MultipartFile[] files) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String url = InvokeRestServiceUtil.getURLRest(wsBucketFilesHost, "", subirArchivosBucketService);
        // Construir el cuerpo de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //headers.set("Authorization", getAuthorizationHeader());
        headers.set("X-API-Key", xApiKeyBucket);
        // Crear el cuerpo del formulario multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("requestType", requestType);
        body.add("storageOption", storageOption);
        for (MultipartFile file : files) {
            try {
                // Crear un ByteArrayResource con los bytes del archivo
                ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
                // Crear los encabezados para este archivo
                HttpHeaders fileHeaders = new HttpHeaders();
                fileHeaders.setContentType(
                        file.getContentType() != null
                                ? MediaType.parseMediaType(file.getContentType())
                                : MediaType.APPLICATION_OCTET_STREAM
                );
                // Crear un HttpEntity con los encabezados y el recurso
                HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(resource, fileHeaders);
                // Agregar el archivo al cuerpo de la solicitud
                body.add("files", fileEntity);
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar el archivo: " + file.getOriginalFilename(), e);
            }
        }
        // Configurar la solicitud con el encabezado y cuerpo multipart
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        return invokeRestServiceUtil.enviarSolicitudBucketOcr(url, requestEntity, HttpMethod.POST, null);
    }

    public RespuestaDTO obtenerOcrBucket(ValidatorOCRReq req) {
        String url = InvokeRestServiceUtil.getURLRest(wsBucketFilesHost, "", obtenerOcrBucketService);
        // Construir el cuerpo de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", xApiKeyBucket);
        // Configurar la solicitud con el encabezado y cuerpo multipart
        HttpEntity<ValidatorOCRReq> requestEntity = new HttpEntity<>(req, headers);
        return invokeRestServiceUtil.enviarSolicitudBucketOcr(url, requestEntity, HttpMethod.POST, null);
    }
}

