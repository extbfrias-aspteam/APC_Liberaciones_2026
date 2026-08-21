package mx.net.asp.asp_pago_management.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.dto.CuentaDTO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.request.AltaBeneficiarioReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

@Log4j2
@Repository
public class AhorroDAO {
    private final Properties queriesAhorro;
    private final ErrorHandler errorHandler;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    // private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final String ejecutandoMessage = "Ejecutando consulta: {}, parametros: {}";
    private final String internalErrorMessage = "Error interno";

    public AhorroDAO(
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesAhorro = QueryLoader.loadYaml("cero", "ahorro.yml");
        // this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    public BeneficiarioOBJ obtenerBeneficiarioCuentaAhByCuentaAh(String cuentaah) {
        BeneficiarioOBJ beneficiarioOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_beneficiarios.obtenerBeneficiarioCuentaAhByCuentaAh";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                beneficiarioOBJ = new BeneficiarioOBJ();

                beneficiarioOBJ.setId(((Number) row.get("id")).longValue());
                beneficiarioOBJ.setCuentaId((Integer) row.get("cuenta_id"));
                beneficiarioOBJ.setCuentaah((String) row.get("cuenta"));
                beneficiarioOBJ.setNombreBeneficiario((String) row.get("nombre_beneficiario"));
                beneficiarioOBJ.setRelacionId((Integer) row.get("relacion_id"));
                beneficiarioOBJ.setDetalleEstatus((Boolean) row.get("detalle_estatus"));
                beneficiarioOBJ.setPorcentaje((Integer) row.get("porcentaje"));
            } else {
                log.error("No se encontro beneficiario para la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            beneficiarioOBJ = null;
        }
        return beneficiarioOBJ;
    }

