package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class IzelStiDAO {
    private final Properties queries;
    private final NamedParameterJdbcTemplate namedIzelSTIJdbc;
    private final ErrorHandler errorHandler;

    public IzelStiDAO(
            @Qualifier("namedIzelSTIJdbcTemplate") NamedParameterJdbcTemplate namedIzelSTIJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("izelSTI", "public.yml");
        this.errorHandler = errorHandler;
        this.namedIzelSTIJdbc = namedIzelSTIJdbc;
    }

    public CatInstiOBJ obtenerInstitucionBancariaById(Integer id) {
        List<Map<String, Object>> rows;
        CatInstiOBJ catInstiOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id", id);

            String query = "spei_instituciones.obtenerInstitucionById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                catInstiOBJ = new CatInstiOBJ();
                catInstiOBJ.setClave_institucion((Integer) row.get("id_institucion"));
                catInstiOBJ.setNombreCorto(((String) row.get("descripcion")).trim());
            } else {
                log.warn("No se encontraron registros de instituciones bancarias");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            catInstiOBJ = null;
        }
        return catInstiOBJ;
    }
}
