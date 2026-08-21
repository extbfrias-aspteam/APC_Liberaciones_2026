package mx.net.asp.asp_pago_onb.utilerias;

import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Log4j2
public class QueryLogger {
    private QueryLogger() {
        // Constructor privado para evitar instanciación
    }

    public static void logQuery(String query, MapSqlParameterSource parameters) {
        log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
    }

    public static void logQuery(String query) {
        log.info("Ejecutando consulta: {}", query);
    }
}
