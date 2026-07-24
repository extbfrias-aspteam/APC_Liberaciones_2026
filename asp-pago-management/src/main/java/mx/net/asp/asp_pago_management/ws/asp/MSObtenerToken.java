package mx.net.asp.asp_pago_management.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ObtenerTokenOBJ;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


@Service
@Log4j2
@RequiredArgsConstructor
public class MSObtenerToken {
    
    @Value("${ws.consulta.auth.token.host}")
    private String msConsultaAuthTokenHost;
    @Value("${ws.obtener.auth.token.service}")
    private String msObtenerAuthTokenPort;
    @Value("${cecoban.token.services.user}")
    private String msCecobanTokenUser;
    @Value("${cecoban.token.services.pass}")
    private String msCecobanTokenPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;

    public RespuestaDTO obtenerToken() {
        // Construir el cuerpo de la solicitud
        ObtenerTokenOBJ obtenerTokenOBJ = new ObtenerTokenOBJ();
        obtenerTokenOBJ.setUsername(msCecobanTokenUser);
        obtenerTokenOBJ.setPassword(msCecobanTokenPass);
        String url = InvokeRestServiceUtil.getURLRest(msConsultaAuthTokenHost, "", msObtenerAuthTokenPort);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Configurar la solicitud con los encabezados
        HttpEntity<ObtenerTokenOBJ> requestEntity = new HttpEntity<>(obtenerTokenOBJ, headers);
        return invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
    }
}
