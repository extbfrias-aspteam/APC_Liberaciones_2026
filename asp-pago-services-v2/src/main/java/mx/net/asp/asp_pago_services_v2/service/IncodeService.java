package mx.net.asp.asp_pago_services_v2.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dao.AhorroDAO;
import mx.net.asp.asp_pago_services_v2.dao.SeguridadDAO;
import mx.net.asp.asp_pago_services_v2.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_services_v2.requests.ActualizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeManualReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.GuardarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
import mx.net.asp.asp_pago_services_v2.utilerias.CorreoUtils;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_services_v2.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_services_v2.utilerias.errores.ErroresFinalizaSesionIncode;
import mx.net.asp.asp_pago_services_v2.utilerias.errores.ErroresFinalizaSesionIncodeManual;
import mx.net.asp.asp_pago_services_v2.ws.asp.MSEnviarEmail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
@Log4j2
@RequiredArgsConstructor
public class IncodeService {

    private final SeguridadDAO seguridadDAO;
    private final AhorroDAO ahorroDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final Gson gson;
    private final MSEnviarEmail msEnviarEmail;
    private final CorreoUtils correoUtils;
    private final CuentaService cuentaService;
    private final ErrorHandler errorHandler;
    private final Environment env;

    public RespuestaDTO obtenerSesionIncode(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = seguridadDAO.obtenerSesionIncode(cuentaah, true, "");
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO guardarSesionIncode(GuardarSesionIncodeReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Validamos que no haya una sesion de incode activa
            respuesta = seguridadDAO.obtenerSesionIncode(req.getCuentaah(), true, "");
            //Si el codigo es 0 significa que si encontró un registro en la tabla
            //Caso contrario validamos que el codigo de error sea el -100 y no un error de Excepcion no controlada
            //Caso contrario a los 2 anteriores, se returnara la respuesta que venga desde el SeguridadDAO
            if (respuesta.getCodigo() == 0) {
                log.info("La cuenta {} ya tiene una sesion de Incode activa", req.getCuentaah());
                respuesta.setCodigo(-101);
                respuesta.setMensaje("La cuenta ya tiene una sesion de Incode activa");
                respuesta.setData(null);
            } else if (respuesta.getCodigo() == -100) {
                //Validamos que el modulo exista
                respuesta = seguridadDAO.obtenerModuloIncodeById(req.getModuloId());
                if (respuesta.getCodigo() == 0) {
                    //Guardamos la sesion de Incode
                    respuesta = seguridadDAO.guardarSesionIncode(req);
                }else if (respuesta.getCodigo() == -100)
                    respuesta.setCodigo(-102);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO actualizarSesionIncode(ActualizarSesionIncodeReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Validamos que haya una sesion de incode activa
            respuesta = seguridadDAO.obtenerSesionIncode(req.getCuentaah(), true, req.getSesionIncodeId());
            if (respuesta.getCodigo() == 0) {
                //Validamos que el modulo exista
                respuesta = seguridadDAO.obtenerModuloIncodeById(req.getModuloId());
                if (respuesta.getCodigo() == 0) {
                    //Acualizamos la sesión de Incode
                    respuesta = seguridadDAO.actualizarSesionIncode(req);
                    if (respuesta.getCodigo() == -100)
                        respuesta.setCodigo(-102);
                } else if (respuesta.getCodigo() == -100)
                    respuesta.setCodigo(-101);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO finalizarSesionIncode(FinalizarSesionIncodeReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String asunto = "";
        String bodyCorreo = "";
        try {
            String estatus = req.getEstatus();
            //Validamos que haya una sesion de incode activa
            respuesta = seguridadDAO.obtenerSesionIncode(req.getCuentaah(), true, req.getSesionIncodeId());
            if (respuesta.getCodigo() == 0) {

                respuesta = obtenerSolicitanteBySesionId(req.getSesionIncodeId(), req.getCuentaah());
                if (respuesta.getCodigo() != 0 && !"OK".equalsIgnoreCase(estatus)) {
                    return RespuestaUtils.asignarError(respuesta,
                                ErroresFinalizaSesionIncode.INFORMACION_SOLICITANTE_NOT_FOUND,
                                ErroresFinalizaSesionIncode.descError.get(ErroresFinalizaSesionIncode.INFORMACION_SOLICITANTE_NOT_FOUND));
                }

                SolicitanteOBJ solicitanteOBJ = gson.fromJson(respuesta.getData(), SolicitanteOBJ.class);

                if ("OK".equalsIgnoreCase(estatus) || "FAIL".equalsIgnoreCase(estatus)) {
                    respuesta = seguridadDAO.finalizarSesionIncode(req, 9);
    
                    if (respuesta.getCodigo() != 0) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresFinalizaSesionIncode.ERROR_FINALIZAR_SESION,
                                ErroresFinalizaSesionIncode.descError.get(ErroresFinalizaSesionIncode.ERROR_FINALIZAR_SESION));
                    }
    
                    if ("FAIL".equalsIgnoreCase(estatus)) {
                        log.info("Estatus FAIL, sesión rechazada.");
                        asunto = "Alerta - cliente no aprobado para subir de nivel su Cuenta fácil.";
                        bodyCorreo = correoUtils.generarCorreoRechazoSubidaNivel(solicitanteOBJ.getNombre());
                        enviarEmail(asunto, bodyCorreo);
                    }
    
                } else if ("MANUAL".equalsIgnoreCase(req.getEstatus())) {
                    log.info("Estatus MANUAL, solicitud pendiente por evaluar.");
                    asunto = "Alerta - revisión manual pendiente en Incode ";
                    respuesta = seguridadDAO.actualizarEstatusSesionIncode(req, 9);
                    if(respuesta.getCodigo() != 0){
                        return respuesta;
                    }
                    bodyCorreo = correoUtils.generarCorreoRevisionManual(solicitanteOBJ.getNombre());
                    enviarEmail(asunto, bodyCorreo);

                } else {
                    log.warn("Estatus no reconocido: '{}'", estatus);
                    return RespuestaUtils.asignarError(respuesta,
                                ErroresFinalizaSesionIncode.ESTATUS_INVALIDO,
                                ErroresFinalizaSesionIncode.descError.get(ErroresFinalizaSesionIncode.ESTATUS_INVALIDO));
                }
                
                respuesta.setCodigo(0);
                respuesta.setMensaje("Sesión finalizada con éxito.");
                respuesta.setData(null);
                
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO finalizarSesionIncodeManual(FinalizarSesionIncodeManualReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String bodyCorreo = "";
        try {

            respuesta = obtenerSolicitanteBySesionId(req.getInterviewId(), "");
            if(respuesta.getCodigo() != 0){
                return respuesta;
            }

            SolicitanteOBJ solicitanteOBJ = gson.fromJson(respuesta.getData(), SolicitanteOBJ.class);

            respuesta = seguridadDAO.finalizarSesionIncodeManual(req, 9);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                            ErroresFinalizaSesionIncodeManual.ERROR_FINALIZAR_SESION_MANUAL,
                            ErroresFinalizaSesionIncodeManual.descError.get(ErroresFinalizaSesionIncodeManual.ERROR_FINALIZAR_SESION_MANUAL));
            }

            if ("MANUAL_REVIEW_APPROVED".equalsIgnoreCase(req.getOnboardingStatus())) {
                log.info("Estatus MANUAL_REVIEW_APPROVED, se procede a enviar correo al usuario y a subir de nivel");
                String asunto = "¡Tu cuenta Fácil Avanzada ha sido aprobada!";
                bodyCorreo = correoUtils.generarCorreoAprobacionSubidaNivel(solicitanteOBJ.getNombre());

                log.info("Subiendo nivel de la cuenta");
                SubirNivelCuentaRemotaReq subirNivelCuentaRemotaReq = new SubirNivelCuentaRemotaReq();
                subirNivelCuentaRemotaReq.setCuentaah(solicitanteOBJ.getCuenta());
                cuentaService.subirNivelCuentaRemota(subirNivelCuentaRemotaReq);
                
                if (!solicitanteOBJ.getCorreo().isEmpty()) {
                    log.info("Enviando correo a {}", solicitanteOBJ.getCorreo());
                    msEnviarEmail.enviarCorreo(solicitanteOBJ.getCorreo(), asunto, bodyCorreo);
                }
            } else if ("MANUAL_REVIEW_REJECT".equalsIgnoreCase(req.getOnboardingStatus())) {
                log.info("Estatus MANUAL_REVIEW_REJECT, se procede a enviar correo a la lista de correos");
                String asunto = "Alerta - cliente no aprobado para subir de nivel su Cuenta fácil.";
                bodyCorreo = correoUtils.generarCorreoRechazoSubidaNivel(solicitanteOBJ.getNombre());
                enviarEmail(asunto, bodyCorreo);
            }

            respuesta.setCodigo(0);
            respuesta.setMensaje("Sesión finalizada con éxito.");
            respuesta.setData(null);
            
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public void enviarEmail (String asunto, String bodyCorreo) {
        List<String> correos = seguridadDAO.obtenerCorreos();

        if(!correos.isEmpty()){
            log.info("Se encontraron correos activos, se procede a enviar los correos");
            correos.forEach(correo -> {
                try {
                    msEnviarEmail.enviarCorreo(correo, asunto, bodyCorreo);
                } catch (Exception e) {
                    log.warn("Error al enviar correo a {}: {}", correo, e.getMessage());
                }
            });
        }
    }

    public RespuestaDTO obtenerSolicitanteBySesionId(String sesionId, String cuentaah){
        RespuestaDTO respuesta = new RespuestaDTO();
        String cuenta = "";
        try {

            if (cuentaah == null || cuentaah.trim().isEmpty()) {
                cuenta = seguridadDAO.obtenerCuentaBySesionId(sesionId);
                log.info("Cuenta obtenida desde obtenerCuentaBySesionId: {}", cuenta);

                if (cuenta.equals("")) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFinalizaSesionIncodeManual.CUENTA_NOT_FOUND,
                            ErroresFinalizaSesionIncodeManual.descError.get(ErroresFinalizaSesionIncodeManual.CUENTA_NOT_FOUND));
                }
            } else {
                cuenta = cuentaah;
                log.info("Cuenta proporcionada como parámetro: {}", cuenta);
            }
    
            // Buscar la persona Id
            String personaId = ahorroDAO.obtenerPersonaIdByCuenta(cuenta);
            if(personaId.equals("")){
                return RespuestaUtils.asignarError(respuesta,
                            ErroresFinalizaSesionIncodeManual.PERSONA_ID_NOT_FOUND,
                            ErroresFinalizaSesionIncodeManual.descError.get(ErroresFinalizaSesionIncodeManual.PERSONA_ID_NOT_FOUND));
            }
    
            // Buscar el correo por cuenta
            respuesta = solicitanteDAO.obtenerInfoSolicitanteById(personaId);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                            ErroresFinalizaSesionIncodeManual.INFORMACION_SOLICITANTE_NOT_FOUND,
                            ErroresFinalizaSesionIncodeManual.descError.get(ErroresFinalizaSesionIncodeManual.INFORMACION_SOLICITANTE_NOT_FOUND));
            }
    
            SolicitanteOBJ solicitanteOBJ = gson.fromJson(respuesta.getData(), SolicitanteOBJ.class);
            solicitanteOBJ.setCuenta(cuenta);
    
            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(gson.toJson(solicitanteOBJ));
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }
}
