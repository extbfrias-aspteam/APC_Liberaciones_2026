package mx.net.asp.asp_pago_documents.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.model.ConfiguracionEstadoMovimientoOBJ;
import mx.net.asp.asp_pago_documents.model.InfoDomicilioOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

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
        InfoDomicilioOBJ infoDomicilioOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
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

    public List<ConfiguracionEstadoMovimientoOBJ> obtenerConfiguracionReporteEdoMov() {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<ConfiguracionEstadoMovimientoOBJ> configuracionEstadoMovimientoList = new ArrayList<>();
        try {
            String query = "conf_data_reporte_edomovimientos.obtenerConfiguracionReporteEdoMov";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConfiguracionEstadoMovimientoOBJ configuracionEstadoMovimientoOBJ = new ConfiguracionEstadoMovimientoOBJ();
                    configuracionEstadoMovimientoOBJ.setClave((String) row.get("clave"));
                    configuracionEstadoMovimientoOBJ.setValor((String) row.get("valor"));
                    configuracionEstadoMovimientoOBJ.setTitulo((String) row.get("titulo"));
                    configuracionEstadoMovimientoList.add(configuracionEstadoMovimientoOBJ);
                }
            } else {
                log.error("No se encontro configuracion de reporte te estado de movimiento");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return configuracionEstadoMovimientoList.isEmpty() ? Collections.emptyList() : configuracionEstadoMovimientoList;
    }
}
