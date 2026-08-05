package mx.net.asp.vf_core_api.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.ws.asp.request.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSVFCoreManagement {

    private String msVFCoreManagementHost;
    private String msVFCoreManagementPort;
    private String msVFCoreManagementUser;
    private String msVFCoreManagementPass;

    private String msValidaReferenciaService;
    private String msObtenerSigSecuenciaService;
    private String msBuscarSolicitanteCompService;
    private String msBuscarColoniaRegionService;
    private String msBuscarAgenteService;
    private String msConsultaCanalesService;
    private String msConsultaCanalesCuentaService;
    private String msLiveService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreManagementUser + ":" + msVFCoreManagementPass).getBytes()
        );
    }

    public RespuestaDTO validarReferencia(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msValidaReferenciaService);
        ValidaReferenciaReq req = gson.fromJson(jsonReq, ValidaReferenciaReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("referencia", Objects.toString(req.getReferencia(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO siguienteSecuencia(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msObtenerSigSecuenciaService);
        SiguienteSecuenciaReq req = gson.fromJson(jsonReq, SiguienteSecuenciaReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("secuencia", Objects.toString(req.getSecuencia(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO buscarSolicitanteCompleto(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msBuscarSolicitanteCompService);
        BscarSolicitanteCompletoReq req = gson.fromJson(jsonReq, BscarSolicitanteCompletoReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("numero", Objects.toString(req.getNumero(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO buscarColoniasPorRegion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msBuscarColoniaRegionService);
        BuscarColoniasPorRegionReq req = gson.fromJson(jsonReq, BuscarColoniasPorRegionReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("region", Objects.toString(req.getRegion(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO buscarAgente(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msBuscarAgenteService);
        BuscarAgenteReq req = gson.fromJson(jsonReq, BuscarAgenteReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("sucursal", Objects.toString(req.getSucursal(), ""));
        queryParams.put("empresa", Objects.toString(req.getEmpresa(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO consultaCanales(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msConsultaCanalesService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);

        if (respuesta.getCodigo() == 0) {
            //Obtenemos la respuesta del servicio guardada en el campo data, la cual es un objeto RespuestaDTO
            RespuestaDTO respFinal = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
            return respFinal;
        }

        return respuesta;
    }

    public RespuestaDTO consultaCanalCuenta(String req) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msConsultaCanalesCuentaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, req, getAuthorizationHeader(), MediaType.APPLICATION_JSON);

        if (respuesta.getCodigo() == 0) {
            //Obtenemos la respuesta del servicio guardada en el campo data, la cual es un objeto RespuestaDTO
            RespuestaDTO respFinal = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
            return respFinal;
        }

        return respuesta;
    }

    public RespuestaDTO live() {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreManagementHost, msVFCoreManagementPort, msLiveService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, null, getAuthorizationHeader());

        if (respuesta.getCodigo() == 0) {
            //Obtenemos la respuesta del servicio guardada en el campo data, la cual es un objeto RespuestaDTO
            RespuestaDTO respFinal = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
            return respFinal;
        }

        return respuesta;
    }
}