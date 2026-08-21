package mx.net.asp.asp_pago_access.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.ConfiguracionASPPagoOBJ;
import mx.net.asp.asp_pago_access.model.CostoOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class PlasticosDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public PlasticosDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "plasticos.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerValorConfAspPagoByConcepto(String concepto) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("concepto", concepto);

            String query = "plconfiguraciones_asppago.obtenerValorConfAspPagoByConcepto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                ConfiguracionASPPagoOBJ confAspPagoOBJ = new ConfiguracionASPPagoOBJ();

                confAspPagoOBJ.setId((Long) row.get("id"));
                confAspPagoOBJ.setConcepto((String) row.get("concepto"));
                confAspPagoOBJ.setValor((String) row.get("valor"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(confAspPagoOBJ));
            } else {
                log.error("No se encontro el concepto {} en configuraciones de la asp pago", concepto);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró el concepto " + concepto);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public List<ConfiguracionASPPagoOBJ> obtenerListadoConfAspPagoByConcepto() {
        List<Map<String, Object>> rows;
        List<ConfiguracionASPPagoOBJ> configList = new ArrayList<>();
        try {
            String query = "plconfiguraciones_asppago.obtenerListadoConfiguracionesAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConfiguracionASPPagoOBJ confAspPagoOBJ = new ConfiguracionASPPagoOBJ();
                    confAspPagoOBJ.setId((Long) row.get("id"));
                    confAspPagoOBJ.setConcepto((String) row.get("concepto"));
                    confAspPagoOBJ.setValor((String) row.get("valor"));
                    configList.add(confAspPagoOBJ);
                }
            } else {
                log.error("No se encontraron registros en configuraciones de la asp pago");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return configList.isEmpty() ? Collections.emptyList() : configList;
    }

    public List<CostoOBJ> obtenerListadoCostosTarjetas() {
        List<Map<String, Object>> rows;
        List<CostoOBJ> costosList = new ArrayList<>();
        try {
            String query = "plcosto_tarjeta.obtenerCostosTarjetas";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CostoOBJ costo = new CostoOBJ();
                    costo.setTipoTarjeta((String) row.get("tipo"));
                    costo.setDescripcion((String) row.get("descripcion"));
                    costo.setCosto((Double) row.get("costo"));
                    costosList.add(costo);
                }
            } else {
                log.error("No se encontro listado de costos de tarjetas");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return costosList.isEmpty() ? Collections.emptyList() : costosList;
    }
}
