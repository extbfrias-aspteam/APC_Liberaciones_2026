package mx.net.asp.asp_pago_onb.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.ws.asp.request.GeneraClabeRequest;
import mx.net.asp.asp_pago_onb.ws.asp.request.HeaderWS;
import mx.net.asp.asp_pago_onb.ws.asp.response.GeneraClabeResponse;
import mx.net.asp.asp_pago_onb.ws.asp.response.ParametroBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;

@Service
@Log4j2
@RequiredArgsConstructor
public class GeneraClabeIfzServiceClient {

    private final ErrorHandler errorHandler;
    @Value("${url.mule.sti}")
    private String urlMuleSti;

    public String generarClabe(Integer producto, String referencia, String agente) {
        String clabe = "";
        try {
            // Modificar la URL del WSDL, cambiando localhost por la URL de producción
            URI uri = URI.create(GeneraClabeIfzService.WSDL_LOCATION.toString().replace("localhost", urlMuleSti));
            URL wsdl = uri.toURL();

            log.info("----> Invocando servicio en: {}", wsdl);

            // Crear el servicio usando el WSDL
            jakarta.xml.ws.Service service = jakarta.xml.ws.Service.create(wsdl, GeneraClabeIfzService.SERVICE);
            GeneraClabeIfz port = service.getPort(GeneraClabeIfzService.GeneraClabeIfzPort, GeneraClabeIfz.class);

            // Crear la solicitud
            GeneraClabeRequest req = new GeneraClabeRequest();
            HeaderWS header = new HeaderWS();
            header.setIdEmpresa(1L);
            header.setIdUbicacion(1L);
            req.setHeader(header);
            req.setProductoId(producto.longValue());
            req.setClaveAplicacion("AHT");
            req.setReferencia(referencia);
            req.setClaveRegion(agente);
            req.setObtenerClabe(1);

            log.info("Producto id: {}", req.getProductoId());
            log.info("Aplicacion: {}", req.getClaveAplicacion());
            log.info("Referencia: {}", req.getReferencia());
            log.info("Region: {}", req.getClaveRegion());
            log.info("Obtener clave: {}", req.getObtenerClabe());

            // Llamar al servicio
            GeneraClabeResponse response = port.procesar(req);

            // Verificar la respuesta
            if (response.getReturn().getErrores().getCodigoError() != 0) {
                log.error("{}: {}", response.getReturn().getErrores().getCodigoError(), response.getReturn().getErrores().getDescError());
            } else {
                // Buscar la clave en los parametros de la respuesta
                for (ParametroBody res : response.getReturn().getBody().getParams()) {
                    log.info("{}: {}", res.getNombre(), res.getValor());
                    if ("clabe".equals(res.getNombre())) {
                        clabe = res.getValor();
                        break;
                    }
                }
                // Si encontramos la clabe, la retornamos
                if (clabe != null) {
                    return clabe;
                }
            }
        } catch (Exception e) {
            log.error("Ocurrió un error al generar la clabe interbancaria:");
            errorHandler.handleException(e);
            return "";
        }

        return clabe;
    }
}
