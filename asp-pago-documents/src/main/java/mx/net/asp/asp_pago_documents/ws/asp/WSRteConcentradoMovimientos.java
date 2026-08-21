package mx.net.asp.asp_pago_documents.ws.asp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.EntradaConsultarMovimientosConcentradoOBJ;
import mx.net.asp.asp_pago_documents.model.ReporteMovimientosAspPagoOBJ;
import mx.net.asp.asp_pago_documents.model.TransaccionCuentaOBJ;
import mx.net.asp.asp_pago_documents.utilerias.InvokeRestServiceUtil;

import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresEstadoMov;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSRteConcentradoMovimientos {

    @Value("${ws.Servicio.base.admin.plasticos.host}")
    private String msAdminPlasticosHost;
    @Value("${ws.Servicio.base.admin.plasticos.port}")
    private String msAdminPlasticosPort;
    @Value("${ws.Servicio.base.admin.plasticos.reporte.concentrado.movimientos}")
    private String reporteConcentradoMovimientos;
    @Value("${ws.adminPlasticos.user}")
    private String msAdminPlasticosUser;
    @Value("${ws.adminPlasticos.pass}")
    private String msAdminPlasticosPass;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msAdminPlasticosUser + ":" + msAdminPlasticosPass).getBytes()
        );
    }

    public RespuestaDTO obtenerEdoMovimientosFromAdminPlasticos(TransaccionCuentaOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", getAuthorizationHeader());
        // Construir el cuerpo de la solicitud
        EntradaConsultarMovimientosConcentradoOBJ entradaConsultarMovimientosConcentradoOBJ = new EntradaConsultarMovimientosConcentradoOBJ();
        entradaConsultarMovimientosConcentradoOBJ.setFechaInicial(String.valueOf(req.getFechaInicio()));
        entradaConsultarMovimientosConcentradoOBJ.setFechaFinal(String.valueOf(req.getFechaFin()));
        entradaConsultarMovimientosConcentradoOBJ.setCuentaASP(String.valueOf(req.getCuenta()));

        String url = InvokeRestServiceUtil.getURLRest(msAdminPlasticosHost, msAdminPlasticosPort, reporteConcentradoMovimientos);
        // Configurar la solicitud con los encabezados
        HttpEntity<EntradaConsultarMovimientosConcentradoOBJ> requestEntity = new HttpEntity<>(entradaConsultarMovimientosConcentradoOBJ, headers);

        try {
            respuesta = invokeRestServiceUtil.enviarSolicitud(url, requestEntity, HttpMethod.POST, null);

            if (respuesta.getCodigo() == 0) {
                //deserealizamos la respuesta del servicio guardada en el campo data
                respuesta = gson.fromJson(respuesta.getData(), RespuestaDTO.class);
                if (respuesta.getCodigo() == 0) {

                    if (!StringUtils.isBlank(respuesta.getData())) {
                        Type listMovimientos = new TypeToken<List<ReporteMovimientosAspPagoOBJ>>() {
                        }.getType();

                        List<ReporteMovimientosAspPagoOBJ> movimientosAspPago = gson.fromJson(respuesta.getData(), listMovimientos);
                        respuesta.setCodigo(0);
                        respuesta.setMensaje("OK");
                        respuesta.setData(gson.toJson(movimientosAspPago));
                    } else {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresEstadoMov.MOVIMIENTOS_NOT_FOUND,
                                ErroresEstadoMov.descError.get(ErroresEstadoMov.MOVIMIENTOS_NOT_FOUND));
                    }
                }
            } else {
                if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
                    RespuestaUtils.asignarError(respuesta,
                            ErroresGenerales.ERROR_WS,
                            ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }
}

