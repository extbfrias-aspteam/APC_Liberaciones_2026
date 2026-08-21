package mx.net.asp.asp_pago_payments.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.ws.asp.request.AplicaPagoReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSAplicaPagoCF {

    @Value("${ws.creditows.host}")
    private String wsCreditoWSHost;
    @Value("${ws.creditows.port}")
    private String wsCreditoWSPort;
    @Value("${ws.creditows.aplicar.pago.cf}")
    private String aplicaPagoCFService;
    @Value("${creditows.user}")
    private String wsCreditoWSUser;
    @Value("${creditows.pass}")
    private String wsCreditoWSPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsCreditoWSUser + ":" + wsCreditoWSPass).getBytes()
        );
    }

    public RespuestaDTO aplicaPagoCf(AplicaPagoReq req) {
        // Construir el cuerpo de la solicitud
        new RespuestaDTO();
        RespuestaDTO respuesta;
        String url = InvokeRestServiceUtil.getURLRest(wsCreditoWSHost, wsCreditoWSPort, aplicaPagoCFService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<AplicaPagoReq> requestEntity = new HttpEntity<>(req, headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() == 0) {
            log.info("respuesta:: {}", respuesta);
            //Obtenemos la respuesta del servicio guardada en el campo data, la cual es un objeto RespuestaDTO
            RespuestaDTO resCreditoWS;
            resCreditoWS = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
            return resCreditoWS;
        } else if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

