package mx.net.asp.vf_core_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.request.ObtenerTokenReq;
import mx.net.asp.vf_core_api.request.ProcesaReq;
import mx.net.asp.vf_core_api.request.UsuarioDatos;
import mx.net.asp.vf_core_api.response.RespuestaCOF;
import mx.net.asp.vf_core_api.handlers.procesa.ProcesaHandler;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import mx.net.asp.vf_core_api.ws.asp.WSVFCoreVentanilla;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class VFCoreVentanillaService {

    private final WSVFCoreVentanilla wsVFCoreVentanilla;

    private final List<ProcesaHandler> handlers;
    private final ErrorHandler errorHandler;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    public ServiceResponse procesa(ProcesaReq reqIn) {
        RespuestaDTO respuesta = new RespuestaDTO();
        WsVfReq req = new WsVfReq();
        try {
            Map<String, Object> mapa = objectMapper.readValue(reqIn.getMap(),
                    new TypeReference<>() {
                    });

            UsuarioDatos ud = reqIn.getUsuarioDatos();

            req.setUsuarioAdmin(ud.getUsuario());
            req.setPasswordAdmin(ud.getPassword());
            req.setTokenAdmin(ud.getToken());
            req.setTransaccionId((String) mapa.get("transaccionIdAut"));
            req.setTipoOperacion(reqIn.getProceso());

            String proceso = reqIn.getProceso();

            log.info("--- INICIA TRANSACCION ---");
            log.info("Usuario: {}", req.getUsuarioAdmin());
            log.info("Operacion: {}", req.getTipoOperacion());
            log.info("Referencia: {}", req.getReferencia());
            log.info("Id Transaccion: {}", req.getTransaccionId());
            log.info("Tipo de Dispositivo: {}", req.getTipoDispositivo());


            handlers.stream()
                    .filter(h -> h.canHandle(proceso))
                    .findFirst()
                    .ifPresent(h -> h.handle(mapa, req));

            // === aquí se llama el VFCoreVentanilla ===
            respuesta = wsVFCoreVentanilla.procesaWsVf(req);

            RespuestaCOF respuestaCOF = buildRespuestaCOF(respuesta);
            return RespuestaUtils.evaluaRespuesta(respuesta, gson.toJson(respuestaCOF), true);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.info("--- FINALIZA TRANSACCION ---");
        }
    }

    public ServiceResponse obtenerToken(ObtenerTokenReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.obtenerToken(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse bloqueaComisionistaEmpleado(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.bloqueaComisionistaEmpleado(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarComisionistaReferencia(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.buscarComisionistaReferencia(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultarRetenciones(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.consultarRetenciones(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaSaldoAhorro(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.consultaSaldoAhorro(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaSaldoCredito(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.consultaSaldoCredito(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse depositoAhorro(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.depositoAhorro(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse enviarNotificaciones(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.enviarNotificaciones(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse retiroAhorro(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.retiroAhorro(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse pagoCredito(String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreVentanilla.pagoCredito(req);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private RespuestaCOF buildRespuestaCOF(RespuestaDTO resp) {
        RespuestaCOF respuestaCOF = new RespuestaCOF();
        respuestaCOF.setResultado(resp.getCodigo());
        respuestaCOF.setMsgHost(resp.getMensaje());
        respuestaCOF.setRespuesta(resp.getData());
        respuestaCOF.setReferencia("000000000000");
        respuestaCOF.setResultadoCatel(-1);
        respuestaCOF.setNumAutorizacion("000000");
        return respuestaCOF;
    }
}