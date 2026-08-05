package mx.net.asp.vf_core_api.config;

import mx.net.asp.vf_core_api.utilerias.CustomResponseErrorHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Value("${trace.id.header}")
    private String traceIdHeader;
    @Value("${trace.id.header.signature}")
    private String traceSignatureHeader;
    @Value("${timeout.default.ms}")
    private Integer timeoutDefaultMs;

    @Bean
    public RestTemplate restTemplate(CustomResponseErrorHandler customResponseErrorHandler) {
        int timeout = timeoutDefaultMs;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout, TimeUnit.MILLISECONDS)
                .setResponseTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.setErrorHandler(customResponseErrorHandler);

        // Agregar interceptor para enviar cabeceras de TraceId y Firma
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String traceId = org.apache.logging.log4j.ThreadContext.get("myUuid");
            String traceSignature = org.apache.logging.log4j.ThreadContext.get("myUuidSigned");

            request.getHeaders().add(traceIdHeader, traceId != null ? traceId : "");
            request.getHeaders().add(traceSignatureHeader, traceSignature != null ? traceSignature : "");

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
