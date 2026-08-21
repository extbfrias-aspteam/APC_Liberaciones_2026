package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.CuentaOBJ;
import mx.net.asp.asp_pago_onb.model.ValidacionesNubariumOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Log4j2
@Repository
public class ProcreaDAO {
    private final Properties queries;
    private final Properties queriesAhorroContrato;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public ProcreaDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.queriesAhorroContrato = QueryLoader.loadYaml("procrea", "ahorrocontrato.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerInflacionVigente() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        try {
            String query = "inflacion.obtenerInflacionVigente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);
            
            rows = procreaJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(row.get("inflacion")));
            } else {
                log.error("No se encontro inflacion vigente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró inflacion vigente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarValidacionNubarium(ValidacionesNubariumOBJ validaciones) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters;
        int rows;
        try {
            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("tipo_consulta", validaciones.getTipoConsulta());
            parameters.addValue("solicitante", validaciones.getSolicitante());
            parameters.addValue("dato", validaciones.getDato());
            parameters.addValue("resultado", validaciones.getResultado());
            parameters.addValue("porcentaje_coincidencia", validaciones.getPorcentajeCoincidencia());
            parameters.addValue("codigo_validacion", validaciones.getCodigoValidacion());
            
            String query = "validaciones_nubarium.nuevaValidacionNubarium";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
            
            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar la validacion de nubarium del solicitante {}", validaciones.getSolicitante());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la validacion de nubarium");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public List<CuentaOBJ> obtenerListadoCuentasProcreaByPersonaId(String personaId, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("productoId", productoId);

            String query = "ahorro_contrato.obtenerListadoCuentasProcreaByPersonaId";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CuentaOBJ cuentaOBJ = new CuentaOBJ();
                    cuentaOBJ.setCuenta((String) row.get("cuenta"));
                    cuentaOBJ.setEstatus((String) row.get("estatus"));
                    cuentaOBJ.setClabe((String) row.get("cuenta_clabe"));
                    cuentaOBJ.setProductoId(productoId);
                    cuentaOBJ.setTieneCuenta(true);
                    cuentaOBJ.setTipoCuenta("AHORRO");
                    cuentasList.add(cuentaOBJ);
                }
            } else {
                log.warn("No se encontro listado de cuentas de producto {} (procrea)", productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
    }

    public List<CuentaOBJ> listarCuentasProcreaMovimientoReciente(List<String> cuentas, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("cuentas", cuentas);

            String query = "ahorro_contrato.listarCuentasProcreaMovimientoReciente";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CuentaOBJ cuentaOBJ = new CuentaOBJ();
                    cuentaOBJ.setCuenta((String) row.get("cuenta"));
                    cuentaOBJ.setEstatus((String) row.get("estatus"));
                    cuentaOBJ.setClabe((String) row.get("cuenta_clabe"));
                    cuentaOBJ.setProductoId(productoId);
                    cuentaOBJ.setTieneCuenta(true);
                    cuentaOBJ.setTipoCuenta("AHORRO");
                    cuentasList.add(cuentaOBJ);
                }
            } else {
                log.error("No se logro enlistar las cuentas proporcionadas por movimiento mas reciente {} (procrea)", cuentas);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
    }

    public Boolean validaExisteCuentaInversion(String cuenta) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("cuenta", cuenta);
            String query = "ahorro_contrato.validaExisteCuentaInversion";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
            Integer count = namedProcreaJdbc.queryForObject(psql, parameters, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return false;
    }
}
