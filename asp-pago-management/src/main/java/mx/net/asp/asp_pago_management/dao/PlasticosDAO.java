package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.ConfiguracionASPPagoOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class PlasticosDAO {
    private final Properties queries;
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;

    public PlasticosDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "plasticos.yml");
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public ConfiguracionASPPagoOBJ obtenerValorConfAspPagoByConcepto(String concepto) {
        ConfiguracionASPPagoOBJ confAspPagoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        try {
            parameters.addValue("concepto", concepto);

            String query = "plconfiguraciones_asppago.obtenerValorConfAspPagoByConcepto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                confAspPagoOBJ = new ConfiguracionASPPagoOBJ();

                confAspPagoOBJ.setId((Long) row.get("id"));
                confAspPagoOBJ.setConcepto((String) row.get("concepto"));
                confAspPagoOBJ.setValor((String) row.get("valor"));
            } else {
                log.error("No se encontro el concepto {} en configuraciones de la asp pago", concepto);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            confAspPagoOBJ = null;
        }
        return confAspPagoOBJ;
    }
}
