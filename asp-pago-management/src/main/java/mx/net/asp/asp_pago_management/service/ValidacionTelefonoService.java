package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.request.EnviarCodigoReq;
import mx.net.asp.asp_pago_management.request.ValidaTelefonoReq;
import mx.net.asp.asp_pago_management.utilerias.AESSFC;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.Token;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresTokenHuella;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresValidacionTelefono;
import mx.net.asp.asp_pago_management.ws.asp.MSEnviarEmail;
import mx.net.asp.asp_pago_management.ws.asp.MSEnviarSMS;
import mx.net.asp.asp_pago_management.ws.asp.MSRevocaJwtTokenCliente;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidacionTelefonoService {

    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final PlasticosDAO plasticosDAO;
    private final MSEnviarSMS msEnviarSMS;
    private final MSEnviarEmail msEnviarEmail;
    private final MSRevocaJwtTokenCliente msRevocaJwtTokenCliente;
    private final ErrorHandler errorHandler;
    private final Environment env;
    private final Gson gson;
    private final String telefonoValidado = "El teléfono se validó correctamente.";
    private final String ahorroString = "AHORRO";

    @Value("${ms.notification.enviar.sms.service}")
    private String enviaSMSService;
    @Value("${ms.notification.enviar.whatsapp.service}")
    private String enviaWhatsappService;
    @Value("${concepto.valida.sms.default}")
    private String conceptoValidaSMSDefault;
    @Value("${concepto.valida.sms.onb}")
    private String conceptoValidaSMSOnb;
    @Value("${concepto.valida.sms.id.hardware}")
    private String conceptoValidaSMSIdHardware;
    @Value("${producto.id.cuenta.facil}")
    private Integer prodcutoIdCtaFacil;
    @Value("${producto.id.mi.ahorro}")
    private Integer prodcutoIdMiAhorro;
    @Value("${version.app}")
    private String versionApp;
    @Value("${tipo.envio}")
    private String tipoEnvio;

    @Value("${cambio.id.hardware.clv}")
    private String cveCambioIdHardware;
    @Value("${obs.cambio.id.hardware.asp.pago}")
    private String obsCambioIdHardwareAspPago;

    public RespuestaDTO enviarCodigoTelefono(EnviarCodigoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String base32Secret = obtenerSemillaToken();
            if (StringUtils.isBlank(base32Secret)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.ERROR_SEMILLA_TOKEN,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.ERROR_SEMILLA_TOKEN));
            }

            String token = generarToken(base32Secret);
            AESSFC aesSFC = inicializarAES();
            log.info("token: {}", token);
            String tokenEncriptado = aesSFC.encriptar(token);
            log.info("Token encriptado: {}", tokenEncriptado);

            RespuestaDTO idTokenResponse = solicitanteDAO.insertarTokenMensaje(tokenEncriptado);
            BigInteger idToken = gson.fromJson(idTokenResponse.getData(), BigInteger.class);

            if (idToken == null || idToken.compareTo(BigInteger.ZERO) <= 0) {
                respuesta.setCodigo(2);
                respuesta.setMensaje("Error al insertar el token en la base de datos");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.ERROR_REGISTRA_TOKEN,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.ERROR_REGISTRA_TOKEN));
            }

            String mensaje = "";
            if (req.isEvaluaProveedor()) {
                if (tipoEnvio.equalsIgnoreCase("sms")) {
                    mensaje = "Código de verificación: " + token;
                    log.info("Se enviara SMS de {} a {}", "Código de verificación: ", req.getTelefono());
                    msEnviarSMS.enviarSMS(req.getTelefono(), mensaje, "1", "Codigo de verificacion", idToken, enviaSMSService);
                } else if (tipoEnvio.equalsIgnoreCase("whatsapp")) {
                    mensaje = token;
                    log.info("Se enviara Whatsapp de {} a {}", "Código de verificación: ", req.getTelefono());
                    msEnviarSMS.enviarSMS(req.getTelefono(), mensaje, "1", "Codigo de verificacion whatsapp", idToken, enviaWhatsappService);
                }
            }else{
                mensaje = "Código de verificación: " + token;
                log.info("Se enviara SMS de {} a {}", "Código de verificación: ", req.getTelefono());
                msEnviarSMS.enviarSMS(req.getTelefono(), mensaje, "1", "Codigo de verificacion", idToken, enviaSMSService);
            }

            RespuestaUtils.respuestaExito(respuesta, 0, "Código enviado.");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO reenviarCodigoCorreo(EnviarCodigoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (req.getCodigo().isEmpty()) {
                return RespuestaUtils.asignarError(respuesta,
                        2,
                        "No se especificó el código a reenviar");
            }

            if (req.getTelefono().isEmpty()) {
                return RespuestaUtils.asignarError(respuesta,
                        2,
                        "Debe de especificar el teléfono del cliente");
            }

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());

            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresTokenHuella.SOLICITANTE_NOT_FOUND,
                        ErroresTokenHuella.getMensajeError(ErroresTokenHuella.SOLICITANTE_NOT_FOUND, req.getTelefono()));
            }

            String mensaje = "Código de verificación: " + req.getCodigo();
            msEnviarEmail.enviarCorreo(solicitanteOBJ.getCorreo(), "Codigo de verificacion", mensaje);

            RespuestaUtils.respuestaExito(respuesta, 0, "Código reenviado por correo.");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarToken(String telefono, String tokenEncriptado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Token2FAOBJ token2FAOBJ = solicitanteDAO.obtenerToken(telefono, tokenEncriptado);
            if (token2FAOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.TOKEN_NOT_FOUND,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.TOKEN_NOT_FOUND));
            }

            if (token2FAOBJ.getStatus() == 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.TOKEN_UTILIZADO,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.TOKEN_UTILIZADO));
            }
            if (token2FAOBJ.getMinutos() >= 5) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.TOKEN_EXPIRADO,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.TOKEN_EXPIRADO));
            }

            if (respuesta.getCodigo() != 0)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.ERROR_ACTUALIZAR_TOKEN,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.ERROR_ACTUALIZAR_TOKEN));

            solicitanteDAO.actualizaToken(token2FAOBJ.getId_codigo());

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validarTelefono(ValidaTelefonoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            AESSFC aesSFC = inicializarAES();
            String tokenEncriptado = aesSFC.encriptar(req.getToken());
            log.info("Token encriptado: {}", tokenEncriptado);

            switch (req.getValidaCuenta()) {
                case 0:
                    //validacion de telefono default
                    respuesta = validaBandera(conceptoValidaSMSDefault, req.getTelefono(), tokenEncriptado);
                    if (respuesta.getCodigo() != 0)
                        return respuesta;
                    return RespuestaUtils.respuestaExito(respuesta, 0, telefonoValidado);
                case 1:
                    //validacion de telefono onboarding
                    respuesta = validaBandera(conceptoValidaSMSOnb, req.getTelefono(), tokenEncriptado);
                    if (respuesta.getCodigo() != 0)
                        return respuesta;
                    respuesta = procesarValidacionOnb(req);
                    break;
                case 2:
                    //validacion de dispositivo
                    respuesta = validaBandera(conceptoValidaSMSIdHardware, req.getTelefono(), tokenEncriptado);
                    if (respuesta.getCodigo() != 0)
                        return respuesta;

                    respuesta = procesarValidacionDispositivo(req, tokenEncriptado);
                    if(respuesta.getCodigo() == 0){
                        //llamar asp-pago-jwt
                        msRevocaJwtTokenCliente.revocaJwtTokenCliente(req.getTelefono());
                    }
                    break;
                default:
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidacionTelefono.PARAM_NOT_FOUND,
                            ErroresValidacionTelefono.getMensajeError(ErroresValidacionTelefono.PARAM_NOT_FOUND,
                                    String.valueOf(req.getValidaCuenta())));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validaBandera(String conceptoValidaSMS, String telefono, String tokenEncriptado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ConfiguracionASPPagoOBJ confAspPagoOBJ = plasticosDAO.obtenerValorConfAspPagoByConcepto(conceptoValidaSMS);
            log.info(conceptoValidaSMS + ":: " + confAspPagoOBJ.getValor());

            if (!"DISABLED".equals(confAspPagoOBJ.getValor()) && !"7221823431".equals(telefono)) {
                respuesta = procesarToken(telefono, tokenEncriptado);
                if (respuesta.getCodigo() != 0) {
                    return respuesta;
                }
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarValidacionOnb(ValidaTelefonoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String personaIdCurp = "";
        String personaIdTelefonoCoDi;
        try {
            //Se valida si el curp enviado existe en BD
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.buscarSolicitanteByCurp(req.getCurp());
            if (solicitanteOBJ != null)
                personaIdCurp = solicitanteOBJ.getNumero();
            //Se valida si el telefono enviado existe en BD
            personaIdTelefonoCoDi = solicitanteDAO.buscarIdSolicitanteByTelefonoCoDi(req.getTelefono());

            if (StringUtils.isBlank(personaIdCurp) && StringUtils.isBlank(personaIdTelefonoCoDi))
                return RespuestaUtils.respuestaExito(respuesta, 0, telefonoValidado);

            // Caso 1: Tiene teléfono pero no CURP
            if (!StringUtils.isBlank(personaIdTelefonoCoDi) && StringUtils.isBlank(personaIdCurp)) {
                return validarTelefonoSinCurp(personaIdTelefonoCoDi);
            }
            // Caso 2: Tiene CURP pero no teléfono
            if (StringUtils.isBlank(personaIdTelefonoCoDi) && !StringUtils.isBlank(personaIdCurp)) {
                return validarCurpSinTelefono(personaIdCurp);
            }
            // Caso 3: Tiene teléfono y CURP
            if (!StringUtils.isBlank(personaIdTelefonoCoDi) && !StringUtils.isBlank(personaIdCurp)) {
                return validarTelefonoYCurp(personaIdTelefonoCoDi, personaIdCurp);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarValidacionDispositivo(ValidaTelefonoReq req, String tokenEncriptado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String personaIdTelefonoCoDi;
        try {
            personaIdTelefonoCoDi = solicitanteDAO.buscarIdSolicitanteByTelefonoCoDi(req.getTelefono());
            if (StringUtils.isBlank(personaIdTelefonoCoDi)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.SOLICITANTE_NOT_FOUND,
                        ErroresValidacionTelefono.getMensajeError(ErroresValidacionTelefono.SOLICITANTE_NOT_FOUND, req.getTelefono()));
            }
            //Se obtiene el registro CoDi mediante el solicitante id
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(personaIdTelefonoCoDi);
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            respuesta = codiDAO.actualizarIdHardwareBySolicitanteId(req.getIdDispositivo(), regCoDiOBJ.getSolicitanteId());
            if (respuesta.getCodigo() == 0) {
                String obs = String.format("%s | TokenCif: %s", obsCambioIdHardwareAspPago, tokenEncriptado);
                //registrar en bitacora la actualizacion del id hardware
                solicitanteDAO.registrarBitacoraCambio(regCoDiOBJ.getSolicitanteId(), cveCambioIdHardware, 9, regCoDiOBJ.getIdHardware(), req.getIdDispositivo(), obs);
                return RespuestaUtils.respuestaExito(respuesta, 0, telefonoValidado);
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.ERROR_ACTUALIZA_DISP,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.ERROR_ACTUALIZA_DISP));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    // Caso 1: Tiene teléfono pero no CURP
    private RespuestaDTO validarTelefonoSinCurp(String personaIdTelefonoCoDi) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("Encontro Id mediante el telefono {} pero no curp", personaIdTelefonoCoDi);

            RespuestaUtils.asignarError(respuesta,
                    ErroresValidacionTelefono.TELEFONO_FOUND_CURP_NOT_FOUND,
                    ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.TELEFONO_FOUND_CURP_NOT_FOUND));

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(personaIdTelefonoCoDi);
            if (regCoDiOBJ != null && versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                log.info("Cliente en la nueva version de la app");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.CURP_NUEVA_VERSION,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.CURP_NUEVA_VERSION));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    // Caso 2: Tiene CURP pero no teléfono
    private RespuestaDTO validarCurpSinTelefono(String personaIdCurp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("Encontro Id mediante el curp {} pero no telefono", personaIdCurp);

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(personaIdCurp);
            if (solicitanteOBJ != null) {
                //validamos si el curp tiene un telefono ligado para ASP Pago (Cat 7)
                if (!StringUtils.isBlank(solicitanteOBJ.getTelefonoCoDi())) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidacionTelefono.CURP_FOUND_TELEFONO_NOT_FOUND,
                            ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.CURP_FOUND_TELEFONO_NOT_FOUND));
                }
            }

            respuesta = obtenerCuentaYEstado(personaIdCurp);
            if (respuesta.getCodigo() == 0) {
                CuentaOBJ cuentaOBJ = gson.fromJson(respuesta.getData(), CuentaOBJ.class);
                log.info("Existe curp y tiene cuenta con estatus {}, se dejara pasar para actualización de datos", cuentaOBJ.getEstatus());
            } else {
                log.info("Existe curp y no tiene cuenta ni teléfono registrado, se dejara pasar para onboarding completo");
                return RespuestaUtils.respuestaExito(respuesta, 0, telefonoValidado);
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(personaIdCurp);
            if (regCoDiOBJ != null && versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                log.info("Cliente en la nueva version de la app");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.CURP_NUEVA_VERSION,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.CURP_NUEVA_VERSION));
            }

            return RespuestaUtils.respuestaExito(respuesta, 12, telefonoValidado);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    // Caso 3: Tiene teléfono y CURP
    private RespuestaDTO validarTelefonoYCurp(String personaIdTelefonoCoDi, String personaIdCurp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("Encontro Id mediante curp {} y teléfono {}", personaIdCurp, personaIdTelefonoCoDi);

            if (!personaIdTelefonoCoDi.equals(personaIdCurp)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.TELEFONO_DIF_CURP,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.TELEFONO_DIF_CURP));
            }

            respuesta = obtenerCuentaYEstado(personaIdCurp);
            if (respuesta.getCodigo() == 0) {
                CuentaOBJ cuentaOBJ = gson.fromJson(respuesta.getData(), CuentaOBJ.class);
                log.info("Existe curp, telefono, y tiene cuenta con estatus {}, se dejara pasar para actualizacion de datos", cuentaOBJ.getEstatus());
            } else {
                log.info("Existe curp y telefono registrado,no tiene cuenta, se dejara pasar para onboarding completo");
                return RespuestaUtils.respuestaExito(respuesta, 0, telefonoValidado);
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(personaIdCurp);
            if (regCoDiOBJ != null && versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                log.info("Cliente en la nueva version de la app");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionTelefono.CURP_NUEVA_VERSION,
                        ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.CURP_NUEVA_VERSION));
            }

            return RespuestaUtils.respuestaExito(respuesta, 12, telefonoValidado);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO obtenerCuentaYEstado(String personaId) {
        RespuestaDTO respuesta;
        respuesta = tieneCuentaProcrea(personaId);
        if (respuesta.getCodigo() != 0) {
            respuesta = tieneCuentaCero(personaId);
        }
        return respuesta;
    }

    public RespuestaDTO tieneCuentaProcrea(String personaId) {
        RespuestaDTO respuesta = obtenerCuenta(personaId, prodcutoIdMiAhorro, ahorroString);
        return respuesta.getCodigo() != 0 ? tieneCtaFacilProcrea(personaId) : respuesta;
    }

    public RespuestaDTO tieneCtaFacilProcrea(String personaId) {
        return obtenerCuenta(personaId, prodcutoIdCtaFacil, ahorroString);
    }

    public RespuestaDTO tieneCuentaCero(String personaId) {
        return obtenerCuenta(personaId, prodcutoIdCtaFacil, "DEBITO");
    }

    public RespuestaDTO obtenerCuenta(String personaId, Integer productoId, String tipoCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<CuentaOBJ> cuentasList;
        try {
            if (tipoCuenta.equalsIgnoreCase(ahorroString))
                cuentasList = procreaDAO.obtenerListadoCuentasProcreaByPersonaId(personaId, productoId);
            else
                cuentasList = ahorroDAO.obtenerListadoCuentasCeroByPersonaId(personaId, productoId);
            if (cuentasList != null && !cuentasList.isEmpty()) {
                CuentaOBJ cuentaOBJ = cuentasList.getFirst();
                log.info("### Tiene cuenta de producto {}", productoId);
                log.info("---> cuenta:: {}", gson.toJson(cuentaOBJ));
                return RespuestaUtils.respuestaExito(respuesta, gson.toJson(cuentaOBJ));
            } else {
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

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresValidacionTelefono.REGISTRO_CODI_NOT_FOUND,
                    ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresValidacionTelefono.VERSION_APP,
                    ErroresValidacionTelefono.descError.get(ErroresValidacionTelefono.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private String obtenerSemillaToken() {
        RespuestaDTO fechaHoraResponse = solicitanteDAO.obtenerFechaHora();
        String fechaHora = fechaHoraResponse.getData();
        if (StringUtils.isBlank(fechaHora)) {
            return "";
        }

        return fechaHora.replace("-", "")
                .replace(":", "")
                .replace(" ", "")
                .replace(".", "")
                .substring(0, 16);
    }

    private String generarToken(String base32Secret) throws GeneralSecurityException {
        Token codigo2FA = new Token();
        return codigo2FA.generateCurrentNumber(base32Secret);
    }

    private AESSFC inicializarAES() {
        AESSFC aesSFC = new AESSFC(env);
        aesSFC.addKey(env.getProperty("aes.key.token", String.class));
        return aesSFC;
    }
}