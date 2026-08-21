package mx.net.asp.asp_pago_management.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.request.ConsultarTarjetaReq;
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
public class WSConsultarTarjeta {

    @Value("${ws.admin.plasticos.services.host}")
    private String wsAdminPlasticosHost;
    @Value("${ws.admin.plasticos.services.port}")
    private String wsAdminPlasticosPort;
    @Value("${ws.consultar.tarjeta.service}")
    private String fondearTarjetaOrquestadorService;
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

    public RespuestaDTO consultarTarjeta(ConsultarTarjetaReq req) {
        // Construir el cuerpo de la solicitud
        RespuestaDTO respuesta;
        String url = InvokeRestServiceUtil.getURLRest(wsAdminPlasticosHost, wsAdminPlasticosPort, fondearTarjetaOrquestadorService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<ConsultarTarjetaReq> requestEntity = new HttpEntity<>(req, headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() == 0) {
            //deserealizamos la respuesta del servicio guardada en el campo data
            respuesta = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
        } else if (respuesta.getCodigo() >= -503 && respuesta.getCodigo() <= -500) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

