package mx.net.asp.asp_pago_onb.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Log4j2
@Repository
public class ProductosDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public ProductosDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "productos.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerConceptoProductoByClave(Integer productoId, String clave) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        Boolean result = false;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        try {
            parameters.addValue("productoId", productoId);
            parameters.addValue("clave", clave);

            String query = "productos.obtenerConceptoProductoByClave";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("valor"));
            } else {
                log.error("No se encontro concepto del producto {} con clave {}", productoId, clave);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró concepto del producto " + productoId);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public ProductoAhorroOBJ obtenerDatosProductoByProductoId(Integer productoId) {
        ProductoAhorroOBJ productoAhorroOBJ = null;
        List<Map<String, Object>> rows;
        Boolean result = false;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        try {
            parameters.addValue("productoId", productoId);

            String query = "productos.obtenerDatosProductoByProductoId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                productoAhorroOBJ = new ProductoAhorroOBJ();

                productoAhorroOBJ.setId((Integer) row.get("id"));
                productoAhorroOBJ.setClave((String) row.get("clave"));
                productoAhorroOBJ.setDescripcion((String) row.get("descripcion"));
                productoAhorroOBJ.setMontoMinimo(((BigDecimal) row.get("monto_minimo")).doubleValue());
                productoAhorroOBJ.setMontoMax(((BigDecimal) row.get("monto_maximo")).doubleValue());
            } else {
                log.error("No se encontraron datos del producto {}", productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            productoAhorroOBJ = null;
        }
        return productoAhorroOBJ;
    }

    public RendimientoOBJ obtenerRendimientoByProductoId(Integer productoId, double montoMin) {
        RendimientoOBJ rendimientoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("productoId", productoId);
            parameters.addValue("montoMin", montoMin);

            String query = "productos.obtenerRendimientoByProductoId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                rendimientoOBJ = new RendimientoOBJ();

                rendimientoOBJ.setPorcentaje(((BigDecimal) row.get("porcentaje")).doubleValue());
                rendimientoOBJ.setPeriodicidad((Integer) row.get("periodicidad"));
            } else {
                log.error("No se encontro rendimiento del producto {}", productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            rendimientoOBJ = null;
        }
        return rendimientoOBJ;
    }

    public List<ProductoAhorroDatosOBJ> obtenerListadoDatosProductoByProductoId(Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<ProductoAhorroDatosOBJ> productoDatosList = new ArrayList<>();
        try {
            parameters.addValue("productoId", productoId);

            String query = "productos.obtenerListadoDatosProductoByProductoId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ProductoAhorroDatosOBJ productoDatosOBJ = new ProductoAhorroDatosOBJ();
                    productoDatosOBJ.setConcepto_id((Integer) row.get("concepto_id"));
                    productoDatosOBJ.setEstatus_id((Integer) row.get("estatus_id"));
                    productoDatosOBJ.setId((Integer) row.get("id"));
                    productoDatosOBJ.setProducto_id(productoId);
                    productoDatosOBJ.setValor((String) row.get("valor"));
                    productoDatosOBJ.setClave((String) row.get("clave"));
                    productoDatosList.add(productoDatosOBJ);
                }
            } else {
                log.error("No se encontro listado de conceptos cuenta Simplificada (cero)");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return productoDatosList.isEmpty() ? Collections.emptyList() : productoDatosList;
    }
}
