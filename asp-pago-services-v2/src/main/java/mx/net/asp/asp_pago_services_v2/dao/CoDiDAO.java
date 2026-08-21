package mx.net.asp.asp_pago_services_v2.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public RespuestaDTO obtenerRegistroCoDiByCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queries.getProperty("registro_codi.obtenerRegistroCoDiByCuentaAh");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();
                RegistroCoDiOBJ regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(regCoDiOBJ));
            } else {
                log.error("No se encontro registro codi de la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró registro codi la cuenta " + cuentaah);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
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
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));
                regCoDiOBJ.setBloqueado((Integer) row.get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) row.get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) row.get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) row.get("intentos"));
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
