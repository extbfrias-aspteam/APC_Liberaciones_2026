package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;

import mx.net.asp.asp_pago_management.model.TraceIdSospechosoOBJ;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

import java.util.Properties;

@Log4j2
@Repository
public class SeguridadDAO {
    private final Properties queriesAhorro;
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    // private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final String ejecutandoMessage = "Ejecutando consulta: {}, parametros: {}";
    private final String internalErrorMessage = "Error interno";

    public SeguridadDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesAhorro = QueryLoader.loadYaml("cero", "seguridad.yml");
        // this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public SesionIncodeOBJ obtenerSesionIncode(String cuentaah) {
        SesionIncodeOBJ sesionIncodeOBJ = null;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queriesAhorro.getProperty("seincode_proceso.obtenerSesionIncode");


            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.get(0);
                sesionIncodeOBJ = new SesionIncodeOBJ();

                sesionIncodeOBJ.setSesionIncodeId((String) row.get("sesion_incode_id"));
                sesionIncodeOBJ.setActivo((Boolean) row.get("activo"));
                sesionIncodeOBJ.setEstatus((String) row.get("estatus"));

            } else {
                log.error("No se encontro sesion incode con la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            log.error("Error interno: ", e);
        }
        return sesionIncodeOBJ;
    }

    public SesionIncodeOBJ obtenerSesionIncodeByEstatus(String cuentaah) {
        SesionIncodeOBJ sesionIncodeOBJ = null;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queriesAhorro.getProperty("seincode_proceso.obtenerSesionIncodeByEstatus");


            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.get(0);
                sesionIncodeOBJ = new SesionIncodeOBJ();

                sesionIncodeOBJ.setSesionIncodeId((String) row.get("sesion_incode_id"));
                sesionIncodeOBJ.setActivo((Boolean) row.get("activo"));
                sesionIncodeOBJ.setEstatus((String) row.get("estatus"));

            } else {
                log.error("No se encontro sesion incode con la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            log.error("Error interno: ", e);
        }
        return sesionIncodeOBJ;
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
            String psql = queriesAhorro.getProperty(query);

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro registrar el UUID sospechoso");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }
}
