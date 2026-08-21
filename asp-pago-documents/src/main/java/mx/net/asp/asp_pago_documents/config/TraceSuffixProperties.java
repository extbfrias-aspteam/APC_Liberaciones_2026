package mx.net.asp.asp_pago_documents.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "trace.aspp")
@Component
public class TraceSuffixProperties {

	private Map<String, String> suffixes;

    public Map<String, String> getSuffixes() { return suffixes; }
    public void setSuffixes(Map<String, String> suffixes) { this.suffixes = suffixes; }
}
