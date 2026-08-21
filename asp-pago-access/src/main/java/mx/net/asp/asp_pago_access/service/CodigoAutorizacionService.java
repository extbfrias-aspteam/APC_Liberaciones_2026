package mx.net.asp.asp_pago_access.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.*;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.BitacoraCodAutorizacionOBJ;
import mx.net.asp.asp_pago_access.model.ConceptoCuentaOBJ;
import mx.net.asp.asp_pago_access.model.PinOTPOBJ;
import mx.net.asp.asp_pago_access.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_access.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_access.model.RegistroCodAutOBJ;
import mx.net.asp.asp_pago_access.request.*;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresCodAutorizacion;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresLogIn;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CodigoAutorizacionService {

    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ProcreaDAO procreaDAO;
    private final CoDiDAO coDiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final SeguridadDAO seguridadDAO;
    private final Environment env;
    private final ErrorHandler errorHandler;

    @Value("${version.app}")
    private String versionApp;
    @Value("${cambio.cod.autorizacion.clv}")
    private String cveCambioCodAutorizacion;
    @Value("${obs.cambio.cod.autorizacion.asp.pago}")
    private String obsCambioCodAutAspPago;
    @Value("${cambio.cod.autorizacion.bio.clv}")
    private String cveCambioCodAutorizacionBio;
    @Value("${obs.cambio.cod.autorizacion.bio.asp.pago}")
    private String obsCambioCodAutBioAspPago;

    public RespuestaDTO validarCodigoAutorizacionInicial(ValidarCodigoAutorizacionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        BitacoraCodAutorizacionOBJ bitacora = new BitacoraCodAutorizacionOBJ();
        String observaciones;
        try {
            //Se inicializa obtjeto de bitacora del codigo de autorización
            bitacora.setCod_autorizacion(req.getCodigo());
            bitacora.setId_servicio(req.getServicioId());
            bitacora.setId_usuario(req.getUsuarioId());
            bitacora.setEstatus(false);
            respuesta = validarCodigoAutorizacion(req);
            if (respuesta.getCodigo() == 0)
                bitacora.setEstatus(true);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        //registrar bitacora codigo autorización
        observaciones = respuesta.getMensaje();
        bitacora.setObservaciones(observaciones);
        procreaDAO.guardarBitacoraCodigoAutorizacion(bitacora);
        return respuesta;
    }

    public RespuestaDTO validarCodigoAutorizacion(ValidarCodigoAutorizacionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Se valida que exista el servicio que se recibio
            respuesta = procreaDAO.validarExisteIdServicioASPPago(req.getServicioId());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ID_SERVICIO_NOT_FOUND,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ID_SERVICIO_NOT_FOUND));
            }

            // Se valida si el codigo de servicio es igual a 4
            if (req.getServicioId() == 4) {
                log.info("****************** Servicio ID igual a 4 ******************");

                // Validar si el codigo de autorizacion esta bloqueado
                log.info("Ejecutando metodo obtenerRegistroCodAutByPersonaId");
                RegistroCodAutOBJ registroCodAutOBJ = seguridadDAO.obtenerRegistroCodAutByPersonaId(req.getUsuarioId());
                log.info("registroCodAutOBJ : {}", registroCodAutOBJ);
                if (registroCodAutOBJ == null) {
                    // Insertar registro
                    registroCodAutOBJ = new RegistroCodAutOBJ();
                    registroCodAutOBJ.setPersonaId(req.getUsuarioId());
                    registroCodAutOBJ.setIntentos(0);
                    registroCodAutOBJ.setBloqueado(false);

                    log.info("Ejecutando metodo insertarRegistroCodAut");
                    respuesta = seguridadDAO.insertarRegistroCodAut(registroCodAutOBJ, 9);
                    if(respuesta.getCodigo() != 0){
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresCodAutorizacion.ERROR_INSERT_REG_COD_AUT,
                                ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_INSERT_REG_COD_AUT));
                    }
                }

                /*
                 * Cuando el codigo de autorización ESTÁ bloqueado
                 */
                if (Boolean.TRUE.equals(registroCodAutOBJ.getBloqueado())) {

                    PinOTPOBJ pinOTPOBJ = seguridadDAO.obtenerPinOTPActivoByPersonaId(req.getUsuarioId());
                    if (pinOTPOBJ == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresCodAutorizacion.ERROR_MAXIMO_INTENTOS,
                                ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_MAXIMO_INTENTOS));
                    }

                    // Continuar conn el proceso de validación de expiración del pin otp
                    respuesta = validaExpiracionPinOTP(registroCodAutOBJ, pinOTPOBJ, req);
                    if (respuesta.getCodigo() != 0){
                        return respuesta;
                    }

                    return RespuestaUtils.respuestaExito(respuesta, 0, "Código validado correctamente");
                }

                /*
                 * Cuándo el código de autorización NO ESTÁ bloqueado
                 */
                PinOTPOBJ pinOTPOBJ = seguridadDAO.obtenerPinOTPActivoByPersonaId(req.getUsuarioId());
                if (pinOTPOBJ == null) {
                    log.info("No hay pin activo, se procede a comparar el codigo de autorizacion del request con el de DB");
                    respuesta = esCodigoAutorizacionValido(req, registroCodAutOBJ.getIntentos());
                    if (respuesta.getCodigo() != 0){
                        return respuesta;
                    }

                    return RespuestaUtils.respuestaExito(respuesta, 0, "Código validado correctamente");
                } else {
                    respuesta = validaExpiracionPinOTP(registroCodAutOBJ, pinOTPOBJ, req);
                    if (respuesta.getCodigo() != 0){
                        return respuesta;
                    }
                    return RespuestaUtils.respuestaExito(respuesta, 0, "Código validado correctamente");
                }
            }

            //Se valida si el codigo de autorización es biometrico
            String claveConcepto;
            if (Boolean.TRUE.equals(req.getBiometrico()))
                claveConcepto = env.getProperty("clave.biometrico", String.class);
            else
                claveConcepto = env.getProperty("clave.codigo.autorizacion", String.class);
            //Se obtiene el registro codi activo a traves del id del cliente
            RegistroCoDiOBJ registroCoDiOBJ = coDiDAO.consultarRegistroCoDiCliente(req.getUsuarioId());
            respuesta = validarRegistroCoDi(registroCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }
            //Se obtiene si la cuenta es de cero o procrea
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(registroCoDiOBJ.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.CUENTA_NOT_FOUND,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.CUENTA_NOT_FOUND));
            }

            log.info("tipoCuentaAh:: {}", posGlobCuentaObj.getTipoCuenta());

            boolean esCuentaAhorro = "AHORRO".equals(posGlobCuentaObj.getTipoCuenta());
            ConceptoCuentaOBJ conceptoCuentaOBJ = obtenerConceptoCuenta(esCuentaAhorro, claveConcepto, req.getUsuarioId());

            if (conceptoCuentaOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_VALIDAR_COD_AUTORIZACION,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_VALIDAR_COD_AUTORIZACION));
            }
            log.info("codigo req:: {}", req.getCodigo());

            if (conceptoCuentaOBJ.getValor().equals(req.getCodigo())) {
                RespuestaUtils.respuestaExito(respuesta, 0, "Código validado correctamente");
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.COD_AUTORIZACION_INCORRECTO,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.COD_AUTORIZACION_INCORRECTO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO cambiarCodigoAutorizacionInicial(CambiarCodigoAutorizacionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        BitacoraCodAutorizacionOBJ bitacora = new BitacoraCodAutorizacionOBJ();
        String observaciones;
        try {
            //Se inicializa obtjeto de bitacora del codigo de autorización
            bitacora.setCod_autorizacion(req.getCodigoNuevo());
            bitacora.setId_servicio(4);
            bitacora.setId_usuario(req.getUsuarioId());
            bitacora.setEstatus(false);
            respuesta = cambiarCodigoAutorizacion(req);
            if (respuesta.getCodigo() == 0)
                bitacora.setEstatus(true);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        //registrar bitacora codigo autorización
        observaciones = respuesta.getMensaje();
        bitacora.setObservaciones(observaciones);
        procreaDAO.guardarBitacoraCodigoAutorizacion(bitacora);
        return respuesta;
    }

    public RespuestaDTO cambiarCodigoAutorizacion(CambiarCodigoAutorizacionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String clave = "";
        String obs = "";
        try {
            //Se valida si el codigo de autorización es biometrico
            String claveConcepto;
            if (Boolean.TRUE.equals(req.getBiometrico())) {
                claveConcepto = env.getProperty("clave.biometrico", String.class);
                clave = cveCambioCodAutorizacionBio;
                obs = obsCambioCodAutBioAspPago;
            } else {
                claveConcepto = env.getProperty("clave.codigo.autorizacion", String.class);
                clave = cveCambioCodAutorizacion;
                obs = obsCambioCodAutAspPago;
            }
            //Se obtiene el registro codi activo a traves del id del cliente
            RegistroCoDiOBJ registroCoDiOBJ = coDiDAO.consultarRegistroCoDiCliente(req.getUsuarioId());
            respuesta = validarRegistroCoDi(registroCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }
            //Se obtiene si la cuenta es de cero o procrea
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(registroCoDiOBJ.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.CUENTA_NOT_FOUND,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.CUENTA_NOT_FOUND));
            }

            log.info("tipoCuentaAh:: {}", posGlobCuentaObj.getTipoCuenta());

            boolean esCuentaAhorro = "AHORRO".equals(posGlobCuentaObj.getTipoCuenta());
            ConceptoCuentaOBJ conceptoCuentaOBJ = obtenerConceptoCuenta(esCuentaAhorro, claveConcepto, req.getUsuarioId());

            if (conceptoCuentaOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_ACTUALIZAR_COD_AUTORIZACION,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_ACTUALIZAR_COD_AUTORIZACION));
            }

            RespuestaDTO respCero = conceptosCuentaDAO.actualizarValorConceptoCuentaCeroByPersonaId(req.getUsuarioId(), claveConcepto, req.getCodigoNuevo());
            RespuestaDTO respProcrea = conceptosCuentaDAO.actualizarValorConceptoCuentaProcreaByPersonaId(req.getUsuarioId(), claveConcepto, req.getCodigoNuevo());

            if (respCero.getCodigo() == 0 || respProcrea.getCodigo() == 0) {
                solicitanteDAO.registrarBitacoraCambio(registroCoDiOBJ.getSolicitanteId(), clave, 9, conceptoCuentaOBJ.getValor(), req.getCodigoNuevo(), obs);
                RespuestaUtils.respuestaExito(respuesta, 0, "Codigo de autorización actualizado correctamente.");
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_ACTUALIZAR_COD_AUTORIZACION,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_ACTUALIZAR_COD_AUTORIZACION));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validarRegistroCoDi(RegistroCoDiOBJ regCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.REGISTRO_CODI_NOT_FOUND,
                        ErroresLogIn.descError.get(ErroresLogIn.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.VERSION_APP,
                        ErroresLogIn.descError.get(ErroresLogIn.VERSION_APP));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private ConceptoCuentaOBJ obtenerConceptoCuenta(boolean esCuentaAhorro, String claveConcepto, String usuarioId) {
        return esCuentaAhorro
                ? conceptosCuentaDAO.obtenerConceptoCuentaProcreaByPersonaId(claveConcepto, usuarioId)
                : conceptosCuentaDAO.obtenerConceptoCuentaCeroByPersonaId(claveConcepto, usuarioId);
    }

    private RespuestaDTO validaExpiracionPinOTP(RegistroCodAutOBJ registroCodAutOBJ, PinOTPOBJ pinOTPOBJ, ValidarCodigoAutorizacionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        log.info("registroCodAutOBJ: {}", registroCodAutOBJ);
        log.info("pinOTPOBJ: {}", pinOTPOBJ);
        log.info("req: {}", req);
        try {

            // Validar si el pin otp ya expiro
            Instant fechaActual = Instant.now();
            Instant fechaExpira = pinOTPOBJ.getFechaExpira().toInstant();
            if (fechaActual.isAfter(fechaExpira) || fechaActual.equals(fechaExpira)) {

                // Desactivar pin_otp y regresar mensaje de error
                respuesta = seguridadDAO.desactivarPinOTPById(pinOTPOBJ.getId(), 9);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCodAutorizacion.ERROR_DESACTIVAR_PIN,
                            ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_DESACTIVAR_PIN));
                }

                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.PIN_EXPIRADO,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.PIN_EXPIRADO));
            }

            if (!req.getCodigo().equals(pinOTPOBJ.getPin_otp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.PIN_OTP_INCORRECTO,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.PIN_OTP_INCORRECTO));
            }

            registroCodAutOBJ = new RegistroCodAutOBJ();
            registroCodAutOBJ.setIntentos(0);
            registroCodAutOBJ.setBloqueado(false);
            registroCodAutOBJ.setPersonaId(req.getUsuarioId());
            registroCodAutOBJ.setUsuarioModificacion(Integer.valueOf(req.getUsuarioId()));

            respuesta = seguridadDAO.actualizarIntentosCodAut(registroCodAutOBJ, 9);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT));
            }

            // Desactivar pin_otp y regresar mensaje de error
            respuesta = seguridadDAO.desactivarPinOTPById(pinOTPOBJ.getId(), 9);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_DESACTIVAR_PIN,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_DESACTIVAR_PIN));
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private RespuestaDTO esCodigoAutorizacionValido(ValidarCodigoAutorizacionReq req, Integer intentos){
        RespuestaDTO respuesta = new RespuestaDTO();
        RegistroCodAutOBJ registroCodAutOBJ = new RegistroCodAutOBJ();
        try {
            //Se valida si el codigo de autorización es biometrico
            String claveConcepto;
            if (Boolean.TRUE.equals(req.getBiometrico()))
                claveConcepto = env.getProperty("clave.biometrico", String.class);
            else
                claveConcepto = env.getProperty("clave.codigo.autorizacion", String.class);
            //Se obtiene el registro codi activo a traves del id del cliente
            RegistroCoDiOBJ registroCoDiOBJ = coDiDAO.consultarRegistroCoDiCliente(req.getUsuarioId());
            respuesta = validarRegistroCoDi(registroCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }
            //Se obtiene si la cuenta es de cero o procrea
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(registroCoDiOBJ.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.CUENTA_NOT_FOUND,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.CUENTA_NOT_FOUND));
            }

            log.info("tipoCuentaAh:: {}", posGlobCuentaObj.getTipoCuenta());

            boolean esCuentaAhorro = "AHORRO".equals(posGlobCuentaObj.getTipoCuenta());
            ConceptoCuentaOBJ conceptoCuentaOBJ = obtenerConceptoCuenta(esCuentaAhorro, claveConcepto, req.getUsuarioId());

            if (conceptoCuentaOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_VALIDAR_COD_AUTORIZACION,
                        ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_VALIDAR_COD_AUTORIZACION));
            }
            log.info("codigo req:: {}", req.getCodigo());

            if (conceptoCuentaOBJ.getValor().equals(req.getCodigo())) {

                registroCodAutOBJ = new RegistroCodAutOBJ();
                registroCodAutOBJ.setIntentos(0);
                registroCodAutOBJ.setBloqueado(false);
                registroCodAutOBJ.setPersonaId(req.getUsuarioId());

                respuesta = seguridadDAO.actualizarIntentosCodAut(registroCodAutOBJ, 9);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT,
                            ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT));
                }
                RespuestaUtils.respuestaExito(respuesta, 0, "Código validado correctamente");

            } else {
                registroCodAutOBJ = new RegistroCodAutOBJ();
                registroCodAutOBJ.setIntentos(intentos + 1);
                registroCodAutOBJ.setPersonaId(req.getUsuarioId());

                if(registroCodAutOBJ.getIntentos() >= 3){
                    registroCodAutOBJ.setBloqueado(true);
                    respuesta = seguridadDAO.actualizarIntentosCodAut(registroCodAutOBJ, 9);
                    if (respuesta.getCodigo() != 0) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT,
                                ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT));
                    }

                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCodAutorizacion.ERROR_MAXIMO_INTENTOS,
                            ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_MAXIMO_INTENTOS));

                }

                registroCodAutOBJ.setBloqueado(false);
                respuesta = seguridadDAO.actualizarIntentosCodAut(registroCodAutOBJ, 9);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT,
                            ErroresCodAutorizacion.descError.get(ErroresCodAutorizacion.ERROR_UPDATE_COD_AUT));
                }
                int intentosRestantes = 3 - registroCodAutOBJ.getIntentos();
                String mensajeIntentos = switch (intentosRestantes) {
                    case 1 -> "queda *1 intento*";
                    case 2 -> "quedan *2 intentos*";
                    default -> "quedan pocos intentos";
                };

                return RespuestaUtils.asignarError(respuesta,
                        ErroresCodAutorizacion.ERROR_INTENTOS,
                        ErroresCodAutorizacion.getMensajeError(ErroresCodAutorizacion.ERROR_INTENTOS, mensajeIntentos));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }
}
