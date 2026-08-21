package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.ConceptoCuentaOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Log4j2
@Repository
public class ConceptosCuentaDAO {
    private final Properties queriesAhorro;
    private final Properties queriesProcrea;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final Environment env;
    private final ErrorHandler errorHandler;

    @Autowired
    public ConceptosCuentaDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            Environment env,
            ErrorHandler errorHandler
    ) {
        this.queriesAhorro = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "conceptos.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.env = env;
        this.errorHandler = errorHandler;
    }

    public List<ConceptoCuentaOBJ> obtenerListadoConceptosCuentaSimplificadaCeroByCuentaAh(String personaId, String cuentaah, List<String> claves) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<ConceptoCuentaOBJ> conceptosList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("claves", claves);

            String query = "ahcuentas_ahorro_datos.obtenerListadoConceptosCuentaSimplificadaCeroByCuentaAh";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConceptoCuentaOBJ conceptoCuentaOBJ = new ConceptoCuentaOBJ();
                    conceptoCuentaOBJ.setId(((Integer) row.get("id")));
                    conceptoCuentaOBJ.setClave((String) row.get("clave"));
                    conceptosList.add(conceptoCuentaOBJ);
                }
            } else {
                log.warn("No se encontraron conceptos (cero) de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return conceptosList.isEmpty() ? Collections.emptyList() : conceptosList;
    }

    public List<ConceptoCuentaOBJ> obtenerListadoConceptosCuentaSimplificadaProcreaByCuentaAh(String personaId, String cuentaah, List<String> claves) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<ConceptoCuentaOBJ> conceptosList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("claves", claves);

            String query = "ahorro_conceptos.obtenerListadoConceptosCuentaSimplificadaProcreaByCuentaAh";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConceptoCuentaOBJ conceptoCuentaOBJ = new ConceptoCuentaOBJ();
                    conceptoCuentaOBJ.setId(((Integer) row.get("id")));
                    conceptoCuentaOBJ.setClave((String) row.get("clave"));
                    conceptosList.add(conceptoCuentaOBJ);
                }
            } else {
                log.warn("No se encontraron conceptos (procrea) de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return conceptosList.isEmpty() ? Collections.emptyList() : conceptosList;
    }

    public RespuestaDTO validarListadoConceptosProductoCuentaByCuenta(String cuentaah, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("productoId", productoId);

            String query = "ahcuentas_ahorro_datos.obtenerListadoConceptosProductoCuentaByCuenta";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.warn("No se encontraron conceptos del producto {} de la cuenta {}", productoId, cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron conceptos del producto en la cuenta " + cuentaah);
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
    public RespuestaDTO guardarConceptoCuentaCero(String clave, String valor, String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("clave", clave);
            parameters.addValue("valor", valor);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_ahorro_datos.guardarConceptoCuenta";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar concepto cuenta Cero con clave {}", clave);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar concepto cuenta Cero con clave " + clave);
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
    public RespuestaDTO guardarConceptoCuentaCeroById(Integer id, String valor, String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("valor", valor);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_ahorro_datos.guardarConceptoCuentaById";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar concepto cuenta Cero con id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar concepto cuenta Cero con id " + id);
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
    public RespuestaDTO actualizarConceptoCuentaCeroById(Integer id, String valor) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("valor", valor);

            String query = "ahcuentas_ahorro_datos.actualizarValorConceptoCuentaById";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar concepto cuenta Cero con id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar concepto cuenta Cero con id " + id);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO guardarConceptoCuentaProcrea(String clave, String valor, String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("clave", clave);
            parameters.addValue("valor", valor);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_conceptos.guardarConceptoCuenta";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar concepto cuenta Procrea con clave {}", clave);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar concepto cuenta Procrea con clave " + clave);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO actualizarConceptoCuentaProcreaById(Integer id, String valor) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("valor", valor);

            String query = "ahorro_conceptos.actualizarValorConceptoCuentaById";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar concepto cuenta Procrea con id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar concepto cuenta Procrea con id " + id);
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
