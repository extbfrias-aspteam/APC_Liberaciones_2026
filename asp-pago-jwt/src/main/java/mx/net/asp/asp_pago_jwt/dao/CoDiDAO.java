package mx.net.asp.asp_pago_jwt.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.config.QueryLoader;
import mx.net.asp.asp_pago_jwt.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Log4j2
@Repository
public class CoDiDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public CoDiDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RegistroCoDiOBJ consultarRegistroCoDiCliente(String personaId) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.consultarRegistroCoDiCliente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) row.get("activo"));
                regCoDiOBJ.setTelefono_validado((String) row.get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) row.get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) row.get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) row.get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) row.get("intentos"));
                regCoDiOBJ.setPrincipal((Boolean) row.get("principal"));
            } else {
                log.error("No se encontro registro codi del solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }
}
