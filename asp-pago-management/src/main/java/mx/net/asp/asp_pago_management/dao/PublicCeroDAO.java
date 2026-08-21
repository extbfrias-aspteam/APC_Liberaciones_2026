package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.model.ConsultaCPOBJ;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class PublicCeroDAO {
    private final Properties queries;
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;

    public PublicCeroDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "public.yml");
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public List<ConsultaCPOBJ> consultaColoniasByCp(String cp) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<ConsultaCPOBJ> coloniasList = new ArrayList<>();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cp", cp);

            String query = "prestado.obtenerInformacionDomicilioByCodigoPostal";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConsultaCPOBJ consultaCPOBJ = new ConsultaCPOBJ();
                    consultaCPOBJ.setEstado((String) row.get("estado"));
                    consultaCPOBJ.setMunicipio(((String) row.get("municipio")).trim());
                    consultaCPOBJ.setLocalidad(((String) row.get("localidad")).trim());
                    consultaCPOBJ.setColoniaId(((Number) row.get("colonia_id")).longValue());
                    consultaCPOBJ.setColonia(((String) row.get("colonia")).trim());
                    consultaCPOBJ.setCp(((String) row.get("cp")).trim());
                    coloniasList.add(consultaCPOBJ);
                }
            } else {
                log.error("No se encontraron registros de colonias para el cp {}", cp);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return coloniasList.isEmpty() ? Collections.emptyList() : coloniasList;
    }
}
