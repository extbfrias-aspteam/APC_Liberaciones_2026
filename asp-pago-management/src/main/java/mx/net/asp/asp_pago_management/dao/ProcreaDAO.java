package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.dto.CuentaDTO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.CuentaOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class ProcreaDAO {
    private final Properties queries;
    private final Properties queriesAhorroContrato;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    public ProcreaDAO(
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.queriesAhorroContrato = QueryLoader.loadYaml("procrea", "ahorrocontrato.yml");
        this.errorHandler = errorHandler;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }

    public RespuestaDTO obtenerParentescoById(Integer id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id", id);

            String query = "cat_relacion.obtenerParentescoById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("parentesco"));
            } else {
                log.error("No se encontro parentesco para el id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró parentesco para el id:: " + id);
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

    public List<CuentaOBJ> obtenerListadoCuentasVigentesProcreaByPersonaId(String personaId, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("productoId", productoId);

            String query = "ahorro_contrato.obtenerListadoCuentasVigentesProcreaByPersonaId";
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
}
