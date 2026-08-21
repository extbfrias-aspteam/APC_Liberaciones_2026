package mx.net.asp.asp_pago_access.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.model.*;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class IzelStiDAO {
    private final Properties queries;
    private final JdbcTemplate izelSTIJdbc;
    private final NamedParameterJdbcTemplate namedIzelSTIJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public IzelStiDAO(
            @Qualifier("izelJdbcTemplate") JdbcTemplate izelSTIJdbc,
            @Qualifier("namedIzelSTIJdbcTemplate") NamedParameterJdbcTemplate namedIzelSTIJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("izelSTI", "public.yml");
        this.izelSTIJdbc = izelSTIJdbc;
        this.namedIzelSTIJdbc = namedIzelSTIJdbc;
        this.errorHandler = errorHandler;
    }

    public List<CatInstiOBJ>  obtenerListadoInstituciones() {
        List<Map<String, Object>> rows;
        List<CatInstiOBJ> institucionesList = new ArrayList<>();
        try {
            String query = "spei_instituciones.obtenerListadoInstituciones";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = izelSTIJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CatInstiOBJ instiObj = new CatInstiOBJ();
                    instiObj.setClave_institucion((Integer) row.get("id_institucion"));
                    instiObj.setNombreCorto(((String) row.get("descripcion")).trim());
                    institucionesList.add(instiObj);
                }
            } else {
                log.error("No se encontraron registros de instituciones bancarias");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return institucionesList.isEmpty() ? Collections.emptyList() : institucionesList;
    }
}
