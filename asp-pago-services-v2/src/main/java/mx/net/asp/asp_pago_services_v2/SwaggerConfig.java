package mx.net.asp.asp_pago_services_v2;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Configuration
public class SwaggerConfig {

//    private final String username = "ASP";
//    private final String password = "a5p*2o2A$";
//    @Bean
//    public RestTemplateCustomizer restTemplateCustomizer() {
//        return restTemplate -> restTemplate.setInterceptors(Collections.singletonList(getBasicAuthInterceptor()));
//    }
//
//    private ClientHttpRequestInterceptor getBasicAuthInterceptor() {
//        return (request, body, execution) -> {
//            String auth = username + ":" + password;
//            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
//            request.getHeaders().add("Authorization", "Basic " + encodedAuth);
//            return execution.execute(request, body);
//        };
//    }
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("ASP Pago Services API")
//                        .version("v1")
//                        .description("Documentación de la API para ASP Pago Services"))
//                .components(new Components()
//                        .addSecuritySchemes("basicScheme", new SecurityScheme()
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("basic")))
//                .addSecurityItem(new SecurityRequirement().addList("basicScheme"));
//    }
}
