package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.ServiciosMontosOBJ;
import mx.net.asp.asp_pago_management.model.ServiciosTercerosOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Log4j2
@Repository
public class ComisionistasDAO {
    private final Properties queries;
    private final ErrorHandler errorHandler;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;

    public ComisionistasDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "comisionistas.yml");
        this.errorHandler = errorHandler;
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public List<ServiciosTercerosOBJ> obtenerListadoServiciosTerceros() {
        List<Map<String, Object>> rows;
        List<ServiciosTercerosOBJ> serviciosTercerosList = new ArrayList<>();
        try {
            String query = "coservicios_empresas.obtenerListadoServiciosTerceros";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ServiciosTercerosOBJ r = new ServiciosTercerosOBJ();
                    r.setId((Long) row.get("id"));
                    r.setServicio((String) row.get("servicio"));
                    r.setTipo_producto((String) row.get("tipo_producto"));
                    r.setNememp((String) row.get("nememp"));
                    r.setSubemp((String) row.get("subemp"));
                    r.setComision(((Float) row.get("comision")).doubleValue());
                    r.setUrl((String) row.get("url"));
                    r.setCodigo((String) row.get("codigo"));
                    r.setEsFavorito((Boolean) row.get("esfavorito"));
                    serviciosTercerosList.add(r);
                }
            } else {
                log.error("No se encontraron registros en el catalogo de Servicios de Terceros");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return serviciosTercerosList.isEmpty() ? Collections.emptyList() : serviciosTercerosList;
    }

    public List<ServiciosTercerosOBJ> obtenerListadoRecargasTelefonicas() {
        List<Map<String, Object>> rows;
        List<Map<String, Object>> rowsMontos;
        List<ServiciosMontosOBJ> resultMontos;
        List<ServiciosTercerosOBJ> serviciosTercerosList = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            String query = "coservicios_empresas.obtenerListadoRecargasTelefonicas";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = ceroJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ServiciosTercerosOBJ r = new ServiciosTercerosOBJ();
                    r.setId((Long) row.get("id"));
                    r.setServicio((String) row.get("servicio"));
                    r.setTipo_producto((String) row.get("tipo_producto"));
                    r.setNememp((String) row.get("nememp"));
                    r.setSubemp((String) row.get("subemp"));
                    r.setComision(((Float) row.get("comision")).doubleValue());
                    r.setUrl((String) row.get("url"));
                    r.setCodigo((String) row.get("codigo"));

                    resultMontos = new ArrayList<>();

                    Long id = (Long) row.get("id");
                    parameters.addValue("id", id);

                    String psqlMontos = queries.getProperty("coservicios_montos.obtenerMontosRecargasTelefonicas");
                    rowsMontos = namedCeroJdbc.queryForList(psqlMontos, parameters);

                    for (Map<String, Object> rowMonto : rowsMontos) {
                        ServiciosMontosOBJ objectMontos = new ServiciosMontosOBJ();
                        objectMontos.setId_monto((Long) rowMonto.get("id"));
                        objectMontos.setMonto((Double) rowMonto.get("monto"));
                        objectMontos.setProducto((String) rowMonto.get("producto"));
                        resultMontos.add(objectMontos);
                    }

                    r.setMontos(resultMontos);
                    serviciosTercerosList.add(r);
                }
            } else {
                log.error("No se encontraron registros en el catalogo de Servicios de Recargas Telefonicas");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return serviciosTercerosList.isEmpty() ? Collections.emptyList() : serviciosTercerosList;
    }
}
