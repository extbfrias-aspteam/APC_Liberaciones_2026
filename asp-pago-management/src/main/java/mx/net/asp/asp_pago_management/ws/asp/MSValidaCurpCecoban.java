package mx.net.asp.asp_pago_management.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.ValidaCurpCecobanReq;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;

import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
@Log4j2
@RequiredArgsConstructor
public class MSValidaCurpCecoban {
    
    @Value("${ws.consulta.cecoban.host}")
	private String wsConsultaCecobanHost;
	@Value("${ws.valida.curp.cecoban.service}")
	private String validaCurpCecobanService;
	@Value("${nubarium.curp.user}")
	private String nubariumCurpUser;
	@Value("${nubarium.curp.pass}")
	private String nubariumCurpPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    public RespuestaDTO validaCurpCecoban(ValidaCurpCecobanReq req, String token) {
        // Construir el cuerpo de la solicitud
        RespuestaDTO respuesta;
        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("ApellidoPaterno", req.getApellidoPaterno().toUpperCase());
        requestBodyJson.addProperty("Nombre", req.getNombre().toUpperCase());
        requestBodyJson.addProperty("Sexo", req.getSexo().toUpperCase());
        requestBodyJson.addProperty("FechaNacimiento", req.getFechaNacimiento());
        requestBodyJson.addProperty("ClaveEntidadFederativa", req.getEntidadFederativaNacimiento().toUpperCase());

        // Solo agregar ApellidoMaterno si tiene valor
        if (req.getApellidoMaterno() != null && !req.getApellidoMaterno().trim().isEmpty()) {
            requestBodyJson.addProperty("ApellidoMaterno", req.getApellidoMaterno().toUpperCase());
        }

        log.info("Request para consultar curp en el servicio de Cecoban: {}", gson.toJson(requestBodyJson));

        String tokenBearer = "Bearer " + token;

        String url = InvokeRestServiceUtil.getURLRest(wsConsultaCecobanHost, "", validaCurpCecobanService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", tokenBearer);
        // Configurar la solicitud con los encabezados
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);
        respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() != 0 && respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }
        return respuesta;
    }
}
