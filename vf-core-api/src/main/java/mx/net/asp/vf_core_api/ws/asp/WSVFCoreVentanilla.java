package mx.net.asp.vf_core_api.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.request.ObtenerTokenReq;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSVFCoreVentanilla {

    private String msVFCoreVentanillaHost;
    private String msVFCoreVentanillaPort;
    private String msVFCoreVentanillaUser;
    private String msVFCoreVentanillaPass;

    private String msObtenerTokenService;
    private String msVFService;
    private String msBloqComisionistaEmpService;
    private String msBloqComisionistaRefService;
    private String msConsultarRetencionesService;
    private String msConsultarSaldoAhorroService;
    private String msConsultarSaldoCreditoService;
    private String msDepositoAhorroService;
    private String msEnviarNotificacionesService;
    private String msRetiroAhorroService;
    private String msPagoCreditoService;

    private Integer timeoutExtendedPlusMs;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreVentanillaUser + ":" + msVFCoreVentanillaPass).getBytes()
        );
    }

    public RespuestaDTO obtenerToken(ObtenerTokenReq req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msObtenerTokenService);

        // Convertimos el objeto req a JSON string
        String jsonReq = gson.toJson(req);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO procesaWsVf(WsVfReq req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msVFService);

        // Convertimos el objeto req a JSON string
        String jsonReq = gson.toJson(req);
        /* -------------------------------------------------Ticket #68131------------------------------------------------- */
        /* Timeout extended 3 minutes */
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPostConTimeout(url, jsonReq, getAuthorizationHeader(), timeoutExtendedPlusMs, MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO bloqueaComisionistaEmpleado(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msBloqComisionistaEmpService);
        
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarComisionistaReferencia(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msBloqComisionistaRefService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO consultarRetenciones(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msConsultarRetencionesService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO consultaSaldoAhorro(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msConsultarSaldoAhorroService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO consultaSaldoCredito(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msConsultarSaldoCreditoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO depositoAhorro(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msDepositoAhorroService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO enviarNotificaciones(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msEnviarNotificacionesService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO retiroAhorro(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msRetiroAhorroService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO pagoCredito(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreVentanillaHost, msVFCoreVentanillaPort, msPagoCreditoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }
}