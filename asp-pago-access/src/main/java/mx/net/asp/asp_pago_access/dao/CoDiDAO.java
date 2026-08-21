package mx.net.asp.asp_pago_access.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.PropertiesInicialesOBJ;
import mx.net.asp.asp_pago_access.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_access.model.UrlServiciosOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
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
import java.util.*;

@Log4j2
@Repository
public class CoDiDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public CoDiDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RegistroCoDiOBJ consultarRegistroCoDiCliente(String personaId) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.consultarRegistroCoDiCliente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) row.get("activo"));
                regCoDiOBJ.setTelefono_validado((String) row.get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) row.get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) row.get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) row.get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) row.get("intentos"));
            } else {
                log.error("No se encontro registro codi del solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }

    public List<RegistroCoDiOBJ> obtenerListRegistroCoDiByPersonaId(String personaId) {
        List<RegistroCoDiOBJ> registros = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Se definen los parámetros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.obtenerListRegistroCoDiByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);

            for (Map<String, Object> row : rows) {
                RegistroCoDiOBJ regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) row.get("activo"));
                regCoDiOBJ.setTelefono_validado((String) row.get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) row.get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) row.get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) row.get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) row.get("intentos"));

                registros.add(regCoDiOBJ);
            }

            if (registros.isEmpty()) {
                log.warn("No se encontró registro CoDi para el solicitante: {}", personaId);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            registros = Collections.emptyList();
        }

        return registros;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarIdHardwareBySolicitanteId(String dispositivoId, String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("dispositivoId", dispositivoId);
            parameters.addValue("solicitante_id", solicitanteId);

            String query = "registro_codi.actualizarIdHardwareBySolicitanteId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar id hardware del solicitante {}", solicitanteId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar id hardware del solicitante " + solicitanteId);
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
    public RespuestaDTO actualizarBloqueoAccesoAspPago(RegistroCoDiOBJ registroCoDi) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {

            parameters.addValue("bloqueado", registroCoDi.getBloqueado());
            parameters.addValue("fbloqueo", registroCoDi.getFechaBloqueo());
            parameters.addValue("intentos", registroCoDi.getIntentos());
            parameters.addValue("btemp", registroCoDi.getBloqueosTemporales());
            parameters.addValue("solicitante_id", registroCoDi.getSolicitanteId());

            String query = "registro_codi.actualizarBloqueoAccesoAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el estatus del bloqueo de la cuenta del cliente {}", registroCoDi.getSolicitanteId());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el estatus del bloqueo de la cuenta del cliente " + registroCoDi.getSolicitanteId());
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

    public List<UrlServiciosOBJ> obtenerListadoUrlCoDi() {
        List<Map<String, Object>> rows;
        List<UrlServiciosOBJ> urlList = new ArrayList<>();
        try {
            String query = "url_codi.obtenerListadoUrlCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    UrlServiciosOBJ urlObj = new UrlServiciosOBJ();
                    urlObj.setId((Integer) row.get("id"));
                    urlObj.setClave((String) row.get("clave"));
                    urlObj.setUrl((String) row.get("url"));
                    urlList.add(urlObj);
                }
            } else {
                log.error("No se encontraron registros de urls codi");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return urlList.isEmpty() ? Collections.emptyList() : urlList;
    }

    public RespuestaDTO obtenerVersionAppActiva(Integer idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            parameters.addValue("idCanal", idCanal);
            String query = "versiones.obtenerVersionAppActiva";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("version"));
            } else {
                log.error("No se encontro version activa");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró version activa");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarPassword(String password, String personaId, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            //Se definen los parametros para el query
            parameters.addValue("password", password);
            parameters.addValue("personaId", personaId);
            parameters.addValue("usuarioModificacion", usuario);

            String query = "registro_servicios_digitales.actualizarPassword";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar la password del solicitante {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar la password");
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

    public List<PropertiesInicialesOBJ> obtenerListadoPropertiesIniciales() {
        List<Map<String, Object>> rows;
        List<PropertiesInicialesOBJ> propIniList = new ArrayList<>();
        try {
            //Se definen los parametros para el query
            String query = "valores_properties_iniciales.obtenerListadoPropertiesIniciales";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    PropertiesInicialesOBJ propIniObj = new PropertiesInicialesOBJ();
                    propIniObj.setId((Integer) row.get("id"));
                    propIniObj.setClave((String) row.get("clave"));
                    propIniObj.setValor((String) row.get("valor"));
                    propIniList.add(propIniObj);
                }
            } else {
                log.error("No se encontraron registros de datos iniciales");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return propIniList.isEmpty() ? Collections.emptyList() : propIniList;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO limpiarBloqueoRegistroCoDi(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("solicitante_id", personaId);

            String query = "registro_codi.limpiarBloqueoRegistroCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro limpiar el bloqueo del cliente {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró limpiar el bloqueo del cliente " + personaId);
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
}
