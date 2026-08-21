package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Log4j2
@Repository
public class PromocionesDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public PromocionesDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "promociones.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public Integer obtenerCodigoIdByCodigoReferencia(String codigoReferencia) {
        Integer id = 0;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("codigo_referencia", codigoReferencia);

            String query = "promociones.obtenerCodigoIdByCodigoReferencia";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                Long idLong = (Long) row.get("id");
                if (idLong > Integer.MAX_VALUE || idLong < Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("El id excede el rango permitido para Integer: " + idLong);
                }
                id = idLong.intValue();
            } else {
                log.error("No se encontro el codigo de referencia {}");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        return id;
    }

    public String obtenerCuentaByCuenta(String cuenta) {
        String cuentaDB = "";
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "promociones.obtenerCuentaByCuenta";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                cuentaDB = (String) row.get("cuenta");
            } else {
                log.error("No se encontro registrada la cuenta {} en DB", cuenta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        return cuentaDB;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registrarCodigoReferencia(Integer idCodigoReferencia, String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Mapeo de los parametros para el query
            parameters.addValue("cuenta", cuenta);
            parameters.addValue("idCodigoReferencia", idCodigoReferencia);

            String query = "promociones.registrarCodigoReferencia";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el coidgo de referencia para la cuenta {}", cuenta);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el código de referencia");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarCodigoReferencia(Integer idCodigoReferencia, String cuenta, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Mapeo de los parametros para el query
            parameters.addValue("cuenta", cuenta);
            parameters.addValue("idCodigoReferencia", idCodigoReferencia);
            parameters.addValue("usuario", usuario);

            String query = "promociones.actualizarCodigoReferencia";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el coidgo de referencia para la cuenta {}", cuenta);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el código de referencia");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}
