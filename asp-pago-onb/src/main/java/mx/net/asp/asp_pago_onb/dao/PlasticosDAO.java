package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.ConfiguracionASPPagoOBJ;
import mx.net.asp.asp_pago_onb.model.ValidacionDeCorreoOBJ;
import mx.net.asp.asp_pago_onb.request.ValidacionDeCorreoReq;
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    public ConfiguracionASPPagoOBJ obtenerValorConfAspPagoByConcepto(String concepto) {
        ConfiguracionASPPagoOBJ confAspPagoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("concepto", concepto);

            String query = "plconfiguraciones_asppago.obtenerValorConfAspPagoByConcepto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                confAspPagoOBJ = new ConfiguracionASPPagoOBJ();

                confAspPagoOBJ.setId((Long) row.get("id"));
                confAspPagoOBJ.setConcepto((String) row.get("concepto"));
                confAspPagoOBJ.setValor((String) row.get("valor"));
            } else {
                log.error("No se encontro el concepto {} en configuraciones de la asp pago", concepto);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            confAspPagoOBJ = null;
        }
        return confAspPagoOBJ;
    }

    public ValidacionDeCorreoOBJ obtenerValidacionDeCorreo(String curp, String rfc) {
        ValidacionDeCorreoOBJ vCorreoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("curp", curp);
            parameters.addValue("rfc", rfc);

            String query = "plvalidacion_correo.obtenerValidacionDeCorreo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                vCorreoOBJ = new ValidacionDeCorreoOBJ();

                vCorreoOBJ.setId((Long) row.get("id"));
                vCorreoOBJ.setCorreoElectronico((String) row.get("correo"));
                vCorreoOBJ.setEstado((String) row.get("estado"));
            } else {
                log.error("No se encontro registro de validacion de correo");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return vCorreoOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarValidacionDeCorreo(ValidacionDeCorreoReq vCorreoOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("correo", vCorreoOBJ.getCorreoElectronico());
            parameters.addValue("token", vCorreoOBJ.getToken());
            parameters.addValue("estado", vCorreoOBJ.getEstado());
            parameters.addValue("id", vCorreoOBJ.getId());

            String query = "plvalidacion_correo.actualizarValidacionDeCorreo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el registro de validacion de correo");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el registro de validacion de correo");
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
    public RespuestaDTO guardarValidacionDeCorreo(ValidacionDeCorreoReq vCorreoOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("correo", vCorreoOBJ.getCorreoElectronico());
            parameters.addValue("token", vCorreoOBJ.getToken());
            parameters.addValue("estado", vCorreoOBJ.getEstado());
            parameters.addValue("rfc", vCorreoOBJ.getRfc());
            parameters.addValue("curp", vCorreoOBJ.getCurp());

            String query = "plvalidacion_correo.guardarValidacionDeCorreo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el registro de validacion de correo");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar el registro de validacion de correo");
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
