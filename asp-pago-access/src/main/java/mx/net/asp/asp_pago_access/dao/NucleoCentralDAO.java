package mx.net.asp.asp_pago_access.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_access.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Log4j2
@Repository
public class NucleoCentralDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public NucleoCentralDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "nucleocentral.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByCuentaAh(String cuentaah) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("cuentaah", cuentaah);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("clabe_interbancaria"));
                posGlobCuentaObj.setEstatus((String) row.get("estatusah"));
                posGlobCuentaObj.setCuenta_cobro((String) row.get("cuenta_cobro"));
                posGlobCuentaObj.setCuenta_pago((String) row.get("cuenta_pago"));
                posGlobCuentaObj.setPersonaId((String) row.get("idpersona"));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get("tarjeta_principal")))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get("tarjeta_principal"));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get("tarjeta_adicional")))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get("tarjeta_adicional"));
                    posGlobCuentaObj.setTienePlastico(true);
                }
            } else {
                log.error("No se encontro informacion para la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }

    public List<PosGlobalCuentaOBJ> obtenerListCuentasPosicionGlobalByPersonaId(String personaId) {
        List<PosGlobalCuentaOBJ> registros = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Se definen los parámetros para el query
            parameters.addValue("personaId", personaId);

            String query = "ncposicionglobalah.obtenerListCuentasPosicionGlobalByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);

            for (Map<String, Object> row : rows) {
                PosGlobalCuentaOBJ posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("clabe_interbancaria"));
                posGlobCuentaObj.setEstatus((String) row.get("estatusah"));
                posGlobCuentaObj.setCuenta_cobro((String) row.get("cuenta_cobro"));
                posGlobCuentaObj.setCuenta_pago((String) row.get("cuenta_pago"));
                posGlobCuentaObj.setPersonaId((String) row.get("idpersona"));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get("tarjeta_principal")))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get("tarjeta_principal"));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get("tarjeta_adicional")))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get("tarjeta_adicional"));
                    posGlobCuentaObj.setTienePlastico(true);
                }

                registros.add(posGlobCuentaObj);
            }

            if (registros.isEmpty()) {
                log.warn("No se encontró registro CoDi para el solicitante: {}", personaId);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            registros = Collections.emptyList();
        }

        return registros;
    }

    public Boolean tieneCuentasVigentes(String personaId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Boolean resultado = false;
        try {

            parameters.addValue("personaId", personaId);

            String query = "ncposicionglobalah.obtenerCuentasVigentesByPersonaId";
            String sql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            resultado = namedCeroJdbc.queryForObject(sql, parameters, Boolean.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la existencia de las cuentas vigentes del cliente: " + e.getMessage()) {
            };
        }

        return resultado;
    }
}
