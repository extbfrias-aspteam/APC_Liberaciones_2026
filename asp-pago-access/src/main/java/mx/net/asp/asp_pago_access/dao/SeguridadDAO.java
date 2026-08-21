package mx.net.asp.asp_pago_access.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.*;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.TraceIdUtil;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Repository
public class SeguridadDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SeguridadDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "seguridad.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public SesionLoginOBJ obtenerSesionActivaAspPagoByPersonaId(String personaId) {
        SesionLoginOBJ sesionLogin;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "sesiones_asppago.obtenerSesionAspPagoByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                sesionLogin = new SesionLoginOBJ();

                sesionLogin.setEstatus((String) row.get("estatus"));
                sesionLogin.setFecha_login(((Timestamp) row.get("fecha_login")).toInstant());
            } else {
                log.error("No se encontro sesion ASP pago del solicitante {}", personaId);
                sesionLogin = new SesionLoginOBJ();
                sesionLogin.setEstatus("NOTFOUND");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            sesionLogin = null;
        }
        return sesionLogin;
    }

    public RegistroCodAutOBJ obtenerRegistroCodAutByPersonaId(String personaId) {
        RegistroCodAutOBJ registroCodAutOBJ;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaid", personaId);

            String query = "seregistro_cod_aut.obtenerRegistroCodAutByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                registroCodAutOBJ = new RegistroCodAutOBJ();

                // registroCodAutOBJ.setId((Integer) row.get("id"));
                // registroCodAutOBJ.setId((Long) row.get("id"));
                registroCodAutOBJ.setId(((Long) row.get("id")).intValue());
                registroCodAutOBJ.setIntentos((Integer) row.get("intentos"));
                registroCodAutOBJ.setBloqueado((Boolean) row.get("bloqueado"));
            } else {
                log.error("No se encontro registo de codigo de autorizacion del solicitante {}", personaId);
                registroCodAutOBJ = null;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            registroCodAutOBJ = null;
        }
        return registroCodAutOBJ;
    }

    public PinOTPOBJ obtenerPinOTPActivoByPersonaId(String personaId) {
        PinOTPOBJ pinOTPOBJ;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        // Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaid", personaId);

            String query = "sepin_otp.obtenerPinOTPActivoByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                pinOTPOBJ = new PinOTPOBJ();
                pinOTPOBJ.setId((Long) row.get("id"));
                pinOTPOBJ.setPersonaId((String) row.get("personaid"));
                pinOTPOBJ.setPin_otp((String) row.get("pin_otp"));
                pinOTPOBJ.setActivo((Boolean) row.get("activo"));
                pinOTPOBJ.setFechaExpira((Timestamp) row.get("fecha_expira"));
                pinOTPOBJ.setFechaCreacion((Timestamp) row.get("fecha_creacion"));
            } else {
                log.error("No se encontro pin otp activo del solicitante {}", personaId);
                pinOTPOBJ = null;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            pinOTPOBJ = null;
        }
        return pinOTPOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarIntentosCodAut(RegistroCodAutOBJ registroCodAutOBJ, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("personaid", registroCodAutOBJ.getPersonaId());
            parameters.addValue("intentos", registroCodAutOBJ.getIntentos());
            parameters.addValue("bloqueado", registroCodAutOBJ.getBloqueado());
            parameters.addValue("usuario_modificacion", usuario);
            parameters.addValue("fecha_bloqueo", Boolean.TRUE.equals(registroCodAutOBJ.getBloqueado()) ? LocalDateTime.now() : null);

            String query = "seregistro_cod_aut.actualizarRegistroCodAut";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el registro de codico de autorizacion del solicitante {}", registroCodAutOBJ.getPersonaId());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el registro de codico de autorizacion del solicitante " + registroCodAutOBJ.getPersonaId());
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO insertarRegistroCodAut(RegistroCodAutOBJ registroCodAutOBJ, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("personaid", registroCodAutOBJ.getPersonaId());
            parameters.addValue("intentos", registroCodAutOBJ.getIntentos());
            parameters.addValue("bloqueado", registroCodAutOBJ.getBloqueado());
            parameters.addValue("usuario_creacion", usuario);

            String query = "seregistro_cod_aut.insertarRegistroCodAut";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el registro de codigo de autorizacion del solicitante {}", registroCodAutOBJ.getPersonaId());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar el registro de codigo de autorizacion del solicitante " + registroCodAutOBJ.getPersonaId());
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO guardarSesionAspPago(String personaId, String estatus) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            Instant now = Instant.now();

            parameters.addValue("personaId", personaId);
            parameters.addValue("estatus", estatus);
            parameters.addValue("fechaLogin", Timestamp.from(now));

            String query = "sesiones_asppago.guardarSesionAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar la sesion de ASP Pago para el solicitante {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar la sesion de ASP Pago para el solicitante " + personaId);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
    
    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarSesionAspPago(String personaId, String estatus, Integer usaurio_modificacion) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            Instant now = Instant.now();

            parameters.addValue("personaId", personaId);
            parameters.addValue("estatus", estatus);
            parameters.addValue("usuarioMod", usaurio_modificacion);
            parameters.addValue("fechaLogin", Timestamp.from(now));

            String query = "sesiones_asppago.actualziarSesionAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar la sesion de ASP Pago");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar la sesion de ASP Pago");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
    
    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registrarBitacoraSesionAspPago(String personaId, String estatus, String latitud, String longitud) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            Instant now = Instant.now();

            parameters.addValue("personaId", personaId);
            parameters.addValue("estatus", estatus);
            parameters.addValue("latitud", latitud);
            parameters.addValue("longitud", longitud);
            parameters.addValue("fechaLogin", Timestamp.from(now));

            String query = "sesiones_asppago.registrarBitacoraSesionesAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar en la bitacora de sesiones ASP Pago para el solicitante {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar en la bitacora de sesiones ASP Pago para el solicitante " + personaId);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public List<PinOTPOBJ> obtenerListadoPinOTPActivos(String personaId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<PinOTPOBJ> pinOTPActivosList = new ArrayList<>();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "sepin_otp.obtenerPinOTPActivos";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    PinOTPOBJ pinOTPOBJ = new PinOTPOBJ();
                    pinOTPOBJ.setId((Long) row.get("id"));
                    pinOTPOBJ.setPersonaId((String) row.get("personaId"));
                    pinOTPOBJ.setPin_otp((String) row.get("pin_otp"));
                    pinOTPOBJ.setActivo((Boolean) row.get("activo"));
                    pinOTPOBJ.setFechaExpira((Timestamp) row.get("fecha_expira"));
                    pinOTPOBJ.setFechaCreacion((Timestamp) row.get("fecha_creacion"));
                    pinOTPActivosList.add(pinOTPOBJ);
                }
            } else {
                log.warn("No se encontraron pin otp activos para el solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return pinOTPActivosList.isEmpty() ? Collections.emptyList() : pinOTPActivosList;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarPinOTPById(Long id, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", id);
            parameters.addValue("activo", false);
            parameters.addValue("usuarioModificacion", usuario);

            String query = "sepin_otp.desactivarPinOtpById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro el pin otp con id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro desactivar el pin otp");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarPinOTPByPersonaId(String personaId, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("personaId", personaId);
            parameters.addValue("activo", false);
            parameters.addValue("usuarioModificacion", usuario);

            String query = "sepin_otp.desactivarPinOTPByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("Cliente sin pines otp activos");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("Cliente sin pines otp activos");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO guardarPinOTP(PinOTPOBJ pinOTPOBJ, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("personaId", pinOTPOBJ.getPersonaId());
            parameters.addValue("pinOTP", pinOTPOBJ.getPin_otp());
            parameters.addValue("activo", pinOTPOBJ.getActivo());
            parameters.addValue("fCreacion", pinOTPOBJ.getFechaCreacion());
            parameters.addValue("fExpira", pinOTPOBJ.getFechaExpira());
            parameters.addValue("usuarioCreacion", usuario);

            String query = "sepin_otp.guardarPinOTP";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el pin otp para el cliente {}", pinOTPOBJ.getPersonaId());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el token cambio pass");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public void registraUUIDSospechoso(TraceIdSospechosoOBJ traceIdSospechosoOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("uuid_txt", traceIdSospechosoOBJ.getTraceId());
            parameters.addValue("uuid_signed", traceIdSospechosoOBJ.getTraceIdSigned());
            parameters.addValue("endpoint", traceIdSospechosoOBJ.getEndpoint());
            parameters.addValue("ip_cliente", traceIdSospechosoOBJ.getIpCliente());
            parameters.addValue("observaciones", traceIdSospechosoOBJ.getObservaciones());

            String query = "seuuid_sospechosos.registraUUIDSospechoso";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro registrar el UUID sospechoso");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Long guardarOTPResetPassword(OTPPwdOBJ otpPwdOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("persona_id", otpPwdOBJ.getPersonaId());
            parameters.addValue("otp_hash", otpPwdOBJ.getOtpHash());
            parameters.addValue("device_id", otpPwdOBJ.getDeviceId());
            parameters.addValue("fecha_creacion", otpPwdOBJ.getFecha_creacion());
            parameters.addValue("fecha_expira", otpPwdOBJ.getFecha_expira());
            parameters.addValue("activo", otpPwdOBJ.getActivo());

            String query = "sepwd_otp.guardaOTPResetPassword";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedCeroJdbc.queryForObject(psql, parameters, Long.class);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return null;
        }
    }

    public PasswordResetControlOBJ obtenerPwdResetControl(String personaId, String tipoControl) {
        PasswordResetControlOBJ controlOBJ;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("persona_id", personaId);
            //parameters.addValue("deviceId", deviceId);
            parameters.addValue("tipo_control", tipoControl);

            String query = "sepassword_reset_control.obtenerPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                controlOBJ = new PasswordResetControlOBJ();
                controlOBJ.setId((Long) row.get("id"));
                controlOBJ.setPersonaId((String) row.get("persona_id"));
                //controlOBJ.setDeviceId((String) row.get("device_id"));
                controlOBJ.setTipoControl((String) row.get("tipo_control"));
                controlOBJ.setIntentos((Integer) row.get("intentos"));
                Timestamp tsInicio = (Timestamp) row.get("fecha_inicio_ventana");
                controlOBJ.setFechaInicioVentana((tsInicio.toLocalDateTime()));
                Timestamp tsBloqueo = (Timestamp) row.get("fecha_bloqueo");
                controlOBJ.setFechaBloqueo(tsBloqueo != null ? tsBloqueo.toLocalDateTime() : null);
                controlOBJ.setBloqueado((Boolean) row.get("bloqueado"));
            } else {
                log.warn("No se encontro registro de control pwd reset para el solicitante {}", personaId);
                controlOBJ = new PasswordResetControlOBJ();
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            controlOBJ = null;
        }
        return controlOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Long registraNuevoPwdResetControl(String personaId, String tipoControl) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("persona_id", personaId);
            parameters.addValue("tipo_control", tipoControl);

            String query = "sepassword_reset_control.registraNuevoPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedCeroJdbc.queryForObject(psql, parameters, Long.class);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return null;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void insertarHistoricoPwdResetControl(Long controlId, String personaId, String deviceId, String evento,
                                                 Integer intentos, String observaciones, String origen) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("id_control", controlId);
            parameters.addValue("persona_id", personaId);
            parameters.addValue("device_id", deviceId);
            parameters.addValue("evento", evento);
            parameters.addValue("intentos", intentos);
            parameters.addValue("observaciones", observaciones);
            parameters.addValue("origen", origen);

            String query = "sepassword_reset_control_historico.insertarHistoricoPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            namedCeroJdbc.update(psql, parameters);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO limpiarPwdResetControl(Long controlId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", controlId);

            String query = "sepassword_reset_control.limpiarPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro limpiar el control de reinicio de pwd del cliente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro limpiar el control de reinicio de pwd del cliente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO bloquearPwdResetControl(Long controlId, Integer intentos) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", controlId);
            parameters.addValue("intentos", intentos);

            String query = "sepassword_reset_control.bloquearPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro bloquear el control de reinicio de pwd del cliente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro bloquear el control de reinicio de pwd del cliente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO incrementarIntentosPwdResetControl(Long controlId, Integer intentos) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", controlId);
            parameters.addValue("intentos", intentos);

            String query = "sepassword_reset_control.incrementarIntentosPwdResetControl";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro incrementar los intentos en el control de reinicio de pwd del cliente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro incrementar los intentos en el control de reinicio de pwd del cliente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public OTPPwdOBJ obtenerOTPResetPassword(String personaId, String deviceId, String otp_hash) {
        OTPPwdOBJ otpPwdOBJ;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("persona_id", personaId);
            parameters.addValue("device_id", deviceId);
            parameters.addValue("otp_hash", otp_hash);

            String query = "sepwd_otp.obtenerOTPResetPassword";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                otpPwdOBJ = new OTPPwdOBJ();
                otpPwdOBJ.setId((Long) row.get("id"));
                otpPwdOBJ.setPersonaId((String) row.get("persona_id"));
                otpPwdOBJ.setDeviceId((String) row.get("device_id"));
                otpPwdOBJ.setOtpHash((String) row.get("otp_hash"));
                Timestamp tsExpira = (Timestamp) row.get("fecha_expira");
                otpPwdOBJ.setFecha_expira((tsExpira.toLocalDateTime()));
                otpPwdOBJ.setActivo((Boolean) row.get("activo"));
            } else {
                log.warn("No se encontro otp para el cliente {}", personaId);
                otpPwdOBJ = null;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            otpPwdOBJ = null;
        }
        return otpPwdOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarOTPResetPassword(Long id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", id);
            parameters.addValue("activo", false);

            String query = "sepwd_otp.desactivarOTPResetPasswrod";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro desactivar el OTP del cliente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro desactivar el OTP del cliente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }


    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarOTPActivosResetPasswordByPersonaId(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("persona_id", personaId);
            parameters.addValue("activo", false);

            String query = "sepwd_otp.desactivarOTPActivosResetPasswordByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se encontraron otps activos del cliente: {}", personaId);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarPasswordResetSession(Long id, Boolean usada) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("id", id);
            parameters.addValue("activo", false);
            parameters.addValue("usada", usada);

            String query = "sepassword_reset_session.desactivarPasswordResetSession";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro desactivar el reset session del cliente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro desactivar el reset session del cliente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO desactivarPasswordResetSessionActivasByPersonaId(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("persona_id", personaId);
            parameters.addValue("activo", false);

            String query = "sepassword_reset_session.desactivarPasswordResetSessionActivasByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se encontraron reset session activas del cliente: {}", personaId);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void guardarResetSessionCambioPwd(ResetSessionOBJ resetSessionOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("persona_id", resetSessionOBJ.getPersonaId());
            parameters.addValue("device_id", resetSessionOBJ.getDeviceId());
            parameters.addValue("fecha_creacion", resetSessionOBJ.getFecha_creacion());
            parameters.addValue("fecha_expira", resetSessionOBJ.getFecha_expira());
            parameters.addValue("activo", resetSessionOBJ.getActivo());
            parameters.addValue("id_session", resetSessionOBJ.getId_session());

            String query = "sepassword_reset_session.guardarResetSessionCambioPwd";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.error("No se logro guardar el reset session para recuperacion de contrasena para el cliente {}", resetSessionOBJ.getPersonaId());
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
        }
    }

    public ResetSessionOBJ obtenerRessetSesion(String deviceId, String session_id) {
        ResetSessionOBJ resetSessionOBJ;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("device_id", deviceId);
            parameters.addValue("id_session", session_id);

            String query = "sepassword_reset_session.obtenerRessetSesion";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                resetSessionOBJ = new ResetSessionOBJ();
                resetSessionOBJ.setId((Long) row.get("id"));
                resetSessionOBJ.setPersonaId((String) row.get("persona_id"));
                resetSessionOBJ.setId_session((String) row.get("id_session"));
                resetSessionOBJ.setDeviceId((String) row.get("device_id"));
                Timestamp tsExpira = (Timestamp) row.get("fecha_expira");
                resetSessionOBJ.setFecha_expira((tsExpira.toLocalDateTime()));
                resetSessionOBJ.setActivo((Boolean) row.get("activo"));
            } else {
                log.warn("No se encontro registro de token reset session para {}", session_id);
                resetSessionOBJ = null;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            resetSessionOBJ = null;
        }
        return resetSessionOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Long registraBitacoraCambioPassword(BitacoraCambioPasswordOBJ bitacoraOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("valor_referencia", bitacoraOBJ.getValor_referencia());
            parameters.addValue("tipo_referencia", bitacoraOBJ.getTipo_referencia());
            parameters.addValue("device_id", bitacoraOBJ.getDevice_id());
            parameters.addValue("tipo_evento", bitacoraOBJ.getTipo_evento());
            parameters.addValue("resultado", bitacoraOBJ.getResultado());
            parameters.addValue("observaciones", bitacoraOBJ.getObservaciones());
            parameters.addValue("uuid_text", TraceIdUtil.getTraceId());

            String query = "sebitacora_password_reset.registraBitacoraCambioPassword";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedCeroJdbc.queryForObject(psql, parameters, Long.class);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return null;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void actualizaBitacoraCambioPasswordValorRef(Long id, String valor, String tipo, String resultado, String observaciones) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("valor_referencia", valor);
            parameters.addValue("tipo_referencia", tipo);
            parameters.addValue("resultado", resultado);
            parameters.addValue("observaciones", observaciones);

            String query = "sebitacora_password_reset.actualizaBitacoraCambioPasswordValorRef";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void actualizaBitacoraCambioPassword(Long id, String resultado, String observaciones) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("resultado", resultado);
            parameters.addValue("observaciones", observaciones);

            String query = "sebitacora_password_reset.actualizaBitacoraCambioPassword";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
        }
    }

    public boolean existeDocPendienteAspPago(String idpersona) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("idpersona", idpersona);

            String query = "documentacion_asppago.consultaDocPendienteAspPago";
            String psql = queries.getProperty(query);

            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            List<Integer> result = namedCeroJdbc.queryForList(psql, parameters, Integer.class);

            boolean requiereDoc = !result.isEmpty();

            if (requiereDoc) {
                log.info("El cliente {} REQUIERE documentacion pendiente", idpersona);
            } else {
                log.info("El cliente {} NO requiere documentacion pendiente", idpersona);
            }

            return requiereDoc;
        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
    }

    public LocalDateTime obtenerHoraBd() {
        return ceroJdbc.queryForObject(
                "SELECT CURRENT_TIMESTAMP",
                LocalDateTime.class
        );
    }
}
