package mx.net.asp.asp_pago_access.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.CoDiDAO;
import mx.net.asp.asp_pago_access.dao.SeguridadDAO;
import mx.net.asp.asp_pago_access.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.*;
import mx.net.asp.asp_pago_access.request.*;
import mx.net.asp.asp_pago_access.response.PasswordResetCambioResp;
import mx.net.asp.asp_pago_access.utilerias.*;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresResetPassword;
import mx.net.asp.asp_pago_access.ws.asp.MSEnviarEmail;
import mx.net.asp.asp_pago_access.ws.asp.MSRevocaJwtTokenCliente;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetAuxService passwordResetAuxService;
    private final RegistroCoDiService registroCoDiService;

    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final SeguridadDAO seguridadDAO;

    private final MSEnviarEmail msEnviarEmail;
    private final MSRevocaJwtTokenCliente msRevocaJwtTokenCliente;

    private final Environment env;
    private final CifradoUtil cifUtil;
    private final CorreoUtils correoUtils;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${usuario.sistemas}")
    private Integer usuarioSistemas;
    @Value("${cambio.password.clv}")
    private String cveCambioPass;
    @Value("${obs.cambio.pass.asp.pago}")
    private String obsCambioPassAspPago;
    @Value("${cve.tipo.envio.otp}")
    private String cveTipoEnvioOtp;
    @Value("${min.otp.res.pwd}")
    private Integer tiempoOTP;

    public RespuestaDTO passwordResetSolicitudInicial(SolicitudPasswordResetReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Long idProcesoBit = 0L;
        try {
            //registra bitacora cambio pass
            BitacoraCambioPasswordOBJ bitacoraOBJ = getBitacoraCambioPasswordOBJ(req.getTelefono(), "TELEFONO", req.getDevice_id(),
                    "", "SOLICITUD CAMBIO PASSWORD", "");
            idProcesoBit = seguridadDAO.registraBitacoraCambioPassword(bitacoraOBJ);

            passwordResetAuxService.procesaPasswordResetSolicitudAsync(req, idProcesoBit);
            //retornar respuesta OK generico
            return RespuestaUtils.respuestaExito(respuesta, 0, passwordResetAuxService.mensajeConfirmacionOtp(cveTipoEnvioOtp));
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return respuesta;
    }

    public RespuestaDTO passwordResetValidaOTPInicial(ValidaOTPPasswordResetReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Long idProcesoBit = 0L;
        try {
            //registra bitacora cambio pass
            BitacoraCambioPasswordOBJ bitacoraOBJ = getBitacoraCambioPasswordOBJ(req.getTelefono(), "TELEFONO", req.getDevice_id(),
                    "", "VALIDACION OTP", "");
            idProcesoBit = seguridadDAO.registraBitacoraCambioPassword(bitacoraOBJ);


            respuesta = passwordResetValidaOTP(req);
        } catch (Exception e) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            errorHandler.handleException(e);
        } finally {
            if (idProcesoBit != null && idProcesoBit > 0) {
                String resultado = respuesta.getCodigo() == 0 ? "OK" : "ERROR";
                seguridadDAO.actualizaBitacoraCambioPassword(idProcesoBit,
                        resultado,
                        String.format("%s | codigo otp: %s", respuesta.getMensaje(), req.getOtp()));
            }
        }
        return respuesta;
    }

    private RespuestaDTO passwordResetValidaOTP(ValidaOTPPasswordResetReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.SOLICITANTE_NOT_FOUND_P,
                        ErroresResetPassword.getMensajeError(ErroresResetPassword.SOLICITANTE_NOT_FOUND_P, req.getTelefono()));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = registroCoDiService.validaRegistroCoDiBase(regCoDiOBJ);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            //control diario
            respuesta = passwordResetAuxService.validarControlPasswordReset(solicitanteOBJ.getNumero(), req.getDevice_id(), "VALIDATION_OTP");
            if (respuesta.getCodigo() != 0)
                return respuesta;

            //obtener otp
            String otpHash = cifUtil.encriptaInformacionB64(
                    cifUtil.generaKeySource(env.getProperty("aes.key.password", String.class)),
                    req.getOtp()
            );

            //validamos que exista el otp enviado
            OTPPwdOBJ otpPwdOBJ = seguridadDAO.obtenerOTPResetPassword(solicitanteOBJ.getNumero(), req.getDevice_id(), otpHash);
            if (otpPwdOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.OTP_NO_VALIDO,
                        ErroresResetPassword.descError.get(ErroresResetPassword.OTP_NO_VALIDO));
            }

            LocalDateTime fechaActual = seguridadDAO.obtenerHoraBd();
            if (fechaActual.isBefore(otpPwdOBJ.getFecha_expira())) {
                //generar y guardar sesion reset hash
                String resetSession = passwordResetAuxService.generarResetSession();
                String hashResetSession = cifUtil.sha512(resetSession);

                LocalDateTime fechaGenera = seguridadDAO.obtenerHoraBd();
                LocalDateTime fechaExpira = fechaGenera.plusMinutes(tiempoOTP);

                ResetSessionOBJ resetSessionOBJ = passwordResetAuxService.construirRessetSession(
                        regCoDiOBJ.getSolicitanteId(), req.getDevice_id(), hashResetSession, fechaGenera, fechaExpira);
                seguridadDAO.guardarResetSessionCambioPwd(resetSessionOBJ);

                //desactivar OTP
                seguridadDAO.desactivarOTPResetPassword(otpPwdOBJ.getId());

                PasswordResetCambioResp response = new PasswordResetCambioResp();
                response.setResset_sesion(resetSession);
                return RespuestaUtils.respuestaExito(respuesta, gson.toJson(response));
            } else {
                //desactivar OTP por expiracion
                seguridadDAO.desactivarOTPResetPassword(otpPwdOBJ.getId());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.OTP_EXPIRADO,
                        ErroresResetPassword.descError.get(ErroresResetPassword.OTP_EXPIRADO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO passwordResetCambioPwdInicial(CambioPasswordResetReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Long idProcesoBit = 0L;
        try {
            //registra bitacora cambio pass
            BitacoraCambioPasswordOBJ bitacoraOBJ = getBitacoraCambioPasswordOBJ(req.getResetSessionToken(), "RESET SESSION ID", req.getDevice_id(),
                    "", "ACTUALIZACION PASSWORD", "");
            idProcesoBit = seguridadDAO.registraBitacoraCambioPassword(bitacoraOBJ);

            respuesta = passwordResetCambioPwd(req);
        } catch (Exception e) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            errorHandler.handleException(e);
        } finally {
            if (idProcesoBit != null && idProcesoBit > 0) {
                String resultado = respuesta.getCodigo() == 0 ? "OK" : "ERROR";
                if (respuesta.getData() == null) {
                    seguridadDAO.actualizaBitacoraCambioPassword(idProcesoBit,
                            resultado,
                            String.format("%s | reset-session-id: %s", respuesta.getMensaje(), req.getResetSessionToken()));
                } else {
                    seguridadDAO.actualizaBitacoraCambioPasswordValorRef(idProcesoBit,
                            respuesta.getData(), "TELEFONO",
                            resultado,
                            String.format("%s | reset-session-id: %s", respuesta.getMensaje(), req.getResetSessionToken()));
                }
            }

            if (respuesta.getCodigo() == ErroresResetPassword.ERROR_CIF) {
                RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            } else
                respuesta.setData(null);
        }
        return respuesta;
    }

    private RespuestaDTO passwordResetCambioPwd(CambioPasswordResetReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //validamos que exista el reset sesion
            String hashResetSession = cifUtil.sha512(req.getResetSessionToken());
            ResetSessionOBJ resetSessionOBJ = seguridadDAO.obtenerRessetSesion(req.getDevice_id(), hashResetSession);
            if (resetSessionOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.RESET_SESSION_NO_VALIDO,
                        ErroresResetPassword.descError.get(ErroresResetPassword.RESET_SESSION_NO_VALIDO));
            }

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(resetSessionOBJ.getPersonaId());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.SOLICITANTE_NOT_FOUND,
                        ErroresResetPassword.descError.get(ErroresResetPassword.SOLICITANTE_NOT_FOUND));
            }

            LocalDateTime fechaActual = seguridadDAO.obtenerHoraBd();
            if (fechaActual.isAfter(resetSessionOBJ.getFecha_expira()) || fechaActual.equals(resetSessionOBJ.getFecha_expira())) {
                //desactivar reset session
                seguridadDAO.desactivarPasswordResetSession(resetSessionOBJ.getId(), true);

                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.RESET_SESSION_EXPIRADO,
                        ErroresResetPassword.descError.get(ErroresResetPassword.RESET_SESSION_EXPIRADO),
                        solicitanteOBJ.getTelefonoCoDi());
            }

            //Se obtiene el registro CoDi mediante el solicitante id
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = registroCoDiService.validaRegistroCoDiBase(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                respuesta.setData(solicitanteOBJ.getTelefonoCoDi());
                return respuesta;
            }

            String pass = "";
            //desencriptar contraseña
            try {
                String keySource = cifUtil.generaKeySource(env.getProperty("aes.key.password", String.class));
                pass = cifUtil.desencriptaInformacionB64(keySource, req.getNueva_password());
            } catch (Exception e) {
                errorHandler.handleException(e);
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.ERROR_CIF,
                        ErroresResetPassword.descError.get(ErroresResetPassword.ERROR_CIF),
                        solicitanteOBJ.getTelefonoCoDi());
            }

            respuesta = passwordResetAuxService.validaReglasPassword(pass, solicitanteOBJ.getNombre());
            if (respuesta.getCodigo() != 0) {
                respuesta.setData(solicitanteOBJ.getTelefonoCoDi());
                return respuesta;
            }

            respuesta = codiDAO.actualizarPassword(req.getNueva_password(), regCoDiOBJ.getSolicitanteId(), usuarioSistemas);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.ERROR_CAMBIO_PASS,
                        ErroresResetPassword.descError.get(ErroresResetPassword.ERROR_CAMBIO_PASS),
                        solicitanteOBJ.getTelefonoCoDi());
            }
            //Se limpia el bloqueo de registro CoDi
            codiDAO.limpiarBloqueoRegistroCoDi(solicitanteOBJ.getNumero());
            //guardamos en bitacora
            solicitanteDAO.registrarBitacoraCambio(solicitanteOBJ.getNumero(), cveCambioPass, 9, regCoDiOBJ.getPassword(), req.getNueva_password(), obsCambioPassAspPago);
            //desactivar reset session y marcar como usada
            seguridadDAO.desactivarPasswordResetSession(resetSessionOBJ.getId(), true);
            //revocacion de JWT
            msRevocaJwtTokenCliente.revocaJwtTokenCliente(solicitanteOBJ.getTelefonoCoDi());
            String bodyCorreo = correoUtils.generaCorreoCambioPass(solicitanteOBJ.getNombre());
            log.info("Se enviara correo Cambio de contrasena exitoso a {}", solicitanteOBJ.getCorreo());
            msEnviarEmail.enviarCorreo(solicitanteOBJ.getCorreo(), "Cambio de contraseña exitoso", bodyCorreo);

            RespuestaUtils.respuestaExito(respuesta, solicitanteOBJ.getTelefonoCoDi());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @NotNull
    public BitacoraCambioPasswordOBJ getBitacoraCambioPasswordOBJ(String valor, String tipo, String deviceId, String obs,
                                                                  String tipoEvento, String resultado) {
        BitacoraCambioPasswordOBJ bitacoraOBJ = new BitacoraCambioPasswordOBJ();
        bitacoraOBJ.setValor_referencia(valor);
        bitacoraOBJ.setTipo_referencia(tipo);
        bitacoraOBJ.setDevice_id(deviceId);
        bitacoraOBJ.setObservaciones(obs);
        bitacoraOBJ.setTipo_evento(tipoEvento);
        bitacoraOBJ.setResultado(resultado);
        return bitacoraOBJ;
    }
}