package mx.net.asp.asp_pago_payments.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.config.QueryLoader;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.PosGlobalCreditoOBJ;
import mx.net.asp.asp_pago_payments.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
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
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
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

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByCuentaClabe(String clabe) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clabe", clabe);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByCuentaClabe";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
                posGlobCuentaObj.setEstatus((String) row.get("estatusah"));
                posGlobCuentaObj.setCuenta_cobro((String) row.get("cuenta_cobro"));
                posGlobCuentaObj.setCuenta_pago((String) row.get("cuenta_pago"));
                posGlobCuentaObj.setPersonaId((String) row.get("idpersona"));
                posGlobCuentaObj.setTienePlastico(false);
                posGlobCuentaObj.setTipoCliente((String) row.get("tipo_cliente"));

                if (!(StringUtils.isBlank((String) row.get("tarjeta_principal")))) {
                    posGlobCuentaObj.setTarjetaVirtual((String) row.get("tarjeta_principal"));
                    posGlobCuentaObj.setTienePlastico(true);
                }
                if (!(StringUtils.isBlank((String) row.get("tarjeta_adicional")))) {
                    posGlobCuentaObj.setTarjetaFisica((String) row.get("tarjeta_adicional"));
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
            parameters.addValue("tarjeta_principal", tarjeta);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByTarjetaPrincipal";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
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
            parameters.addValue("tarjeta_adicional", tarjeta);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByTarjetaAdicional";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
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
                log.error("No se encontro informacion para la tarjeta {}", tarjeta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByPersonaIdCuentaAh(String personaId, String cuentaah) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("cuentaah", cuentaah);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByPersonaIdCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
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
                log.error("No se encontro informacion de posicion global para el solicitante {}, con cuenta {}", personaId, cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }

    public PosGlobalCuentaOBJ obtenerCuentaPosicionGlobalByPersonaId(String personaId) {
        PosGlobalCuentaOBJ posGlobCuentaObj = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("personaId", personaId);

            String query = "ncposicionglobalah.obtenerCuentaPosicionGlobalByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobCuentaObj = new PosGlobalCuentaOBJ();

                posGlobCuentaObj.setTipoCuenta((String) row.get("tipocuentaah"));
                posGlobCuentaObj.setCuentaah((String) row.get("cuentaah"));
                posGlobCuentaObj.setClabe((String) row.get("cuenta_clabe"));
            } else {
                log.error("No se encontro informacion para el solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobCuentaObj = null;
        }
        return posGlobCuentaObj;
    }


    public RespuestaDTO obtenerCatCanalByCuentaClabe(String clabe, String claveTransaccion, String claveAplicativo) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clabe", clabe);
            parameters.addValue("claveTransaccion", claveTransaccion);
            parameters.addValue("claveAplicativo", claveAplicativo);

            String query = "cat_canal_cuenta.obtenerCatCanalByCuentaClabe";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.warn("No se encontraron canales en cat_canal_cuenta para la clabe {}", clabe);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron canales en cat_canal_cuenta para la clabe " + clabe);
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

    public PosGlobalCreditoOBJ obtenerCreditoPosicionGlobalByCuentaClabe(String clabe) {
        PosGlobalCreditoOBJ posGlobalCreditoOBJ = null;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("clabe", clabe);

            String query = "ncposicionglobalcr.obtenerCreditoPosicionGlobalByCuentaClabe";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                posGlobalCreditoOBJ = new PosGlobalCreditoOBJ();

                posGlobalCreditoOBJ.setId((Integer) row.get("id"));
                posGlobalCreditoOBJ.setEstadocr((String) row.get("estadocr"));
                posGlobalCreditoOBJ.setProductocr((String) row.get("productocr"));
                posGlobalCreditoOBJ.setContratocr((String) row.get("contratocr"));
                posGlobalCreditoOBJ.setSaldoinsolutocr((Double) row.get("saldoinsolutocr"));
                posGlobalCreditoOBJ.setMontosolicitadocr((Double) row.get("montosolicitadocr"));
                posGlobalCreditoOBJ.setCalificacioncr((String) row.get("calificacioncr"));
                posGlobalCreditoOBJ.setDiasvencidocr((Integer) row.get("diasvencidocr"));
                posGlobalCreditoOBJ.setAvancecr((Integer) row.get("avancecr"));
                posGlobalCreditoOBJ.setDisposicioncr((Integer) row.get("disposicioncr"));
                posGlobalCreditoOBJ.setIdpersona((String) row.get("idpersona"));
                posGlobalCreditoOBJ.setCuentaClabe((String) row.get("cuenta_clabe"));
            } else {
                log.error("No se encontro credito para la clabe {}", clabe);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            posGlobalCreditoOBJ = null;
        }
        return posGlobalCreditoOBJ;
    }
}
