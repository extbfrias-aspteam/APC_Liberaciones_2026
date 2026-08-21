package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ConceptoCuentaOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final String valorString = "valor";
    private final String claveString = "clave";
    private final String cuentaString = "cuentaah";
    private final String ejecutandoLogMessage = "Ejecutando consulta: {}, parametros: {}";

    public ConceptosCuentaDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesAhorro = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "conceptos.yml");
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }

    public ConceptoCuentaOBJ obtenerConceptoCuentaCeroByClave(String clave, String personaId, String cuentaah) {
        ConceptoCuentaOBJ conceptoCuentaOBJOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(claveString, clave);
            parameters.addValue("personaId", personaId);
            parameters.addValue(cuentaString, cuentaah);

            String query = "ahcuentas_ahorro_datos.obtenerConceptoCuentaByClave";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                conceptoCuentaOBJOBJ = new ConceptoCuentaOBJ();

                conceptoCuentaOBJOBJ.setId(((Integer) row.get("id")));
                conceptoCuentaOBJOBJ.setValor((String) row.get(valorString));
            } else {
                log.error("No se encontro concepto cuenta Cero con clave {} para el solicitante {} con cuenta {}", clave, personaId, cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            conceptoCuentaOBJOBJ = null;
        }
        return conceptoCuentaOBJOBJ;
    }

    public ConceptoCuentaOBJ obtenerConceptoCuentaProcreaByClave(String clave, String personaId, String cuentaah) {
        ConceptoCuentaOBJ conceptoCuentaOBJOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(claveString, clave);
            parameters.addValue("personaId", personaId);
            parameters.addValue(cuentaString, cuentaah);

            String query = "ahorro_conceptos.obtenerConceptoCuentaByClave";
            String psql = queriesProcrea.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                conceptoCuentaOBJOBJ = new ConceptoCuentaOBJ();

                conceptoCuentaOBJOBJ.setId(((Integer) row.get("id")));
                conceptoCuentaOBJOBJ.setValor((String) row.get(valorString));
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
            parameters.addValue(claveString, clave);
            parameters.addValue(valorString, valor);
            parameters.addValue(cuentaString, cuentaah);

            String query = "ahcuentas_ahorro_datos.guardarConceptoCuenta";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

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
            parameters.addValue(valorString, valor);

            String query = "ahcuentas_ahorro_datos.actualizarValorConceptoCuentaById";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

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

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarValorConceptoCuentaCeroByPersonaId(String personaId, String clave, String valor) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("clave", clave);
            parameters.addValue("valor", valor);

            String query = "ahcuentas_ahorro_datos.actualizarValorConceptoCuentaCeroByPersonaId";
            String psql = queriesAhorro.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar concepto cuenta Cero");
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
            parameters.addValue(claveString, clave);
            parameters.addValue(valorString, valor);
            parameters.addValue(cuentaString, cuentaah);

            String query = "ahorro_conceptos.guardarConceptoCuenta";
            String psql = queriesProcrea.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

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
            parameters.addValue(valorString, valor);

            String query = "ahorro_conceptos.actualizarValorConceptoCuentaById";
            String psql = queriesProcrea.getProperty(query);
            log.info(ejecutandoLogMessage, query, parameters.getValues());

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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO actualizarValorConceptoCuentaProcreaByPersonaId(String personaId, String clave, String valor) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("clave", clave);
            parameters.addValue("valor", valor);

            String query = "ahorro_conceptos.actualizarValorConceptoCuentaProcreaByPersonaId";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar concepto cuenta Procrea");
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
