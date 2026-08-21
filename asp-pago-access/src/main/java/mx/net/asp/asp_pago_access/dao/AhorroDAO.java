package mx.net.asp.asp_pago_access.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.model.CuentaAspPagoOBJ;
import mx.net.asp.asp_pago_access.model.NivelCuentaOBJ;
import mx.net.asp.asp_pago_access.model.TarjetaOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class AhorroDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public AhorroDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public CuentaAspPagoOBJ obtenerDatosCuentaAhByCuentaAh(String cuentaah) {
        CuentaAspPagoOBJ cuentaAspPagoObj = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<TarjetaOBJ> tarjetas = new ArrayList<>();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerDatosCuentaAhByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                cuentaAspPagoObj = new CuentaAspPagoOBJ();
                cuentaAspPagoObj.setCuentaId((Integer) row.get("cuentaId"));
                cuentaAspPagoObj.setCuenta((String) row.get("cuenta"));
                cuentaAspPagoObj.setProducto((String) row.get("producto"));
                cuentaAspPagoObj.setProductoId((Integer) row.get("productoId"));
                cuentaAspPagoObj.setDescripcion((String) row.get("descripcion"));
                cuentaAspPagoObj.setClabe((String) row.get("clabe"));
                cuentaAspPagoObj.setCobro((String) row.get("cuenta_cobro"));
                cuentaAspPagoObj.setPago((String) row.get("cuenta_pago"));
                cuentaAspPagoObj.setEstatus((String) row.get("estatusah"));
                cuentaAspPagoObj.setMostrarTarjetaAPP(1);

                if (!(StringUtils.isBlank((String) row.get("tarjeta_principal")))) {
                    TarjetaOBJ tarjeta = new TarjetaOBJ();
                    tarjeta.setNumeroTarjeta((String) row.get("tarjeta_principal"));
                    tarjeta.setProveedor((String) row.get("proveedor"));
                    tarjeta.setTipoTarjeta((String) row.get("tipo_tarjeta"));
                    tarjeta.setMostrarTarjetaAPP(1);
                    tarjetas.add(tarjeta);
                }
                if (!(StringUtils.isBlank((String) row.get("tarjeta_adicional")))) {
                    TarjetaOBJ tarjeta = new TarjetaOBJ();
                    tarjeta.setNumeroTarjeta((String) row.get("tarjeta_adicional"));
                    tarjeta.setProveedor((String) row.get("proveedor_tarjeta_adicional"));
                    tarjeta.setTipoTarjeta((String) row.get("tipo_tarjeta_adicional"));
                    tarjetas.add(tarjeta);
                }

                cuentaAspPagoObj.setTarjetas(tarjetas);
            } else {
                log.error("No se encontro nivel de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            cuentaAspPagoObj = null;
        }
        return cuentaAspPagoObj;
    }

    public NivelCuentaOBJ obtenerNivelCuentaAh(String cuentaah) {
        NivelCuentaOBJ nvlCuentaOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerNivelCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                nvlCuentaOBJ = new NivelCuentaOBJ();

                nvlCuentaOBJ.setProductoId((Integer) row.get("producto_ahorro_id"));
                nvlCuentaOBJ.setNivelCuenta((String) row.get("nivel_cuenta"));
            } else {
                log.error("No se encontro nivel de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            nvlCuentaOBJ = null;
        }
        return nvlCuentaOBJ;
    }

    public Double obtenerUdisByNivelCuentaProducto(String nivelCuenta, Integer productoId) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Double result = null;
        try {
            //Se definen los parametros para el query
            parameters.addValue("nivel", nivelCuenta);
            parameters.addValue("productoId", productoId);

            String query = "ahcuentas.obtenerUdisByNivelCuentaProducto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                BigDecimal udis = ((BigDecimal) rows.getFirst().get("udis"));
                result = (udis != null) ? udis.doubleValue() : null;
            } else {
                log.error("No se encontraron Udis para el nivel de la cuenta {} con ProductoId {}", nivelCuenta, productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return result;
    }

    public Double obtenerValorUdis(LocalDate fecha) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        double valorUdiFinal = 0d;
        try {
            int limCiclos = 12;
            while (limCiclos > 0) {
                int anio = fecha.get(ChronoField.YEAR);
                int mes = fecha.get(ChronoField.MONTH_OF_YEAR);
                Map<String, Object> valoresUdi;

                parameters.addValue("anio", anio);
                String query = "ahcuentas.obtenerValorUdis";
                String psql = queries.getProperty(query);
                log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
                valoresUdi = namedProcreaJdbc.queryForMap(psql, parameters);

                if (valoresUdi != null) {
                    String claveMes = "";
                    switch (mes) {
                        case 1:
                            claveMes = "m_01";
                            break;
                        case 2:
                            claveMes = "m_02";
                            break;
                        case 3:
                            claveMes = "m_03";
                            break;
                        case 4:
                            claveMes = "m_04";
                            break;
                        case 5:
                            claveMes = "m_05";
                            break;
                        case 6:
                            claveMes = "m_06";
                            break;
                        case 7:
                            claveMes = "m_07";
                            break;
                        case 8:
                            claveMes = "m_08";
                            break;
                        case 9:
                            claveMes = "m_09";
                            break;
                        case 10:
                            claveMes = "m_10";
                            break;
                        case 11:
                            claveMes = "m_11";
                            break;
                        case 12:
                            claveMes = "m_12";
                            break;
                        default:
                            break;
                    }
                    if (!StringUtils.isBlank(claveMes)) {
                        if (valoresUdi.get(claveMes) != null) {
                            valorUdiFinal = (double) valoresUdi.get(claveMes);
                            log.info("Udi Encontrada en anio: {}, mes: {}, valor: {}", anio, mes, valorUdiFinal);
                            break;
                        }
                    }
                }
                fecha = fecha.minusMonths(1);
                limCiclos--;
            }
        } catch (Exception e) {
            log.error("Ocurrio un error al buscar el valor de la udi::");
            errorHandler.handleException(e);
        }
        return valorUdiFinal;
    }

    public Integer obtenerEstatusCuentaAh(String cuentaah) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer estatus = null;
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerEstatusCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                estatus = (Integer) row.get("estatus_id");
            } else {
                log.error("No se encontro estatus de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return estatus;
    }
}
