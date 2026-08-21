package mx.net.asp.asp_pago_payments.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.request.ProcesaReq;
import mx.net.asp.asp_pago_payments.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
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
public class WSProcesaVF {

    @Value("${ws.ventanilla.facil.host}")
    private String wsVentanillaFacilWSHost;
    @Value("${ws.ventanilla.facil.port}")
    private String wsVentanillaFacilWSPort;
    @Value("${ws.procesa.vf.service}")
    private String procesaPagoVFService;
    @Value("${ventanilla.facil.user}")
    private String wsVentanillaFacilWSUser;
    @Value("${ventanilla.facil.pass}")
    private String wsVentanillaFacilWSPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (wsVentanillaFacilWSUser + ":" + wsVentanillaFacilWSPass).getBytes()
        );
    }

    public RespuestaDTO procesaPagoVF(ProcesaReq req) {
        // Construir el cuerpo de la solicitud
        new RespuestaDTO();
        RespuestaDTO respuesta;
        String url = InvokeRestServiceUtil.getURLRest(wsVentanillaFacilWSHost, wsVentanillaFacilWSPort, procesaPagoVFService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Configurar la solicitud con los encabezados
        HttpEntity<String> requestEntity = new HttpEntity<>(gson.toJson(req), headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() == 0) {
            //Obtenemos la respuesta del servicio guardada en el campo data, la cual es un objeto RespuestaDTO
            new RespuestaDTO();
            RespuestaDTO resVentanillaWS;
            resVentanillaWS = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
            return resVentanillaWS;
        } else if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}

