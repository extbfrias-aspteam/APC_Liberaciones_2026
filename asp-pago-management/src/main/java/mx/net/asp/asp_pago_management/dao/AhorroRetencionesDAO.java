package mx.net.asp.asp_pago_management.dao;

import java.util.Properties;

import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;

@Log4j2
@Repository
public class AhorroRetencionesDAO {

    private final Properties queriesProcrea;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    public AhorroRetencionesDAO(
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.errorHandler = errorHandler;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }


    public Double montoRetenciones(String cuenta) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_retenciones.montoRetenciones";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedProcreaJdbc.queryForObject(psql, parameters, Double.class);
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return 0.0;
    }


}
