package mx.net.asp.asp_pago_onb.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
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
public class MSValidaCurpNubarium {

    @Value("${ms.management.host}")
    private String msManagementHost;
    @Value("${ms.management.port}")
    private String msManagementPort;
    @Value("${ms.management.valida.curp.nub}")
    private String validaCurpNubService;
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

    public RespuestaDTO validaCurpNibarium(String curp) {
        // Construir el cuerpo de la solicitud
        new RespuestaDTO();
        RespuestaDTO respuesta;
        //ConsultaSaldoReq consultaSaldoReq = new ConsultaSaldoReq();
        Map<String, String> params = new HashMap<>();
        params.put("curp", curp);
        //consultaSaldoReq.setCuenta(cuentaah);
        String url = InvokeRestServiceUtil.getURLRest(msManagementHost, msManagementPort, validaCurpNubService);
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