    public RespuestaDTO verificarBeneficiarioAsociadoCuenta(Long idBeneficiario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id_beneficiario", idBeneficiario);

            String query = "ahcuentas_beneficiarios_detalle.verificarBeneficiarioAsociadoCuenta";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("El detalle del beneficiario ya fue registrado");
                respuesta.setData(null);

                return respuesta;
            } else {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
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

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO insertarDetalleBeneficiario(AltaBeneficiarioReq beneficiario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        long id;
        try {
            //Se definen los parametros para el query
            parameters.addValue("fecha_nacimiento", beneficiario.getFecha_nac());
            parameters.addValue("id_beneficiario", beneficiario.getId());
            parameters.addValue("domicilio", beneficiario.getDomicilio());
            parameters.addValue("cp", beneficiario.getCp());
            parameters.addValue("colonia", beneficiario.getColonia());
            parameters.addValue("usuario_creacion", beneficiario.getUsuario_creacion());
            parameters.addValue("ciudad", beneficiario.getCiudad());
            parameters.addValue("no_interior", beneficiario.getNumeroInterior());
            parameters.addValue("calle", beneficiario.getCalle());
            parameters.addValue("no_exterior", beneficiario.getNumeroExterior());
            parameters.addValue("curp", beneficiario.getCurp());
            parameters.addValue("rfc", beneficiario.getRfc());
            parameters.addValue("numero_celular", beneficiario.getNumeroCelular());

            String query = "ahcuentas_beneficiarios_detalle.altaDetalleBeneficiario";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                id = ((Number) rows.getFirst().get("id")).longValue();
                beneficiario.setId(id);

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(beneficiario));
            } else {
                log.error("No se logro registrar el detalle del beneficiario para la cuenta {}", beneficiario.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el detralle del beneficiario para la cuenta " + beneficiario.getCuenta());
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

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarDetalleEstatusBeneficiario(Integer cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta_id", cuenta);

            String query = "ahcuentas_beneficiarios.actualizarDetalleEstatusBeneficiario";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el estatus del detalle del beneficiario");
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

    public Double obtenerSaldoByCuentaAh(String cuentaah) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Double saldo;
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_saldos.obtenerSaldoCero";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                saldo = (Double) row.get("saldo");
            } else {
                log.error("No se encontro saldo de la cuenta {}", cuentaah);
                saldo = 0d;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            saldo = null;
        }
        return saldo;
    }

    public List<CuentaOBJ> obtenerListadoCuentasCeroByPersonaId(String personaId, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("productoId", productoId);

            String query = "ahcuentas.obtenerListadoCuentasCeroByPersonaId";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CuentaOBJ cuentaOBJ = new CuentaOBJ();
                    cuentaOBJ.setCuentaId((Integer) row.get("id"));
                    cuentaOBJ.setCuenta((String) row.get("cuenta"));
                    cuentaOBJ.setEstatus((String) row.get("estatus"));
                    cuentaOBJ.setClabe((String) row.get("clabe_interbancaria"));
                    cuentaOBJ.setProductoId(productoId);
                    cuentaOBJ.setTieneCuenta(true);
                    cuentaOBJ.setTipoCuenta("DEBITO");
                    cuentasList.add(cuentaOBJ);
                }
            } else {
                log.warn("No se encontro listado de cuentas de producto {} (cero)", productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
    }

    public List<CuentaOBJ> obtenerListadoCuentasVigentesCeroByPersonaId(String personaId, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("productoId", productoId);

            String query = "ahcuentas.obtenerListadoCuentasVigentesCeroByPersonaId";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CuentaOBJ cuentaOBJ = new CuentaOBJ();
                    cuentaOBJ.setCuenta((String) row.get("cuenta"));
                    cuentaOBJ.setEstatus((String) row.get("estatus"));
                    cuentaOBJ.setClabe((String) row.get("clabe_interbancaria"));
                    cuentaOBJ.setProductoId(productoId);
                    cuentaOBJ.setTipoCuenta("DEBITO");
                    cuentasList.add(cuentaOBJ);
                }
            } else {
                log.warn("No se encontro listado de cuentas de producto {} (cero)", productoId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
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
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

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
                cuentaAspPagoObj.setTipoCuenta("CUENTA FACIL");

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

    public List<TarjetaOBJ> obtenerTarjetasByCuentaAh(String cuentaah) {
        List<TarjetaOBJ> tarjetas = new ArrayList<>();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
    
        try {
            parameters.addValue("cuentaah", cuentaah);
    
            String query = "ahcuentas.obtenerDatosCuentaAhByCuentaAh";
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());
    
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
    
                if (!StringUtils.isBlank((String) row.get("tarjeta_principal"))) {
                    TarjetaOBJ tarjeta = new TarjetaOBJ();
                    tarjeta.setNumeroTarjeta((String) row.get("tarjeta_principal"));
                    tarjeta.setProveedor((String) row.get("proveedor"));
                    tarjeta.setTipoTarjeta((String) row.get("tipo_tarjeta"));
                    tarjeta.setMostrarTarjetaAPP(1);
                    tarjetas.add(tarjeta);
                }
    
                if (!StringUtils.isBlank((String) row.get("tarjeta_adicional"))) {
                    TarjetaOBJ tarjeta = new TarjetaOBJ();
                    tarjeta.setNumeroTarjeta((String) row.get("tarjeta_adicional"));
                    tarjeta.setProveedor((String) row.get("proveedor_tarjeta_adicional"));
                    tarjeta.setTipoTarjeta((String) row.get("tipo_tarjeta_adicional"));
                    tarjeta.setMostrarTarjetaAPP(1);
                    tarjetas.add(tarjeta);
                }
            } else {
                log.error("No se encontraron tarjetas para la cuenta {}", cuentaah);
            }
    
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
    
        return tarjetas;
    }    

    public NivelCuentaOBJ obtenerNivelCuentaAh(String cuentaah) {
        NivelCuentaOBJ nvlCuentaOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerNivelCuentaAh";
            // String psql = queries.getProperty(query);
            String psql = queriesAhorro.getProperty(query);
            // log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
            log.info(ejecutandoMessage, query, parameters.getValues());

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
            // String psql = queries.getProperty(query);
            String psql = queriesAhorro.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

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
                // String psql = queries.getProperty(query);
                String psql = queriesAhorro.getProperty(query);
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

}
