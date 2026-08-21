package mx.net.asp.asp_pago_documents.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Log4j2
@Repository
public class InversionesDAO {
    private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public InversionesDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "inversiones.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public Boolean existeInversionByCuenta(String cuenta) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Boolean resultado = false;
        try {
            parameters.addValue("cuenta", cuenta);

            String query = "inversiones.existeInversionByCuenta";
            String sql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            resultado = namedProcreaJdbc.queryForObject(sql, parameters, Boolean.class);
        } catch (Exception e) {
        	errorHandler.handleException(e);
            throw new DataAccessException("No se pudo obtener la existencia de la cuenta de inversion: " + e.getMessage()) {
            };
        }

        return resultado;
    }

    public Integer validaExisteInformacionEdoCuenta(Integer anio, Integer mes, String cuenta) {
        List<Map<String, Object>> rows;
        Integer res = -1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
        	parameters.addValue("anio", anio);
        	parameters.addValue("mes", mes);
            parameters.addValue("cuenta", cuenta);

            String query = "inversiones.validaExisteInformacionEdoCuenta";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                res = 0;
            } else {
                log.error("No se encontraron datos de para la generación de estado de cuenta {}", cuenta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);

        }
        return res;
    }
}
