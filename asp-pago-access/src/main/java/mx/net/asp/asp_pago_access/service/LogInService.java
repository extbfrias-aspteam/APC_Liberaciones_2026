package mx.net.asp.asp_pago_access.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.ApiException;
import mx.net.asp.asp_pago_access.dao.*;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.*;
import mx.net.asp.asp_pago_access.request.CierraSesionReq;
import mx.net.asp.asp_pago_access.request.LogInReq;
import mx.net.asp.asp_pago_access.response.LoginResp;
import mx.net.asp.asp_pago_access.utilerias.CifradoUtil;
import mx.net.asp.asp_pago_access.utilerias.DatosInvalidosException;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresCierraSesion;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresLogIn;
import mx.net.asp.asp_pago_access.ws.asp.MSObtenerCuentasVigentes;
import mx.net.asp.asp_pago_access.ws.asp.response.CuentaVigenteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class LogInService {

    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ProcreaDAO procreaDAO;
    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final SeguridadDAO seguridadDAO;
    private final PlasticosDAO plasticosDAO;
    private final AhorroDAO ahorroDAO;
    private final IzelStiDAO izelStiDAO;
    private final Gson gson;
    private final CifradoUtil cifUtil;
    private final VerificacionService verificacionService;
    private final ErrorHandler errorHandler;

    private final MSObtenerCuentasVigentes msObtenerCuentasVigentes;

    private final Environment env;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO logIn(LogInReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        LoginResp loginResp = new LoginResp();
        SesionLoginOBJ sesionLogin;
        String idcCoDiPublico;
        String idcCoDiPrivado;
        String keySourceASP;
        String dataDecripted;
        try {
            log.info("### Telefono Login: {}", req.getTelefono());
            if (req.getTelefono() == null || !req.getTelefono().matches("[0-9]{10}"))
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.FORMATO_TELEFONO,
                        ErroresLogIn.descError.get(ErroresLogIn.FORMATO_TELEFONO));

            //Se busca el solicitante mediante el telefono registrado para ASP Pago
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.SOLICITANTE_NOT_FOUND,
                        ErroresLogIn.getMensajeError(ErroresLogIn.SOLICITANTE_NOT_FOUND, req.getTelefono()));
            }

            log.info("### Solicitante Login: {}", solicitanteOBJ.getNumero());
            //Se obtiene el registro CoDi mediante el solicitante id
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            String dataCif = req.getData();
            Map<String, String> data;

            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(regCoDiOBJ.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.CUENTA_NOT_FOUND,
                        ErroresLogIn.descError.get(ErroresLogIn.CUENTA_NOT_FOUND));
            }

            log.info("tipoCuentaAh:: {}", posGlobCuentaObj.getTipoCuenta());
            data = desencriptarLogin(dataCif, posGlobCuentaObj, regCoDiOBJ, req.getTelefono());
            if (data == null || data.isEmpty()) {
                verificacionService.procesaVerificacion(regCoDiOBJ, false, env.getProperty("origen.verificacion.login", Integer.class));
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.CREDENCIALES_INCORRECTAS,
                        ErroresLogIn.descError.get(ErroresLogIn.CREDENCIALES_INCORRECTAS));
            }

            dataDecripted = data.get("data");
            keySourceASP = data.get("keysource");
            LogInDataCifOBJ loginData = gson.fromJson(dataDecripted, LogInDataCifOBJ.class);
            log.info("dataDecripted:: {}", dataDecripted);

            validarLoginData(loginData);

            log.info("### bd dispositivoId: {}", regCoDiOBJ.getIdHardware());
            log.info("### req dispositivoId: {}", loginData.getDispositivoId());
            if (regCoDiOBJ.getIdHardware() == null || regCoDiOBJ.getIdHardware().trim().isEmpty()) {
                respuesta = codiDAO.actualizarIdHardwareBySolicitanteId(loginData.getDispositivoId(), regCoDiOBJ.getSolicitanteId());
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresLogIn.ERROR_ACTUALIZA_ID_HARDWARE,
                            ErroresLogIn.descError.get(ErroresLogIn.ERROR_ACTUALIZA_ID_HARDWARE));
                }
                //registrar bitacora cambio id hardware

                regCoDiOBJ.setIdHardware(loginData.getDispositivoId());
            }
            if (!regCoDiOBJ.getIdHardware().equals(loginData.getDispositivoId())) {
                //Validar si Maanyuba esta esperando codigo -4
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.ACTUALIZA_ID_HARDWARE,
                        ErroresLogIn.descError.get(ErroresLogIn.ACTUALIZA_ID_HARDWARE));
            }

            sesionLogin = seguridadDAO.obtenerSesionActivaAspPagoByPersonaId(regCoDiOBJ.getSolicitanteId());
            if (sesionLogin == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.ERROR_SESION_ACTIVA,
                        ErroresLogIn.descError.get(ErroresLogIn.ERROR_SESION_ACTIVA));
            }

            respuesta = procesarSesionLogin(sesionLogin, loginData, regCoDiOBJ.getSolicitanteId(), loginResp);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            //Se comienza a llenar el objeto del response
            if (req.getIdCanal() == 1) {
                idcCoDiPublico = env.getProperty("id.publico.cert.android", String.class);
                idcCoDiPrivado = env.getProperty("id.privado.cert.android", String.class);
            } else {
                idcCoDiPublico = env.getProperty("id.publico.cert.iOS", String.class);
                idcCoDiPrivado = env.getProperty("id.privado.cert.iOS", String.class);
            }

            loginResp.setIdCPU(idcCoDiPublico);
            loginResp.setIdCPV(idcCoDiPrivado);

            //obtener todas las cuentas del cliente tanto de procrea y cero producto 2 y 6 vigentes
            List<String> cuentasVigentes = obtenerCuentasVigentes(regCoDiOBJ.getSolicitanteId());
            log.info("Cuentas vigentes: {}", cuentasVigentes);
            if (cuentasVigentes != null && !cuentasVigentes.isEmpty())
                loginResp.setCuentas(cuentasVigentes);
            else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.CLIENTE_SIN_CUENTAS,
                        ErroresLogIn.descError.get(ErroresLogIn.CLIENTE_SIN_CUENTAS));
            }

            loginResp.setCorreo(solicitanteOBJ.getCorreo());
            loginResp.setNombre(solicitanteOBJ.getNombre());
            loginResp.setTelefono(solicitanteOBJ.getTelefonoCoDi());
            loginResp.setId(solicitanteOBJ.getNumero());

            log.info("Se procede a cargar datos adicionales");
            cargarDatosAdicionales(loginResp);

            log.info("Se procede a cargar procesaVerificacion");
            verificacionService.procesaVerificacion(regCoDiOBJ, true, env.getProperty("origen.verificacion.login", Integer.class));

            //log.info("### Datos LogIn sin cifrar: " + gson.toJson(loginResp));
            String loginRespStrCif = cifUtil.encriptaInformacionB64(keySourceASP, gson.toJson(loginResp));

            log.info("Finaliza el proceso");
            RespuestaUtils.respuestaExito(respuesta, loginRespStrCif);
        } catch (DatosInvalidosException | ApiException ex) {
            throw ex;  // Relanzar para que lo capture el @ExceptionHandler
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

            if (!regCoDiOBJ.getTelefono_validado().equals("SI")) {
                log.info("*** Telefono NO validado");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.TELEFONO_NO_VALIDADO,
                        ErroresLogIn.descError.get(ErroresLogIn.TELEFONO_NO_VALIDADO));
            }

            if (regCoDiOBJ.getBloqueado() == 1) {
                log.info("*** Registro Bloqueado");
                return RespuestaUtils.asignarError(new RespuestaDTO(),
                        ErroresLogIn.REG_CODI_BLOQUEADO,
                        ErroresLogIn.descError.get(ErroresLogIn.REG_CODI_BLOQUEADO));
            }

            if (regCoDiOBJ.getBloqueosTemporales() > 0) {
                respuesta = procreaDAO.obtenerValorParametrosGeneralesByClave(
                        env.getProperty("concepto.limite.minutos.bloq.temporal", String.class));

                String limiteMinutosBloqueo = (respuesta.getCodigo() == 0)
                        ? respuesta.getData()
                        : env.getProperty("limite.minutos.bloq.temporal.def", String.class);

                log.info("#limiteMinutosBloqueo :: {}", limiteMinutosBloqueo);

                if (limiteMinutosBloqueo != null && !limiteMinutosBloqueo.isEmpty()) {
                    Long fechaActual = Instant.now().toEpochMilli();
                    Long fechaBloqueo = regCoDiOBJ.getFechaBloqueo().getTime();
                    long diferenciaMinutos = ((fechaActual - fechaBloqueo) / 1000) / 60;

                    log.info("#fechaActual :: {}", fechaActual);
                    log.info("#fechaBloqueo :: {}", fechaBloqueo);
                    log.info("#DiferenciaTiempo Minutos :: {}", diferenciaMinutos);

                    if (diferenciaMinutos < Long.parseLong(limiteMinutosBloqueo)) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresLogIn.REG_CODI_BLOQUEADO_TEMP,
                                ErroresLogIn.descError.get(ErroresLogIn.REG_CODI_BLOQUEADO_TEMP));
                    }
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO procesarSesionLogin(SesionLoginOBJ sesionLogin, LogInDataCifOBJ loginData, String solicitanteId, LoginResp loginResp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("sesionLogin:: {}", sesionLogin);
            if ("NOTFOUND".equals(sesionLogin.getEstatus())) {
                log.info("#### Se registra nueva sesión para:: {}", solicitanteId);
                seguridadDAO.guardarSesionAspPago(solicitanteId, "ACTIVA");
                seguridadDAO.registrarBitacoraSesionAspPago(solicitanteId, "ACTIVA", loginData.getLatitud(), loginData.getLongitud());
                return RespuestaUtils.respuestaExito(respuesta);
            }

            //Zona Mexico SOLO para mostrar
            ZoneId mexico = ZoneId.of("America/Mexico_City");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaFormateada = sesionLogin.getFecha_login()
                    .atZone(mexico)
                    .format(formatter);

            loginResp.setLastLogin(fechaFormateada);

            if ("ACTIVA".equals(sesionLogin.getEstatus())) {
                Instant fechaLogin = sesionLogin.getFecha_login();
                Instant fechaActual = Instant.now();

                ZonedDateTime fechaActualMx = fechaActual.atZone(mexico);
                ZonedDateTime fechaLoginMx = fechaLogin.atZone(mexico);

                log.info("*** fechaActual UTC :: {}", fechaActual);
                log.info("*** fechaLogin UTC :: {}", fechaLogin);

                log.info("*** fechaActual MX :: {}", fechaActualMx);
                log.info("*** fechaLogin MX :: {}", fechaLoginMx);

                long diferenciaMinutos = Duration.between(fechaLogin, fechaActual).toMinutes();
                long diferenciaSegundos = Duration.between(fechaLogin, fechaActual).toSeconds();

                log.info("--> Diferencia tiempo segundos :: {}", diferenciaSegundos);
                log.info("--> Diferencia tiempo minutos :: {}", diferenciaMinutos);

                respuesta = plasticosDAO.obtenerValorConfAspPagoByConcepto(env.getProperty("concepto.limite.minutos.login", String.class));
                String minutosCnf = respuesta.getCodigo() == 0
                        ? gson.fromJson(respuesta.getData(), ConfiguracionASPPagoOBJ.class).getValor()
                        : env.getProperty("limite.minutos.login.def", String.class);

                log.info("Minutos configurados:: {}", minutosCnf);

                if (fechaLogin.isAfter(fechaActual)) {
                    log.warn("+++ Fecha futura detectada en sesiones +++");
                }

                if (diferenciaMinutos >= Integer.parseInt(minutosCnf) || fechaLogin.isAfter(fechaActual)) {
                    seguridadDAO.registrarBitacoraSesionAspPago(solicitanteId, "EXPIRADA", loginData.getLatitud(), loginData.getLongitud());
                    seguridadDAO.actualizarSesionAspPago(solicitanteId, "ACTIVA", env.getProperty("usuario.sistemas", Integer.class));
                    seguridadDAO.registrarBitacoraSesionAspPago(solicitanteId, "ACTIVA", loginData.getLatitud(), loginData.getLongitud());
                } else {
                    return RespuestaUtils.asignarError(respuesta, ErroresLogIn.SESION_ACTIVA, ErroresLogIn.descError.get(ErroresLogIn.SESION_ACTIVA));
                }
            } else {
                // Caso cuando la sesión fue CERRADA
                seguridadDAO.actualizarSesionAspPago(solicitanteId, "ACTIVA", env.getProperty("usuario.sistemas", Integer.class));
                seguridadDAO.registrarBitacoraSesionAspPago(solicitanteId, "ACTIVA", loginData.getLatitud(), loginData.getLongitud());
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO procesarCuentaEstatus(PosGlobalCuentaOBJ posGlobCuentaObj, String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (!posGlobCuentaObj.getEstatus().equals("VIG")) {
                if (posGlobCuentaObj.getEstatus().equals("BLOQ")) {
                    log.info("Se encuentra bloqueada la cuenta: {}", cuenta);
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresLogIn.CUENTA_BLOQUEADA,
                            ErroresLogIn.descError.get(ErroresLogIn.CUENTA_BLOQUEADA));
                } else if (posGlobCuentaObj.getEstatus().equals("CAN")) {
                    log.info("Se encuentra cancelada la cuenta: {}", cuenta);
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresLogIn.CUENTA_CANCELADA,
                            ErroresLogIn.descError.get(ErroresLogIn.CUENTA_CANCELADA));
                } else
                    log.warn("Estatus en posicion global no reconocido {}", posGlobCuentaObj.getEstatus());
            }

            // Obtenemos el estado de la cuenta según el tipo
            String estatus = obtenerEstatusCuenta(posGlobCuentaObj.getCuentaah(), posGlobCuentaObj.getTipoCuenta());

            // Si el estatus es null, hay un error interno
            if (estatus == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }

            // Validamos si la cuenta está bloqueada o cancelada
            if ("B".equals(estatus) || "3".equals(estatus)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.CUENTA_BLOQUEADA,
                        ErroresLogIn.descError.get(ErroresLogIn.CUENTA_BLOQUEADA));
            }
            if ("C".equals(estatus) || "2".equals(estatus)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresLogIn.CUENTA_CANCELADA,
                        ErroresLogIn.descError.get(ErroresLogIn.CUENTA_CANCELADA));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }

    private String obtenerEstatusCuenta(String cuentaah, String tipoCuenta) {
        if ("AHORRO".equals(tipoCuenta)) {
            return procreaDAO.obtenerEstatusCuentaAh(cuentaah);
        } else {
            Integer estatus = ahorroDAO.obtenerEstatusCuentaAh(cuentaah);
            return estatus != null ? String.valueOf(estatus) : null;
        }
    }

    private List<String> obtenerCuentasVigentes(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<String> cuentasVigentes = new ArrayList<>();

        // Llamada servicio asp-pago-management
        respuesta = msObtenerCuentasVigentes.obtenerCuentasVigentes(personaId);
        if (respuesta.getCodigo() != 0) {
            throw new ApiException(
                    respuesta.getCodigo(),
                    respuesta.getMensaje()
            );
        }

        Type cuentasListType = new TypeToken<List<CuentaVigenteResponse>>() {
        }.getType();
        List<CuentaVigenteResponse> response = gson.fromJson(respuesta.getData(), cuentasListType);

        for (CuentaVigenteResponse cuenta : response) {
            cuentasVigentes.add(cuenta.getCuenta());
        }

        return cuentasVigentes;
    }

    private void cargarDatosAdicionales(LoginResp loginResp) {
        try {
            log.info("### Se consulta el listado de urls CoDi");
            List<UrlServiciosOBJ> serviciosCoDiList = codiDAO.obtenerListadoUrlCoDi();
            if (serviciosCoDiList != null && !serviciosCoDiList.isEmpty()) {
                loginResp.setUrlCoDi(serviciosCoDiList);
            } else {
                log.error("Ocurrio un Error al consultar el catalogo de urls CoDi");
            }

            log.info("### Se consulta el listado de Instituciones");
            List<CatInstiOBJ> institucionesList = izelStiDAO.obtenerListadoInstituciones();
            if (institucionesList != null && !institucionesList.isEmpty()) {
                loginResp.setListCatinsti(institucionesList);
            } else {
                log.error("Ocurrio un Error al consultar el catalogo Instituciones");
            }

            log.info("### Se consulta el listado de Configuraciones ASP Pago");
            List<ConfiguracionASPPagoOBJ> configuracionList = plasticosDAO.obtenerListadoConfAspPagoByConcepto();
            if (configuracionList != null && !configuracionList.isEmpty()) {
                loginResp.setConfiguracionesASPpago(configuracionList);
            } else {
                log.error("Ocurrio un Error al consultar el listado de Configuraciones ASP Pago");
            }

            log.info("### Se consultan costos de tarjetas");
            List<CostoOBJ> costosList = plasticosDAO.obtenerListadoCostosTarjetas();
            if (costosList != null && !costosList.isEmpty()) {
                loginResp.setCostos(costosList);
            } else {
                log.error("Ocurrio un Error al consultar el listado de costos de tarjetas");
            }

            log.info("### Se consulta tabla de documentacion pendiente para el cliente:: {}", loginResp.getId());
            loginResp.setSolicitarDocumentacion(seguridadDAO.existeDocPendienteAspPago(loginResp.getId()));
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }

    public RespuestaDTO cierraSesion(CierraSesionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SesionLoginOBJ sesionLogin;
        try {
            sesionLogin = seguridadDAO.obtenerSesionActivaAspPagoByPersonaId(req.getPersonaId());
            if (sesionLogin == null || "NOTFOUND".equals(sesionLogin.getEstatus())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCierraSesion.SESION_ACTIVA_NOT_FOUND,
                        ErroresCierraSesion.descError.get(ErroresCierraSesion.SESION_ACTIVA_NOT_FOUND));
            }

            if ("CERRADA".equals(sesionLogin.getEstatus()))
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCierraSesion.SESION_CERRADA,
                        ErroresCierraSesion.descError.get(ErroresCierraSesion.SESION_CERRADA));

            respuesta = seguridadDAO.actualizarSesionAspPago(req.getPersonaId(), "CERRADA", env.getProperty("usuario.sistemas", Integer.class));
            if (respuesta.getCodigo() == 0) {
                //registrar en bitacora de sesiones asp pago
                seguridadDAO.registrarBitacoraSesionAspPago(req.getPersonaId(), "CERRADA", "", "");
                return RespuestaUtils.respuestaExito(respuesta);
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCierraSesion.ERROR_CIERRA_SESION,
                        ErroresCierraSesion.descError.get(ErroresCierraSesion.ERROR_CIERRA_SESION));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private Map<String, String> desencriptarLogin(String encriptado, PosGlobalCuentaOBJ
            posGlobCuentaObj, RegistroCoDiOBJ regCoDiOBJ, String telefono) {
        String desencriptado;
        String keySource;
        String credencial;
        Map<String, String> datos = new HashMap<>();
        try {
            credencial = regCoDiOBJ.getPassword();
            keySource = cifUtil.generaKeySource(telefono, credencial);
            desencriptado = cifUtil.desencriptaInformacionB64(keySource, encriptado);
            if (!desencriptado.isEmpty()) {
                datos.put("keysource", keySource);
                datos.put("data", desencriptado);
                datos.put("credencial", "password");
                return datos;
            } else {
                log.info("No se logro desencriptar la data mediante password y telefono");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        log.info("### Se procede a desencriptar mediante biometrico");

        try {
            ConceptoCuentaOBJ conceptoCuentaOBJ;
            if (posGlobCuentaObj.getTipoCuenta().equals("AHORRO"))
                conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaProcreaByClave(env.getProperty("clave.biometrico", String.class), regCoDiOBJ.getSolicitanteId(), regCoDiOBJ.getCuenta());
            else
                conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaCeroByClave(env.getProperty("clave.biometrico", String.class), regCoDiOBJ.getSolicitanteId(), regCoDiOBJ.getCuenta());

            if (conceptoCuentaOBJ == null) {
                return datos;
            }

            credencial = conceptoCuentaOBJ.getValor();
            keySource = cifUtil.generaKeySource(telefono, credencial);
            desencriptado = cifUtil.desencriptaInformacionB64(keySource, encriptado);
            if (!desencriptado.isEmpty()) {
                datos.put("keysource", keySource);
                datos.put("data", desencriptado);
                datos.put("credencial", "token");
                return datos;
            } else {
                log.info("No se logro desencriptar la data mediante biometrico y telefono");
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return datos;
    }

    private void validarLoginData(LogInDataCifOBJ loginData) {
        List<String> errores = new ArrayList<>();
        if (!esCadenaValida(loginData.getPassword())) {
            errores.add("password");
        }
        if (!esCadenaValida(loginData.getDispositivoId())) {
            errores.add("dispositivoId");
        }

        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private boolean esCadenaValida(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

}