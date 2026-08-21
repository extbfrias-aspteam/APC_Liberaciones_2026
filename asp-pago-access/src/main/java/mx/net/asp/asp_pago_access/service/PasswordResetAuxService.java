package mx.net.asp.asp_pago_access.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.CoDiDAO;
import mx.net.asp.asp_pago_access.dao.NucleoCentralDAO;
import mx.net.asp.asp_pago_access.dao.SeguridadDAO;
import mx.net.asp.asp_pago_access.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.*;
import mx.net.asp.asp_pago_access.request.SolicitudPasswordResetReq;
import mx.net.asp.asp_pago_access.utilerias.*;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresResetPassword;
import mx.net.asp.asp_pago_access.ws.asp.MSEnviarEmail;
import mx.net.asp.asp_pago_access.ws.asp.MSEnviarSMS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class PasswordResetAuxService {

    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final SeguridadDAO seguridadDAO;
    private final NucleoCentralDAO nucleoCentralDAO;

    private final Environment env;
    private final CifradoUtil cifUtil;
    private final PasswordUtils passUtils;
    private final CorreoUtils correoUtils;
    private final ErrorHandler errorHandler;

    private final RegistroCoDiService registroCoDiService;
    private final MSEnviarEmail msEnviarEmail;
    private final MSEnviarSMS msEnviarSMS;

    @Value("${min.otp.res.pwd}")
    private Integer tiempoOTP;
    @Value("${min.control.ventana.res.pwd}")
    private Integer tiempoControlVentana;
    @Value("${cve.tipo.envio.otp}")
    private String cveTipoEnvioOtp;
    @Value("${min.length.pass.rule}")
    private Integer minLengthPass;
    @Value("${max.length.pass.rule}")
    private Integer maxLengthPass;
    @Value("${tries.res.pwd}")
    private Integer triesResetPwd;
    @Value("${ms.notification.enviar.sms.service}")
    private String enviaSMSService;
    @Value("${ms.notification.enviar.whatsapp.service}")
    private String enviaWhatsappService;

    @Async
    public CompletableFuture<RespuestaDTO> procesaPasswordResetSolicitudAsync(SolicitudPasswordResetReq req, Long idProcesoBit) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
            if (solicitanteOBJ == null) {
                RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.SOLICITANTE_NOT_FOUND_P,
                        ErroresResetPassword.getMensajeError(ErroresResetPassword.SOLICITANTE_NOT_FOUND_P, req.getTelefono()));

               return CompletableFuture.completedFuture(respuesta);
            }
            log.info("### Datos solicitante que solicita el cambio de password, numero: {}, telefonoCoDi: {}",
                    solicitanteOBJ.getNumero(), solicitanteOBJ.getTelefonoCoDi());

            //Se obtiene el registro CoDi mediante el solicitante id
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = registroCoDiService.validaRegistroCoDiBase(regCoDiOBJ);
            if (respuesta.getCodigo() != 0)
                return CompletableFuture.completedFuture(respuesta);

            //Obtener cuentas del cliente y validar que al menos una este vigente
            boolean tieneCuentasVigentes = nucleoCentralDAO.tieneCuentasVigentes(solicitanteOBJ.getNumero());
            if (!tieneCuentasVigentes) {
                RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.CLIENTE_SIN_CUENTAS_VIGENTES,
                        ErroresResetPassword.descError.get(ErroresResetPassword.CLIENTE_SIN_CUENTAS_VIGENTES));

                return CompletableFuture.completedFuture(respuesta);
            }

            //Control intentos SOLICITUD OTP (ventana 30 min)
            respuesta = validarControlPasswordReset(solicitanteOBJ.getNumero(), regCoDiOBJ.getIdHardware(), "REQUEST_OTP");
            if (respuesta.getCodigo() != 0)
                return CompletableFuture.completedFuture(respuesta);;

            //Desactivamos cualquier OTP y reset_session activo
            desactivarOtpsPrevios(regCoDiOBJ.getSolicitanteId());

            String otp = generarOTP();
            LocalDateTime fechaGenera = seguridadDAO.obtenerHoraBd();
            LocalDateTime fechaExpira = fechaGenera.plusMinutes(tiempoOTP);

            String OtpCif = cifUtil.encriptaInformacionB64(
                    cifUtil.generaKeySource(env.getProperty("aes.key.password", String.class)),
                    otp
            );

            OTPPwdOBJ otpPwdObj = construirOTP(regCoDiOBJ.getSolicitanteId(), req.getDevice_id(), OtpCif, fechaGenera, fechaExpira);

            Long idOTP = seguridadDAO.guardarOTPResetPassword(otpPwdObj);

            if (idOTP != null) {
                otpPwdObj.setId(idOTP);
                log.warn("*** Tipo de envío OTP: {}", cveTipoEnvioOtp);
                String mensaje = "";
                switch (cveTipoEnvioOtp.toUpperCase()) {
                    case "WP":
                        mensaje = otp;
                        msEnviarSMS.enviarSMS(req.getTelefono(), mensaje, regCoDiOBJ.getSolicitanteId(), "Codigo de verificacion whatsapp cambio password", BigInteger.valueOf(otpPwdObj.getId()), enviaWhatsappService);
                        break;
                    case "MAIL":
                        enviarCorreoOTP(solicitanteOBJ.getCorreo(), otp, solicitanteOBJ.getNombre());
                        break;
                    case "SMS":
                        mensaje = "Código de verificación: " + otp;
                        msEnviarSMS.enviarSMS(req.getTelefono(), mensaje, regCoDiOBJ.getSolicitanteId(), "Codigo de verificacion cambio password", BigInteger.valueOf(otpPwdObj.getId()), enviaSMSService);
                        break;
                    default:
                        log.warn("Tipo de envío OTP no soportado: {}, se envia mediante correo electronico", cveTipoEnvioOtp);
                        enviarCorreoOTP(solicitanteOBJ.getCorreo(), otp, solicitanteOBJ.getNombre());
                }

                RespuestaUtils.respuestaExito(respuesta, 0, "OTP generado con éxito.");
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.ERROR_PETICION_CAMBIO_PASS,
                        ErroresResetPassword.descError.get(ErroresResetPassword.ERROR_PETICION_CAMBIO_PASS));

                return CompletableFuture.completedFuture(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            if (idProcesoBit != null && idProcesoBit > 0) {
                String resultado = respuesta.getCodigo() == 0 ? "OK" : "ERROR";
                seguridadDAO.actualizaBitacoraCambioPassword(idProcesoBit, resultado, respuesta.getMensaje());
            }
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    public RespuestaDTO validaReglasPassword(String pass, String nombre) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (pass.length() < minLengthPass || pass.length() > maxLengthPass) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresResetPassword.PASS_NO_CUMPLE,
                    ErroresResetPassword.getMensajeError(ErroresResetPassword.PASS_NO_CUMPLE,
                            String.format("Debe tener entre %s y %s caracteres", minLengthPass, maxLengthPass)));
        }

        if (passUtils.tieneCaracteresConsecutivos(pass)) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresResetPassword.PASS_NO_CUMPLE,
                    ErroresResetPassword.getMensajeError(ErroresResetPassword.PASS_NO_CUMPLE, "No debe contener caracteres consecutivos"));
        }

        if (passUtils.contieneNombreCliente(pass, nombre)) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresResetPassword.PASS_NO_CUMPLE,
                    ErroresResetPassword.getMensajeError(ErroresResetPassword.PASS_NO_CUMPLE, "contiene el nombre del cliente"));
        }

        // Validar que contiene al menos un dígito, una minúscula, una mayúscula y un carácter especial
        boolean tieneDigito = pass.chars().anyMatch(Character::isDigit);
        boolean tieneMinuscula = pass.chars().anyMatch(Character::isLowerCase);
        boolean tieneMayuscula = pass.chars().anyMatch(Character::isUpperCase);
        boolean tieneEspecial = pass.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        if (!(tieneDigito && tieneMinuscula && tieneMayuscula && tieneEspecial)) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresResetPassword.PASS_NO_CUMPLE,
                    ErroresResetPassword.getMensajeError(ErroresResetPassword.PASS_NO_CUMPLE, "falta: digito, minuscula, mayuscula o caracter especial"));
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }

    public RespuestaDTO validarControlPasswordReset(String personaId, String deviceId, String tipoControl) {

        RespuestaDTO respuesta = new RespuestaDTO();
        LocalDateTime now = seguridadDAO.obtenerHoraBd();
        String observaciones = "REQUEST_OTP".equals(tipoControl)
                ? "Solicitud OTP"
                : "Validacion OTP";

        try {

            PasswordResetControlOBJ control =
                    seguridadDAO.obtenerPwdResetControl(personaId, tipoControl);

            // ─────────────────────────────
            // Registro NO existe
            // ─────────────────────────────
            if (control == null) {
                //ERROR INTERNO

                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }

            if (control.getId() == null) {

                Long id = seguridadDAO.registraNuevoPwdResetControl(personaId, tipoControl);

                if (id != null) {
                    seguridadDAO.insertarHistoricoPwdResetControl(id, personaId, deviceId,
                            EventoControl.CREATE.getClave(), 1, observaciones, tipoControl);

                    return RespuestaUtils.respuestaExito(respuesta, 0, "OK");
                } else {
                    //Retornar ERROR
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresGenerales.ERROR_INTERNO,
                            ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
                }
            }

            // ─────────────────────────────
            // Registro EXISTE
            // ─────────────────────────────
            // Bloqueado y aún en ventana de bloqueo
            if (Boolean.TRUE.equals(control.getBloqueado())
                    && now.isBefore(control.getFechaBloqueo().plusMinutes(tiempoControlVentana))) {

                seguridadDAO.insertarHistoricoPwdResetControl(control.getId(), personaId, deviceId,
                        EventoControl.BLOQUEADO.getClave(),
                        control.getIntentos(),
                        observaciones + " - Registro bloqueado", tipoControl);

                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.SOLICITUD_OTP_BLOQUEADA,
                        ErroresResetPassword.getMensajeError(ErroresResetPassword.SOLICITUD_OTP_BLOQUEADA, String.valueOf(tiempoControlVentana)));
            }

            // Bloqueado pero bloqueo expiró
            if (Boolean.TRUE.equals(control.getBloqueado())
                   && control.getFechaBloqueo() != null
                    && now.isAfter(control.getFechaBloqueo().plusMinutes(tiempoControlVentana))) {

                seguridadDAO.insertarHistoricoPwdResetControl(control.getId(), personaId, deviceId,
                        EventoControl.DESBLOQUEO.getClave(),
                        1,
                        observaciones + " - Bloqueo expirado", tipoControl);

                limpiarControlYRelacionado(
                        control,
                        personaId,
                        deviceId,
                        tipoControl,
                        observaciones + " - Reinicio de ventana"
                );

                return RespuestaUtils.respuestaExito(respuesta);
            }

            // Ventana expirada
            if (now.isAfter(control.getFechaInicioVentana().plusMinutes(tiempoControlVentana))) {

                //limpiar por cliente
                limpiarControlYRelacionado(
                        control,
                        personaId,
                        deviceId,
                        tipoControl,
                        observaciones + " - Reinicio de ventana"
                );

                return RespuestaUtils.respuestaExito(respuesta);
            }

            // Dentro de ventana
            int nuevosIntentos = control.getIntentos() + 1;

            // Supera límite
            if (nuevosIntentos > triesResetPwd) {

                //bloquear por cliente
                seguridadDAO.bloquearPwdResetControl(control.getId(), nuevosIntentos);

                // ───── BLOQUEO CRUZADO ─────
                String controlRelacionado = obtenerControlRelacionado(tipoControl);

                PasswordResetControlOBJ otroControl =
                        seguridadDAO.obtenerPwdResetControl(personaId, controlRelacionado);

                if (otroControl != null && Boolean.FALSE.equals(otroControl.getBloqueado())) {

                    seguridadDAO.bloquearPwdResetControl(otroControl.getId(), nuevosIntentos);

                    seguridadDAO.insertarHistoricoPwdResetControl(
                            otroControl.getId(),
                            personaId,
                            deviceId,
                            EventoControl.BLOQUEO.getClave(),
                            nuevosIntentos,
                            "Bloqueo por efecto de " + tipoControl,
                            controlRelacionado
                    );
                }

                seguridadDAO.insertarHistoricoPwdResetControl(control.getId(), personaId, deviceId,
                        EventoControl.BLOQUEO.getClave(),
                        nuevosIntentos,
                        observaciones + " - Se superó el límite de intentos", tipoControl);

                return RespuestaUtils.asignarError(respuesta,
                        ErroresResetPassword.LIMITE_SUPERADO,
                        ErroresResetPassword.getMensajeError(ErroresResetPassword.LIMITE_SUPERADO, String.valueOf(tiempoControlVentana)));
            }

            // Incrementa intentos
            seguridadDAO.incrementarIntentosPwdResetControl(control.getId(), nuevosIntentos);

            seguridadDAO.insertarHistoricoPwdResetControl(control.getId(), personaId, deviceId,
                    EventoControl.INCREMENT.getClave(),
                    nuevosIntentos,
                    observaciones, tipoControl);

            RespuestaUtils.respuestaExito(respuesta);

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private void limpiarControlYRelacionado(
            PasswordResetControlOBJ control,
            String personaId,
            String deviceId,
            String tipoControl,
            String observaciones
    ) {

        // Limpia control actual
        seguridadDAO.limpiarPwdResetControl(control.getId());

        seguridadDAO.insertarHistoricoPwdResetControl(
                control.getId(),
                personaId,
                deviceId,
                EventoControl.RESET_VENTANA.getClave(),
                1,
                observaciones,
                tipoControl
        );

        // ───── LIMPIEZA CRUZADA ─────
        String controlRelacionado = obtenerControlRelacionado(tipoControl);

        PasswordResetControlOBJ otroControl =
                seguridadDAO.obtenerPwdResetControl(personaId, controlRelacionado);

        if (otroControl != null) {

            seguridadDAO.limpiarPwdResetControl(otroControl.getId());

            seguridadDAO.insertarHistoricoPwdResetControl(
                    otroControl.getId(),
                    personaId,
                    deviceId,
                    EventoControl.RESET_VENTANA.getClave(),
                    1,
                    "Reinicio por efecto de " + tipoControl,
                    controlRelacionado
            );
        }
    }

    private void desactivarOtpsPrevios(String personaId) {
        seguridadDAO.desactivarOTPActivosResetPasswordByPersonaId(personaId);
        seguridadDAO.desactivarPasswordResetSessionActivasByPersonaId(personaId);
    }

    private OTPPwdOBJ construirOTP(String personaId, String deviceId, String cifradoPin, LocalDateTime fechaGenera, LocalDateTime fechaExpira) {
        OTPPwdOBJ pin = new OTPPwdOBJ();
        pin.setOtpHash(cifradoPin);
        pin.setPersonaId(personaId);
        pin.setDeviceId(deviceId);
        pin.setFecha_creacion(fechaGenera);
        pin.setFecha_expira(fechaExpira);
        pin.setActivo(true);
        return pin;
    }

    private void enviarCorreoOTP(String correo, String pin, String nombre) {
        String cuerpoCorreo = correoUtils.generaCorreoPeticionPinOTP(pin, nombre, String.valueOf(tiempoOTP));
        log.info("Se enviara correo Solicitud OTP para cambio de contrasena a {}", correo);
        msEnviarEmail.enviarCorreo(correo,
                "Tu código de un solo uso para reestablecer tu contraseña",
                cuerpoCorreo);
    }

    public static String generarOTP() {
        SecureRandom random = new SecureRandom();
        int pin = random.nextInt(10_00000); // Genera un número entre 0 y 999999
        return String.format("%06d", pin); // Asegura que tenga 6 dígitos (rellena con ceros si es necesario)
    }

    public static String generarResetSession() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String mensajeConfirmacionOtp(String cveTipoEnvioOtp) {
        return switch (cveTipoEnvioOtp.toUpperCase()) {
            case "WP" -> "Tu solicitud está siendo procesada.\n" +
                    "Si la información proporcionada es correcta, recibirás un WhatsApp para poder continuar el proceso.";
            case "MAIL" -> "Tu solicitud está siendo procesada.\n" +
                    "Si la información proporcionada es correcta, recibirás un correo electrónico para poder continuar el proceso.";
            case "SMS" -> "Tu solicitud está siendo procesada.\n" +
                    "Si la información proporcionada es correcta, recibirás un SMS para poder continuar el proceso.";
            default -> "Tu solicitud está siendo procesada.\n" +
                    "Si la información proporcionada es correcta, recibirás un mensaje para poder continuar el proceso.";
        };
    }

    public ResetSessionOBJ construirRessetSession(String personaId, String deviceId, String idSession, LocalDateTime fechaGenera, LocalDateTime fechaExpira) {
        ResetSessionOBJ resetSessionOBJ = new ResetSessionOBJ();
        resetSessionOBJ.setPersonaId(personaId);
        resetSessionOBJ.setDeviceId(deviceId);
        resetSessionOBJ.setFecha_creacion(fechaGenera);
        resetSessionOBJ.setFecha_expira(fechaExpira);
        resetSessionOBJ.setActivo(true);
        resetSessionOBJ.setId_session(idSession);
        return resetSessionOBJ;
    }

    private String obtenerControlRelacionado(String tipoControl) {
        return "REQUEST_OTP".equals(tipoControl)
                ? "VALIDATION_OTP"
                : "REQUEST_OTP";
    }
}