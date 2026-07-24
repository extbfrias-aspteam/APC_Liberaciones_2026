package mx.net.asp.asp_pago_management.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.request.ValidaCurpNubReq;
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
public class WSValidaCurpNubarium {

	@Value("${ws.consulta.nubariuem.host}")
	private String wsConsultaNubariumHost;
	@Value("${ws.valida.curp.nub.service}")
	private String validaCurpNubService;
	@Value("${nubarium.curp.user}")
	private String nubariumCurpUser;
	@Value("${nubarium.curp.pass}")
	private String nubariumCurpPass;
	private final InvokeRestServiceUtil invokeRestServiceUtil;
	private final Gson gson;

	private String getAuthorizationHeader() {
		return "Basic " + Base64.getEncoder().encodeToString(
				(nubariumCurpUser + ":" + nubariumCurpPass).getBytes()
		);
	}

	public RespuestaDTO validaCurpNub(String curp) {
		// Construir el cuerpo de la solicitud
		RespuestaDTO respuesta;
		ValidaCurpNubReq req = new ValidaCurpNubReq();
		req.setCurp(curp);
		String url = InvokeRestServiceUtil.getURLRest(wsConsultaNubariumHost, "", validaCurpNubService);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", getAuthorizationHeader());
		// Configurar la solicitud con los encabezados
		HttpEntity<ValidaCurpNubReq> requestEntity = new HttpEntity<>(req, headers);
		respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() != 0 && respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_WS,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
        }

		return respuesta;
	}
}

