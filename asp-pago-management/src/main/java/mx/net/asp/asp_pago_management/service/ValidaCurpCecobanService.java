package mx.net.asp.asp_pago_management.service;

import lombok.RequiredArgsConstructor;
import mx.net.asp.asp_pago_management.dao.ProcreaDAO;
import mx.net.asp.asp_pago_management.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.PlasticosDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.ValidaCurpCecobanReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresValidaCurpCecoban;
import mx.net.asp.asp_pago_management.ws.asp.MSObtenerToken;
import mx.net.asp.asp_pago_management.ws.asp.MSValidaCurpCecoban;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidaCurpCecobanService {
    private final PlasticosDAO plasticosDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final ProcreaDAO procreaDAO;

    private final MSObtenerToken msObtenerToken;
    private final MSValidaCurpCecoban msValidaCurpCecoban;
    private final GeneraCurpService generaCurpService;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${concepto.consulta.cecoban}")
    private String conceptoConsultaCecoban;
    @Value("${producto.id.mi.ahorro}")
    private Integer prodcutoIdMiAhorro;
    @Value("${valida.curp.flag}")
    private Boolean validaCurpFlag;

    public RespuestaDTO validaCurpCecoban(ValidaCurpCecobanReq req) {

        RespuestaDTO respuesta = new RespuestaDTO();

        try {

            //valida cliente existe con cuenta
            if (!Boolean.TRUE.equals(validaCurpFlag)){
                log.info("*** VALIDACION DE CURP DESHABILITADA POR CONFIGURACION ***");
                return RespuestaUtils.respuestaExito(respuesta, 0, "Curp validada exitosamente");
            }

            ConfiguracionASPPagoOBJ confAspPagoOBJ = plasticosDAO.obtenerValorConfAspPagoByConcepto(conceptoConsultaCecoban);
            log.info("confAspPagoOBJ: {}", confAspPagoOBJ);

            if (confAspPagoOBJ == null) {
                log.warn("No se logro encontrar el concepto de consulta a Cecoban");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP,
                        ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP));
            }

            if (confAspPagoOBJ.getValor().equals("ENABLED")) {
                log.info("<--- COMPARAR CURP'S HABILITADO --->");
                RespuestaDTO respuestaGeneraCurp = generaCurpService.generaCurp(req);
                log.info("respuesta del generaCurpService: {}", respuestaGeneraCurp);
                if (respuestaGeneraCurp.getCodigo() == 0) {

                    String curpGenerada = respuestaGeneraCurp.getData();
                    String curpRecibida = req.getCurp();

                    Boolean consultarCecoban = curpGenerada.equals(curpRecibida.substring(0, 16));
                    if (!consultarCecoban) {
                        log.info("La CURP generada ({}) no coincide con la CURP recibida ({})", curpGenerada, curpRecibida);
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresValidaCurpCecoban.CURP_NO_COINCIDE,
                                ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.CURP_NO_COINCIDE));
                    }

                    log.info("La CURP generada ({}) coincide con la CURP recibida ({})", curpGenerada, curpRecibida);

                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERROR_GENERA_CURP,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_GENERA_CURP));
                }
            } else {
                log.info("<--- COMPARAR CURP'S DESHABILITADO --->");
            }

            //valida cliente existe con cuenta
            if (validaClienteExistentePorCurp(req.getCurp())){
                return RespuestaUtils.respuestaExito(respuesta, 0, "Curp validada exitosamente");
            }

            RespuestaDTO respuestaObtenerToken = generaTokenAuth();

            if (respuestaObtenerToken.getCodigo() != 0) {
                return respuestaObtenerToken;
            }

            TokenResponseOBJ tokenResponseOBJ = gson.fromJson(respuestaObtenerToken.getData(), TokenResponseOBJ.class);

            RespuestaDTO respuestaValidaCurp = msValidaCurpCecoban.validaCurpCecoban(req, tokenResponseOBJ.getAccess_token());
            log.info("Respuesta del servicio de validación de curp: {}", respuestaValidaCurp);
            if (respuestaValidaCurp.getCodigo() != 0) {
                if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERROR_TEMPORAL,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_TEMPORAL));
                }

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP,
                        ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP));
            }

            JsonElement jsonElement = gson.fromJson(respuestaValidaCurp.getData(), JsonElement.class);

            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject rootObject = jsonElement.getAsJsonObject();

                // Verificar si existe el campo "detail"
                if (rootObject.has("detail")) {
                    JsonElement detailElement = rootObject.get("detail");

                    // Validar si es array
                    if (detailElement.isJsonArray()) {
                        JsonArray detailArray = detailElement.getAsJsonArray();
                        StringBuilder errorMsg = new StringBuilder("Errores del servicio Cecoban: ");

                        for (JsonElement element : detailArray) {
                            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                                errorMsg.append(element.getAsString()).append("; ");
                            }
                        }

                        return RespuestaUtils.asignarError(respuesta,
                                ErroresValidaCurpCecoban.ERROR_GENERICO_CECOBAN,
                                ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_GENERICO_CECOBAN));
                    }

                    // Seguridad adicional: si no fuera array (aunque no debería ocurrir)
                    log.info("Error del servicio Cecoban: el campo 'detail' tiene un formato inválido o inesperado.");
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_VALIDAR_CURP));
                }

                // Respuesta exitosa
                CurpRespuestaExitosaCecobanOBJ curpRespuestaExitosaCecobanOBJ = gson.fromJson(respuestaValidaCurp.getData(), CurpRespuestaExitosaCecobanOBJ.class);
                if (curpRespuestaExitosaCecobanOBJ.getCurp() != null) {
                    JsonObject curpJson = new JsonObject();
                    curpJson.addProperty("curp", curpRespuestaExitosaCecobanOBJ.getCurp());
                    if (req.getCurp().equalsIgnoreCase(curpRespuestaExitosaCecobanOBJ.getCurp())) {
                        RespuestaUtils.respuestaExito(respuesta, 0, "Curp validada exitosamente", gson.toJson(curpJson));
                    } else {
                        log.info("Las curps no son iguales, termina el proceso...");
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresValidaCurpCecoban.CURPS_NOT_MATCH,
                                ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.CURPS_NOT_MATCH));
                    }
                } else {
                    String mensaje = curpRespuestaExitosaCecobanOBJ.getResultado() + " | " + curpRespuestaExitosaCecobanOBJ.getMensajeDeRenapo();
                    log.info("Respuesta no exitosa del servicio de cecoban, descripción: {}" + mensaje);
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERROR_GENERICO_CECOBAN,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_GENERICO_CECOBAN));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO generaTokenAuth() {

        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            respuesta = msObtenerToken.obtenerToken();
            log.info("Respuesta del servicio de autenticación: {}", gson.toJson(respuesta));

            if (respuesta.getData() == null || respuesta.getData().trim().isEmpty()) {
                log.warn("La respuesta del servicio de autenticación no contiene datos.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaCurpCecoban.ERRORES_AUTENTICACION,
                        ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERRORES_AUTENTICACION));
            }

            JsonObject jsonResponse = JsonParser.parseString(respuesta.getData()).getAsJsonObject();

            if (jsonResponse.has("access_token")) {
                // Deserializa como respuesta correcta
                log.info("Token generado correctamente");
                TokenResponseOBJ tokenResponse = gson.fromJson(respuesta.getData(), TokenResponseOBJ.class);
                RespuestaUtils.respuestaExito(respuesta, 0, "Token generado correctamente", gson.toJson(tokenResponse));

            } else if (jsonResponse.has("detail")) {
                log.info("Respuesta del servicio de autenticación contiene el atributo de error detail");
                JsonElement detailElement = jsonResponse.get("detail");

                if (detailElement.isJsonArray()) {
                    // Deserializa error array
                    ErrorArrayResponseOBJ errorArray = gson.fromJson(respuesta.getData(), ErrorArrayResponseOBJ.class);
                    String mensaje = errorArray.getDetail().isEmpty() ? "Error: " : errorArray.getDetail().get(0);
                    log.warn("Error durante el proceso de autenticación: {}", mensaje);
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERRORES_AUTENTICACION,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERRORES_AUTENTICACION));
                } else if (detailElement.isJsonPrimitive()) {
                    // Deserializa error string
                    ErrorStringResponseOBJ errorString = gson.fromJson(respuesta.getData(), ErrorStringResponseOBJ.class);
                    log.warn("Error durante el proceso de autenticación: {}", errorString.getDetail());
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidaCurpCecoban.ERROR_AUTENTICACION,
                            ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_AUTENTICACION));
                }
            } else {
                log.warn("Error desconocido durante el proceso de autenticación.");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaCurpCecoban.ERROR_AUTENTICACION_DESCONOCIDO,
                        ErroresValidaCurpCecoban.descError.get(ErroresValidaCurpCecoban.ERROR_AUTENTICACION_DESCONOCIDO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    private boolean validaClienteExistentePorCurp(String curp) {
        RespuestaDTO respuesta;
        //Se valida si el curp enviado existe en BD
        SolicitanteOBJ solicitanteOBJ = solicitanteDAO.buscarSolicitanteByCurp(curp);
        if (solicitanteOBJ == null)
            return false;
        respuesta = obtenerCuenta(solicitanteOBJ.getNumero());
        if (respuesta.getCodigo() != 0)
            return false;

        return true;
    }

    public RespuestaDTO obtenerCuenta(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<CuentaOBJ> cuentasList;
        try {
            cuentasList = procreaDAO.obtenerListadoCuentasProcreaByPersonaId(personaId, prodcutoIdMiAhorro);

            if (cuentasList != null && !cuentasList.isEmpty()) {
                CuentaOBJ cuentaOBJ = cuentasList.getFirst();
                log.info("### El cliente {} tiene cuenta de producto {}", personaId, prodcutoIdMiAhorro);
                return RespuestaUtils.respuestaExito(respuesta, gson.toJson(cuentaOBJ));
            } else {
                log.info("### El cliente {} NO tiene cuenta de producto {}", personaId, prodcutoIdMiAhorro);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No tiene cuenta");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta, ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

}
