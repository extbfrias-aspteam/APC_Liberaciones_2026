package mx.net.asp.asp_pago_onb.ws.asp;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServiceClient(name = "GeneraClabeIfzService",
        wsdlLocation = "http://localhost:21081/esb/spei/genera-clabe?wsdl",
        targetNamespace = "http://generaClabe.spei.ws.izel.net/")
public class GeneraClabeIfzService extends Service {

    private static final Logger log = LogManager.getLogger(GeneraClabeIfzService.class);
    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://generaClabe.spei.ws.izel.net/", "GeneraClabeIfzService");
    public final static QName GeneraClabeIfzPort = new QName("http://generaClabe.spei.ws.izel.net/", "GeneraClabeIfzPort");
    static {
        URL url = null;
        try {
            URI uri = URI.create("http://localhost:21081/esb/spei/genera-clabe?wsdl");
            url = uri.toURL();
        } catch (MalformedURLException e) {
            log.info(
                    "No se puede inicializar el WSDL desde {}", "http://localhost:21081/esb/spei/genera-clabe?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public GeneraClabeIfzService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public GeneraClabeIfzService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public GeneraClabeIfzService() {
        super(WSDL_LOCATION, SERVICE);
    }

    // Este constructor requiere la API JAX-WS 2.2. Necesitarás incluir el JAX-WS 2.2
    // o re-ejecutar wsdl2java con "-frontend jaxws21" para generar código compatible con JAX-WS 2.1
    public GeneraClabeIfzService(WebServiceFeature... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    // Este constructor requiere la API JAX-WS 2.2.
    public GeneraClabeIfzService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SERVICE, features);
    }

    // Este constructor requiere la API JAX-WS 2.2.
    public GeneraClabeIfzService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     devuelve GeneraClabeIfz
     */
    @WebEndpoint(name = "GeneraClabeIfzPort")
    public GeneraClabeIfz getGeneraClabeIfzPort() {
        return super.getPort(GeneraClabeIfzPort, GeneraClabeIfz.class);
    }

    /**
     *
     * @param features
     *     Una lista de {@link jakarta.xml.ws.WebServiceFeature} para configurar en el proxy. Las características soportadas que no estén en el parámetro <code>features</code> tendrán sus valores predeterminados.
     * @return
     *     devuelve GeneraClabeIfz
     */
    @WebEndpoint(name = "GeneraClabeIfzPort")
    public GeneraClabeIfz getGeneraClabeIfzPort(WebServiceFeature... features) {
        return super.getPort(GeneraClabeIfzPort, GeneraClabeIfz.class, features);
    }

}