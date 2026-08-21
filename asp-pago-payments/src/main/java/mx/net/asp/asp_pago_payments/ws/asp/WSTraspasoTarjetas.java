package mx.net.asp.asp_pago_payments.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.ws.asp.request.TraspasoTarjetasReq;
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
public class WSTraspasoTarjetas {

    @Value("${ws.admin.plasticos.services.host}")
    private String wsAdminPlasticosHost;
    @Value("${ws.admin.plasticos.services.port}")
    private String wsAdminPlasticosPort;
    @Value("${ws.traspaso.tarjetas.service}")
    private String traspasoTarjetasService;
    @Value("${admin.plasticos.services.user}")
    private String wsAdminPlasticosUser;
    @Value("${admin.plasticos.services.pass}")
    private String wsAdminPlasticosPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsAdminPlasticosUser + ":" + wsAdminPlasticosPass).getBytes()
        );
    }

    public RespuestaDTO traspasoTarjetas(TraspasoTarjetasReq reqTra) {
        // Construir el cuerpo de la solicitud
        new RespuestaDTO();
        RespuestaDTO respuesta;
        String url = InvokeRestServiceUtil.getURLRest(wsAdminPlasticosHost, wsAdminPlasticosPort, traspasoTarjetasService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<TraspasoTarjetasReq> requestEntity = new HttpEntity<>(reqTra, headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() == 0) {
            //deserealizamos la respuesta del servicio guardada en el campo data
            respuesta = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
        } else if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

