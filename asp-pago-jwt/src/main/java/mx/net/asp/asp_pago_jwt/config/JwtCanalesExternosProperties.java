package mx.net.asp.asp_pago_jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "jwt.canales")
@Component
public class JwtCanalesExternosProperties {
    private List<String> externos;

    public List<String> getExternos() {
        return externos;
    }

    public void setExternos(List<String> externos) {
        this.externos = externos;
    }
}