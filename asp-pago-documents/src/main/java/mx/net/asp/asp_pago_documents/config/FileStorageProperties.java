package mx.net.asp.asp_pago_documents.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "app.files")
@Getter
@Setter
public class FileStorageProperties {

    private Map<String, Set<String>> allowedContentTypes = new HashMap<>();
}
