package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.model.InfoDomicilioOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class PublicCeroDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public PublicCeroDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "public.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public InfoDomicilioOBJ obtenerInformacionDomicilioByColoniaId(Long coloniaId) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        InfoDomicilioOBJ infoDomicilioOBJ = null;
        try {
            //Se definen los parametros para el query
            parameters.addValue("coloniaId", coloniaId);

            String query = "prestado.obtenerInformacionDomicilioByColoniaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                infoDomicilioOBJ = new InfoDomicilioOBJ();

                infoDomicilioOBJ.setEstado((String) row.get("estado"));
                infoDomicilioOBJ.setMunicipio((String) row.get("municipio"));
                infoDomicilioOBJ.setLocalidad((String) row.get("localidad"));
                infoDomicilioOBJ.setColonia((String) row.get("colonia"));
                infoDomicilioOBJ.setCp((String) row.get("cp"));
            } else {
                log.error("No se encontro informacion de la colonia {}", coloniaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            infoDomicilioOBJ = null;
        }
        return infoDomicilioOBJ;
    }

    public Integer obtenerEstadoByClave(String clave) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer resultado = 0;
        try {
            //Se definen los parametros para el query
            parameters.addValue("clave", clave);

            String query = "prestado.obtenerEstadoByClave";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                resultado = (Integer) row.get("estado_id");
            } else {
                log.error("No se encontro informacion del estado con clave {}", clave);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return resultado;
    }

}
