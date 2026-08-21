package mx.net.asp.asp_pago_access.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.CreditoOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class CreditosDAO {
    private final Properties queriesCero;
    private final Properties queriesProcrea;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public CreditosDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesCero = QueryLoader.loadYaml("cero", "nucleocentral.yml");
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerListadoCreditosByPersonaId(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        Gson gson = new Gson();
        List<CreditoOBJ> creditosList = new ArrayList<>();
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "ncposicionglobalcr.obtenerCreditosActivosByPersonaId";
            String psql = queriesCero.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CreditoOBJ credObj = new CreditoOBJ();
                    credObj.setControl((String) row.get("cuenta"));
                    credObj.setCuentaClabe((String) row.get("cuenta_clabe"));
                    credObj.setMonto((BigDecimal) row.get("monto"));
                    credObj.setReferencia((String) row.get("referencia"));
                    creditosList.add(credObj);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                //respuesta.setData(gson.toJson(creditosList));
            } else {
                log.info("No se encontraron creditos activos en cero del solicitante {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron creditos activos en cero del solicitante " + personaId);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "creditos.obtenerCreditosActivosByPersonaId";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CreditoOBJ credObj = new CreditoOBJ();
                    credObj.setControl((String) row.get("cuenta"));
                    credObj.setCuentaClabe((String) row.get("cuenta_clabe"));
                    credObj.setMonto((BigDecimal) row.get("monto"));
                    credObj.setReferencia((String) row.get("referencia"));
                    creditosList.add(credObj);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                //respuesta.setData(gson.toJson(creditosList));
            } else {
                log.info("No se encontraron creditos activos en procrea del solicitante {}", personaId);
                if (creditosList.isEmpty()) {
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("No se encontraron creditos activos en procrea del solicitante " + personaId);
                    respuesta.setData(null);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            if (creditosList.isEmpty()) {
                RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }
        }
        if (respuesta.getCodigo() == 0)
            respuesta.setData(gson.toJson(creditosList));

        return respuesta;
    }
}
