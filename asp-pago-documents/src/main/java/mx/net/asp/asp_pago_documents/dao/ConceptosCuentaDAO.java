package mx.net.asp.asp_pago_documents.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.ConceptoCuentaOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public ConceptoCuentaOBJ obtenerConceptoCuentaCeroByClave(String clave, String personaId, String cuentaah) {
        ConceptoCuentaOBJ conceptoCuentaOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clave", clave);
            parameters.addValue("personaId", personaId);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_ahorro_datos.obtenerConceptoCuentaByClave";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                conceptoCuentaOBJ = new ConceptoCuentaOBJ();

                conceptoCuentaOBJ.setId(((Integer) row.get("id")));
                conceptoCuentaOBJ.setValor((String) row.get("valor"));
            } else {
                log.error("No se encontro concepto cuenta Cero con clave {} para el solicitante {} con cuenta {}", clave, personaId, cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            conceptoCuentaOBJ = null;
        }
        return conceptoCuentaOBJ;
    }

    public ConceptoCuentaOBJ obtenerConceptoCuentaProcreaByClave(String clave, String personaId, String cuentaah) {
        ConceptoCuentaOBJ conceptoCuentaOBJOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clave", clave);
            parameters.addValue("personaId", personaId);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_conceptos.obtenerConceptoCuentaByClave";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                conceptoCuentaOBJOBJ = new ConceptoCuentaOBJ();

                conceptoCuentaOBJOBJ.setId(((Integer) row.get("id")));
                conceptoCuentaOBJOBJ.setValor((String) row.get("valor"));
            } else {
                log.error("No se encontro concepto cuenta Procrea con clave {} para el solicitante {} con cuenta {}", clave, personaId, cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            conceptoCuentaOBJOBJ = null;
        }
        return conceptoCuentaOBJOBJ;
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
        Gson gson = new Gson();
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
