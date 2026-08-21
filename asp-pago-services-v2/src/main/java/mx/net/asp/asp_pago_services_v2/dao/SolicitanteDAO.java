package mx.net.asp.asp_pago_services_v2.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class SolicitanteDAO {
    private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SolicitanteDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "solicitante.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerInfoSolicitanteById(String idSolicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        parameters.addValue("numero", idSolicitante);
        try {
            String psql = queries.getProperty("solicitante.obtenerInfoById");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();
                SolicitanteOBJ solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(solicitanteOBJ));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró información del solicitante");
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
}
