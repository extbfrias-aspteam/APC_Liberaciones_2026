package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class NucleoCentralDAO {
    private final Properties queries;
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final String estatusAh = "estatusAh";
    private final String cuentaCobro = "cuenta_cobro";
    private final String clabeInterbancaria = "clabe_interbancaria";
    private final String cuenta_clabe = "cuenta_clabe";
    private final String tarjetaAdicional = "tarjeta_adicional";
    private final String tipoCuentaAh = "tipocuentaah";
    private final String cuentaPago = "cuenta_pago";
    private final String idPersona = "idpersona";
    private final String cuentaAh = "cuentaah";
    private final String ejecutandoConsulta = "Ejecutando consulta: {}, parametros: {}";
    private final String tarjetaPrincipal = "tarjeta_principal";

    public NucleoCentralDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "nucleocentral.yml");
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByCuentaAh(String cuentaah) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(cuentaAh, cuentaah);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByCuentaAh";
            String psql = queries.getProperty(query);
            log.info(ejecutandoConsulta, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get(tipoCuentaAh));
                posGlobCuentaObj.setCuentaah((String) row.get(cuentaAh));
                posGlobCuentaObj.setClabe((String) row.get(cuenta_clabe));
                posGlobCuentaObj.setEstatus((String) row.get(estatusAh));
                posGlobCuentaObj.setCuenta_cobro((String) row.get(cuentaCobro));
                posGlobCuentaObj.setCuenta_pago((String) row.get(cuentaPago));
                posGlobCuentaObj.setPersonaId((String) row.get(idPersona));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get(tarjetaPrincipal)))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get(tarjetaPrincipal));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get(tarjetaAdicional)))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get(tarjetaAdicional));
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

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByCuentaClabe(String clabe) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clabe", clabe);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByCuentaClabe";
            String psql = queries.getProperty(query);
            log.info(ejecutandoConsulta, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get(tipoCuentaAh));
                posGlobCuentaObj.setCuentaah((String) row.get(cuentaAh));
                posGlobCuentaObj.setClabe((String) row.get(cuenta_clabe));
                posGlobCuentaObj.setEstatus((String) row.get(estatusAh));
                posGlobCuentaObj.setCuenta_cobro((String) row.get(cuentaCobro));
                posGlobCuentaObj.setCuenta_pago((String) row.get(cuentaPago));
                posGlobCuentaObj.setPersonaId((String) row.get(idPersona));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get(tarjetaPrincipal)))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get(tarjetaPrincipal));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get(tarjetaAdicional)))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get(tarjetaAdicional));
                    posGlobCuentaObj.setTienePlastico(true);
                }
            } else {
                log.error("No se encontro informacion para la clabe {}", clabe);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByTarjetaPrincipal(String tarjeta) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(tarjetaPrincipal, tarjeta);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByTarjetaPrincipal";
            String psql = queries.getProperty(query);
            log.info(ejecutandoConsulta, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get(tipoCuentaAh));
                posGlobCuentaObj.setCuentaah((String) row.get(cuentaAh));
                posGlobCuentaObj.setClabe((String) row.get(cuenta_clabe));
                posGlobCuentaObj.setEstatus((String) row.get(estatusAh));
                posGlobCuentaObj.setCuenta_cobro((String) row.get(cuentaCobro));
                posGlobCuentaObj.setCuenta_pago((String) row.get(cuentaPago));
                posGlobCuentaObj.setPersonaId((String) row.get(idPersona));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get(tarjetaPrincipal)))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get(tarjetaPrincipal));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get(tarjetaAdicional)))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get(tarjetaAdicional));
                    posGlobCuentaObj.setTienePlastico(true);
                }
            } else {
                log.error("No se encontro informacion para la tarjeta {}", tarjeta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByTarjetaAdicional(String tarjeta) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(tarjetaAdicional, tarjeta);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByTarjetaAdicional";
            String psql = queries.getProperty(query);
            log.info(ejecutandoConsulta, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get(tipoCuentaAh));
                posGlobCuentaObj.setCuentaah((String) row.get(cuentaAh));
                posGlobCuentaObj.setClabe((String) row.get(cuenta_clabe));
                posGlobCuentaObj.setEstatus((String) row.get(estatusAh));
                posGlobCuentaObj.setCuenta_cobro((String) row.get(cuentaCobro));
                posGlobCuentaObj.setCuenta_pago((String) row.get(cuentaPago));
                posGlobCuentaObj.setPersonaId((String) row.get("idpersona"));
                posGlobCuentaObj.setTienePlastico(false);

                if (!(StringUtils.isBlank((String) row.get(tarjetaPrincipal)))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get(tarjetaPrincipal));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get(tarjetaAdicional)))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get(tarjetaAdicional));
                    posGlobCuentaObj.setTienePlastico(true);
                }
            } else {
                log.error("No se encontro informacion para la tarjeta {}", tarjeta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }
}
