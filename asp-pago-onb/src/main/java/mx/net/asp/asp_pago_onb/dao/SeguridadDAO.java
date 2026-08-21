package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.model.TraceIdSospechosoOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    public void registraLogValidaFotoIne(String curp, String response, String resultadoValidacion, String tipoFlujo, Integer usuario) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("curp", curp);
            parameters.addValue("response", response);
            parameters.addValue("resultado_validacion", resultadoValidacion);
            parameters.addValue("tipo_flujo", tipoFlujo);
            parameters.addValue("usuario", usuario);

            String query = "selog_validacion_biometrica.registraLogValidaFotoIne";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Se registro el LOG de validar Foto INE correctamente");
            } else {
                log.error("No se logro registrar el LOG de validar Foto INE");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }

}
