package mx.net.asp.asp_pago_payments.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.config.QueryLoader;
import mx.net.asp.asp_pago_payments.model.SesionLoginOBJ;
import mx.net.asp.asp_pago_payments.model.TraceIdSospechosoOBJ;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class SeguridadDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SeguridadDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "seguridad.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public boolean esSesionActivaValida(String personaId, String minutos) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("personaId", personaId);
            parameters.addValue("minutos", minutos);

            String query = "sesiones_asppago.esSesionActivaValida";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parámetros: {}", query, parameters.getValues());
    
            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);
            return !rows.isEmpty();
    
        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
    }

    public void registraUUIDSospechoso(TraceIdSospechosoOBJ traceIdSospechosoOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("uuid_txt", traceIdSospechosoOBJ.getTraceId());
            parameters.addValue("uuid_signed", traceIdSospechosoOBJ.getTraceIdSigned());
            parameters.addValue("endpoint", traceIdSospechosoOBJ.getEndpoint());
            parameters.addValue("ip_cliente", traceIdSospechosoOBJ.getIpCliente());
            parameters.addValue("observaciones", traceIdSospechosoOBJ.getObservaciones());

            String query = "seuuid_sospechosos.registraUUIDSospechoso";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro registrar el UUID sospechoso");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }

    public SesionLoginOBJ obtenerSesionActivaAspPagoByPersonaId(String personaId) {
        SesionLoginOBJ sesionLogin;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "sesiones_asppago.obtenerSesionAspPagoByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                sesionLogin = new SesionLoginOBJ();

                sesionLogin.setEstatus((String) row.get("estatus"));
                sesionLogin.setFecha_login(((Timestamp) row.get("fecha_login")).toInstant());
            } else {
                log.error("No se encontro sesion ASP pago del solicitante {}", personaId);
                sesionLogin = new SesionLoginOBJ();
                sesionLogin.setEstatus("NOTFOUND");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            sesionLogin = null;
        }
        return sesionLogin;
    }
}
