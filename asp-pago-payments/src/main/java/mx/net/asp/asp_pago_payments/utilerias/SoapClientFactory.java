package mx.net.asp.asp_pago_payments.utilerias;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

@Component
public class SoapClientFactory {

    public WebServiceTemplate createWebServiceTemplate(String endpoint) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setDefaultUri(endpoint);
        webServiceTemplate.setMessageSender(new HttpUrlConnectionMessageSender()); // Usa HttpURLConnection
        return webServiceTemplate;
    }
}
