package mx.net.asp.vf_core_api.utilerias;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
@RequiredArgsConstructor
public class InvokeRestServiceUtil {

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final String causaMessage = "Causa: ";
    private final String noCausaErrorMessage = "No hay causa";

    private String msBuscarSolicitanteCompService;
    private String msBuscarColoniaRegionService;

    private List<String> urlsConBodyOculto;

    @PostConstruct
    public void init() {
        urlsConBodyOculto = Arrays.asList(
                msBuscarSolicitanteCompService,
                msBuscarColoniaRegionService
        );
    }

    public CompletableFuture<RespuestaDTO> enviarSolicitudAsync(String url, HttpEntity<?> requestEntity, HttpMethod method, Map<String, String> queryParams) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Construcción de la URL con parámetros si existen
            if (queryParams != null && !queryParams.isEmpty()) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
                queryParams.forEach(builder::queryParam);
                url = builder.toUriString();
            }

            ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("-----> Respuesta exitosa: " + response.getBody());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(response.getBody());
                return CompletableFuture.completedFuture(respuesta);
            } else {
                log.error("-----> Respuesta ERROR: " + response.getBody());
                respuesta = gson.fromJson(response.getBody(), RespuestaDTO.class);
                return CompletableFuture.completedFuture(respuesta);
            }
        } catch (ResourceAccessException e) {
            // Manejo de errores específicos de acceso al recurso
            respuesta = manejarExcepcionAcceso(e);
            return CompletableFuture.completedFuture(respuesta);
        } catch (Exception e) {
            // Manejo de errores generales
            log.error("-----> Error general al enviar la solicitud: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            return CompletableFuture.completedFuture(crearRespuestaError("Error inesperado: " + e.getMessage()));
        }
    }

    public RespuestaDTO enviarSolicitud(String url, HttpEntity<?> requestEntity, HttpMethod method, Map<String, String> queryParams) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Construcción de la URL con parámetros si existen
            if (queryParams != null && !queryParams.isEmpty()) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
                queryParams.forEach(builder::queryParam);
                url = builder.toUriString();
            }

            // Verificar si la URL contiene "asp-pago-documents"
            boolean peticionDocuments = url.contains("asp-pago-documents");

            // Realizar la solicitud al servicio
            ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
            // Manejar respuesta exitosa (2xx)
            if (response.getStatusCode().is2xxSuccessful() || response.getStatusCode() == HttpStatus.FOUND) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");

                if (peticionDocuments) {
                    log.info("-----> Respuesta exitosa asp-pago-documents (Body oculto)");
                    byte[] bytes = response.getBody().getBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    respuesta.setData(base64);
                } else {
                    log.info("-----> Respuesta exitosa: " + response.getBody());
                    respuesta.setData(response.getBody());
                }
                return respuesta;
            } else {
                // Manejar errores con códigos HTTP no exitosos
                log.error("-----> Respuesta ERROR: " + response.getBody());
                respuesta = gson.fromJson(response.getBody(), RespuestaDTO.class);
                return respuesta;
            }
        } catch (ResourceAccessException e) {
            // Manejo de errores específicos de acceso al recurso
            respuesta = manejarExcepcionAcceso(e);
            return respuesta;
        } catch (Exception e) {
            // Manejo de errores generales
            log.error("-----> Error general al enviar la solicitud: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            return crearRespuestaError("Error inesperado: " + e.getMessage());
        }
    }

    public RespuestaDTO enviarSolicitudConTimeout(String url, HttpEntity<?> requestEntity, HttpMethod method,
                                                  Map<String, String> queryParams, int timeoutMs) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Construcción de la URL con parámetros si existen
            if (queryParams != null && !queryParams.isEmpty()) {
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
                queryParams.forEach(builder::queryParam);
                url = builder.toUriString();
            }

            // RestTemplate con timeout dinámico
            RestTemplate restTemplateTimeout = crearRestTemplateConTimeout(timeoutMs);

            // Realizar la solicitud al servicio
            ResponseEntity<String> response = restTemplateTimeout.exchange(url, method, requestEntity, String.class);
            // Manejar respuesta exitosa (2xx)
            if (response.getStatusCode().is2xxSuccessful() || response.getStatusCode() == HttpStatus.FOUND) {

                log.info("-----> Respuesta exitosa: " + response.getBody());

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(response.getBody());
                return respuesta;
            } else {
                // Manejar errores con códigos HTTP no exitosos
                log.error("-----> Respuesta ERROR: " + response.getBody());
                respuesta = gson.fromJson(response.getBody(), RespuestaDTO.class);
                return respuesta;
            }
        } catch (ResourceAccessException e) {
            // Manejo de errores específicos de acceso al recurso
            respuesta = manejarExcepcionAcceso(e);
            return respuesta;
        } catch (Exception e) {
            // Manejo de errores generales
            log.error("-----> Error general al enviar la solicitud: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            return crearRespuestaError("Error inesperado: " + e.getMessage());
        }
    }

    // Metodo para crear un objeto RespuestaDTO en caso de error
    private RespuestaDTO crearRespuestaError(String mensaje) {
        RespuestaDTO respuesta = new RespuestaDTO();
        respuesta.setMensaje(mensaje);
        respuesta.setCodigo(-500);
        respuesta.setData(null);
        return respuesta;
    }

    // Metodo para manejar ResourceAccessException y retornarlo como JSON
    private RespuestaDTO manejarExcepcionAcceso(ResourceAccessException e) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Throwable cause = e.getCause();
        if (cause instanceof java.net.SocketTimeoutException) {
            log.error("-----> Error SocketTimeoutException: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            respuesta.setMensaje("El servicio no respondió en el tiempo esperado.");
            respuesta.setCodigo(-501);
        } else if (cause instanceof java.net.ConnectException) {
            log.error("-----> Error ConnectException: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            respuesta.setMensaje("No se pudo establecer conexión con el servicio.");
            respuesta.setCodigo(-502);
        } else {
            log.error("-----> Error de acceso al recurso: " + e.getMessage());
            log.error(causaMessage + (e.getCause() != null ? e.getCause().getMessage() : noCausaErrorMessage));
            respuesta.setMensaje("Error al acceder al recurso: " + e.getMessage());
            respuesta.setCodigo(-503);
        }
        return respuesta;
    }

    public static String getURLRest(String ip, String puerto, String servicio) {
        // Declaracion de variable para la respuesta
        StringBuilder urlRest = new StringBuilder();
        // Validacion del parametro ip
        if (validaCadena(ip)) {
            // Se agrega la ip a la variable de respuesta
            urlRest.append(ip);
        }
        // Validacion del parametro puerto
        if (validaCadena(puerto)) {
            // Se agrega el puerto a la variable de respuesta
            urlRest.append(":").append(puerto);
        }
        // Validacion del parametro servicio
        if (validaCadena(servicio)) {
            // Se agrega el servicio a la variable de respuesta
            urlRest.append("/").append(servicio);
        }
        // Impresion de la respuesta
        log.info("[" + ip + "][" + puerto + "][" + servicio + "]:: [" + urlRest + "]");
        // Se regresa respuesta
        return urlRest.toString();
    }

    public static boolean validaCadena(String cadena) {
        // Declaracion de a variable para la respuesta
        boolean valida = false;
        // Validacion de null
        if (cadena != null) {
            // Eliminan espacios del inicio y final de la cadena
            cadena = cadena.trim();
            // Validacion de vacio
            if (!cadena.isEmpty()) {
                // Asignacion de true a la respuesta
                valida = true;
            }
        }
        // Se regresa respuesta
        return valida;
    }

    private RestTemplate crearRestTemplateConTimeout(int timeoutMs) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .setResponseTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate dynamicTemplate = new RestTemplate(clientHttpRequestFactory);

        // Copiar interceptores del RestTemplate principal
        dynamicTemplate.setInterceptors(restTemplate.getInterceptors());

        // Copiar el mismo ErrorHandler
        dynamicTemplate.setErrorHandler(restTemplate.getErrorHandler());

        return dynamicTemplate;
    }

    public <T> RespuestaDTO ejecutarPost(String url, T req, String auth, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.set("Authorization", auth);

        HttpEntity<?> requestEntity;
        if (req instanceof String) {
            requestEntity = new HttpEntity<>((String) req, headers);
        } else {
            requestEntity = new HttpEntity<>(req, headers);
        }

        RespuestaDTO respuesta = enviarSolicitud(
                url, requestEntity, HttpMethod.POST, null
        );

        validarErrores(respuesta);

        return respuesta;
    }

    public <T> RespuestaDTO ejecutarGet(String url, Map<String, String> queryParams, String auth) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", auth);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        RespuestaDTO respuesta = enviarSolicitud(
                url, requestEntity, HttpMethod.GET, queryParams
        );

        validarErrores(respuesta);

        return respuesta;
    }

    public <T> RespuestaDTO ejecutarPostConTimeout(String url, T req, String auth, int timeoutMs, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.set("Authorization", auth);

        HttpEntity<?> requestEntity;
        if (req instanceof String) {
            requestEntity = new HttpEntity<>((String) req, headers);
        } else {
            requestEntity = new HttpEntity<>(req, headers);
        }

        RespuestaDTO respuesta = enviarSolicitudConTimeout(
                url, requestEntity, HttpMethod.POST, null, timeoutMs
        );

        validarErrores(respuesta);

        return respuesta;
    }

    private void validarErrores(RespuestaDTO respuesta) {
        if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(
                    respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS)
            );
        }
    }
}
