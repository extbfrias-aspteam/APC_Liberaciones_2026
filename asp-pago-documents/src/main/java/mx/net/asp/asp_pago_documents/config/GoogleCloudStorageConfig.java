package mx.net.asp.asp_pago_documents.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@EnableConfigurationProperties(GcpStorageProperties.class)
public class GoogleCloudStorageConfig {

    @Bean
    public Storage storage(GcpStorageProperties properties) throws IOException {
        Path path = Path.of(properties.getCredentialsPath());

        if (!Files.exists(path)) {
            throw new IllegalStateException(
                    "No existe el archivo de credenciales en la ruta: " + path.toAbsolutePath()
            );
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

            return StorageOptions.newBuilder()
                    .setProjectId(properties.getProjectId())
                    .setCredentials(credentials)
                    .build()
                    .getService();
        }
    }
}
