package mx.net.asp.vf_core_api.ws.asp.vf_credito_ws;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.ws.asp.request.BusquedaClientesMorososReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSVFCoreCredito_REGISTROS {

    @Value("${ms.vf.core.credito.host}")
    private String msVFCoreCreditoHost;
    @Value("${ms.vf.core.credito.port}")
    private String msVFCoreCreditoPort;
    @Value("${vf.core.credito.user}")
    private String msVFCoreCreditoUser;
    @Value("${vf.core.credito.pass}")
    private String msVFCoreCreditoPass;

    @Value("${ms.alta.contrato.service}")
    private String msAltaContratoService;
    @Value("${ms.alta.solicitud.service}")
    private String msAltaSolicitudService;
    @Value("${ms.registrar.domiciliacion.service}")
    private String msRegistrarDomiciliacionService;
    @Value("${ms.registrar.beneficiarios.seguro.service}")
    private String registrarBeneficiariosSeguro;
    @Value("${ms.registrar.datos.solicitud.service}")
    private String msRegistrarDatosSolicitudService;
    @Value("${ms.registrar.disposicion.linea.service}")
    private String msRegistrarDisposicionLineaService;
    @Value("${ms.registrar.movimiento.service}")
    private String msRegistrarMovimientoService;
    @Value("${ms.guardar.expediente.solicitud.service}")
    private String msGuardarExpedienteSolicitudService;
    @Value("${ms.solicitar.aumento.service}")
    private String msSolicitarAumentoService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreCreditoUser + ":" + msVFCoreCreditoPass).getBytes()
        );
    }

    public RespuestaDTO altaContrato(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msAltaContratoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO altaSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msAltaSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO registraDomiciliacion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msRegistrarDomiciliacionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO registrarBeneficiariosSeguro(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, registrarBeneficiariosSeguro);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO registrarDatosSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msRegistrarDatosSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO registrarDisposicionDeLinea(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msRegistrarDisposicionLineaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO registrarMovimiento(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msRegistrarMovimientoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO guardarExpedienteSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msGuardarExpedienteSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO solcitarAumento(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msSolicitarAumentoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }
}