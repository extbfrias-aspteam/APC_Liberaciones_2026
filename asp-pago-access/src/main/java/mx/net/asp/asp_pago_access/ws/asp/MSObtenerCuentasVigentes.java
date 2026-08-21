package mx.net.asp.asp_pago_access.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class MSObtenerCuentasVigentes {

    @Value("${ms.management.host}")
    private String msManagementHost;
    @Value("${ms.management.port}")
    private String msManagementPort;
    @Value("${ms.management.obtener.cuentas.vigentes}")
    private String obtenerCuentasVigentesService;
    @Value("${asp.pago.management.user}")
    private String msManagementUser;
    @Value("${asp.pago.management.pass}")
    private String msManagementPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msManagementUser + ":" + msManagementPass).getBytes()
        );
    }

    public RespuestaDTO obtenerCuentasVigentes(String personaId) {
        // Construir el cuerpo de la solicitud
        RespuestaDTO respuesta;
        Map<String, String> params = new HashMap<>();
        params.put("personaId", personaId);
        //consultaSaldoReq.setCuenta(cuentaah);
        String url = InvokeRestServiceUtil.getURLRest(msManagementHost, msManagementPort, obtenerCuentasVigentesService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.GET, params);
        if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

