package mx.net.asp.asp_pago_payments.dao;

import com.google.gson.*;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.config.QueryLoader;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Log4j2
@Repository
public class MovimientosCajaDAO {
    private final Properties queries;
    //private final Properties queriesAhorroContrato;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public MovimientosCajaDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "movimientoscaja.yml");
        //this.queriesAhorroContrato = QueryLoader.loadYaml("procrea", "ahorrocontrato.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerAhorroMovimientoById(Integer movimiento_id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("movimiento_id", movimiento_id);

            String query = "movimientos_caja.obtenerAhorroMovimientoById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                AhorroMovimientoOBJ ahorroMovimientoOBJ = new AhorroMovimientoOBJ();

                ahorroMovimientoOBJ.setMovimientoId((Integer) row.get("movimiento_id"));
                ahorroMovimientoOBJ.setDescripcion((String) row.get("descripcion"));
                ahorroMovimientoOBJ.setOperacion((String) row.get("operacion"));
                ahorroMovimientoOBJ.setSalvoBuenCobro((String) row.get("salvo_buen_cobro"));
                ahorroMovimientoOBJ.setCuentaContable((String) row.get("cuenta_contable"));
                ahorroMovimientoOBJ.setProvisionar((String) row.get("provisionar"));
                ahorroMovimientoOBJ.setManual((String) row.get("manual"));
                ahorroMovimientoOBJ.setCreadoPor((Integer) row.get("creado_por"));
                ahorroMovimientoOBJ.setFechaCreacion((Date) row.get("fecha_creacion"));
                ahorroMovimientoOBJ.setModificadoPor((Integer) row.get("modificado_por"));
                ahorroMovimientoOBJ.setFechaModificacion((Date) row.get("fecha_modificacion"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(ahorroMovimientoOBJ));
            } else {
                log.error("No se encontro informacion para el movimiento con id {}", movimiento_id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró información para el movimiento con id " + movimiento_id);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registraMovimientoCaja(MovimientosCajaOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setMovimientoId(obtenerSecuenciaMovimientosCaja());

            parameters.addValue("movimiento_id", req.getMovimientoId());
            parameters.addValue("caja_id", req.getCajaId());
            parameters.addValue("cajero_id", req.getCajeroId());
            parameters.addValue("fecha", req.getFecha());
            parameters.addValue("tipo_mov_id", req.getTipoMovId());
            parameters.addValue("moneda_id", req.getMonedaId());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("tipo_cambio", req.getTipoCambio());
            parameters.addValue("forma_pago", req.getFormaPago());
            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("control", req.getControl());
            parameters.addValue("banco_id", req.getBancoId());
            parameters.addValue("cheque", req.getCheque());
            parameters.addValue("estatus", req.getEstatus());
            parameters.addValue("obs", req.getObs());
            parameters.addValue("poliza", req.getPoliza());
            parameters.addValue("referencia_movimiento_id", req.getReferenciaMovimientoId());
            parameters.addValue("banco_origen", req.getBancoOrigen());
            parameters.addValue("fecha_deposito", req.getFechaDeposito());
            parameters.addValue("poliza_provision", req.getPolizaProvision());
            parameters.addValue("transaccion_id", req.getTransaccionId());
            parameters.addValue("tarjeta_operativa_id", req.getTarjetaOperativaOd());
            parameters.addValue("region_id", req.getRegionId());
            parameters.addValue("id_importacion", req.getIdImportacion());
            parameters.addValue("moneda_valor", req.getMonedaValor());
            parameters.addValue("moneda_nombre", req.getMonedaNombre());
            parameters.addValue("moneda_fecha_valor", req.getMonedaFechaValor());
            parameters.addValue("monto_original", req.getMontoOriginal());
            parameters.addValue("monto_destino", req.getMontoDestino());
            parameters.addValue("moneda_error", req.getMonedaError());
            parameters.addValue("creado_por", req.getCreadoPor());
            parameters.addValue("modificado_por", req.getModificadoPor());

            String query = "movimientos_caja.registraMovimientoCaja";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(req.getMovimientoId()));
            } else {
                log.error("No se logro registrar el movimiento caja");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el movimiento caja");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registraAhorroDisposicion(AhorroDisposicionesOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setId(obtenerSecuenciaAhorroDisposicion());

            parameters.addValue("id", req.getId());
            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("fecha", req.getFecha());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("forma_pago_id", req.getFormaPagoId());
            parameters.addValue("banco_id", req.getBancoId());
            parameters.addValue("cheque", req.getCheque());
            parameters.addValue("movimiento_id", req.getMovtoId());
            parameters.addValue("creado_por", req.getCreadoPor());
            parameters.addValue("transaccion_id", req.getTransaccionId());
            parameters.addValue("tarjeta_operativa_id", req.getTarjetaOperativaId());
            parameters.addValue("app", req.getApp());
            parameters.addValue("transaccion_version_id", req.getTransaccionVersionId());
            parameters.addValue("aviso_id", req.getAvisoId());

            String query = "ahorro_disposiciones.registraAhorroDisposicion";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(req.getId()));
            } else {
                log.error("No se logro registrar el ahorro disposicion");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el ahorro disposicion");
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
    public RespuestaDTO registraGeolocalizacionMovimientoCaja(MovimientosCajaOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("movimiento_id", req.getMovimientoId());
            parameters.addValue("usuario_creacion", req.getCreadoPor());
            parameters.addValue("latitud", req.getLatitud());
            parameters.addValue("longitud", req.getLongitud());

            String query = "movimientos_caja.registraGeolocalizacionMovimientoCaja";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el movimiento caja");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el movimiento caja");
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

    public RespuestaDTO obtenerAhorroSaldoByCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd") // Configurar formato de fecha correcto
                .create();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_saldos.obtenerAhorroSaldoByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                AhorroSaldosOBJ ahorroSaldosOBJ = new AhorroSaldosOBJ();

                ahorroSaldosOBJ.setCuenta((String) row.get("cuenta"));
                ahorroSaldosOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                ahorroSaldosOBJ.setSaldoReal((Double) row.get("saldo_real"));
                ahorroSaldosOBJ.setSaldoPromedio((Double) row.get("saldo_promedio"));
                ahorroSaldosOBJ.setSaldoAcumulado((Double) row.get("saldo_acumulado"));
                ahorroSaldosOBJ.setFechaCorte((Date) row.get("fecha_corte"));
                ahorroSaldosOBJ.setFechaDeposito((Date) row.get("fecha_deposito"));
                ahorroSaldosOBJ.setIntereses((Double) row.get("intereses"));
                ahorroSaldosOBJ.setIva((Double) row.get("iva"));
                ahorroSaldosOBJ.setIsr((Double) row.get("isr"));
                ahorroSaldosOBJ.setRetenciones((Double) row.get("retenciones"));
                ahorroSaldosOBJ.setDesviacion((Double) row.get("desviacion"));
                ahorroSaldosOBJ.setDias((Integer) row.get("dias"));
                ahorroSaldosOBJ.setSaldoDisponible((Double) row.get("saldo_disponible"));
                ahorroSaldosOBJ.setAhorroSaldosId((Integer) row.get("ahorro_saldos_id"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(ahorroSaldosOBJ));
            } else {
                log.error("No se encontro informacion del saldo de la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró información del saldo de la cuenta " + cuentaah);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registraAhorroSaldos(AhorroSaldosOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setAhorroSaldosId(obtenerSecuenciaAhorroSaldos());

            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("solicitante_id", req.getSolicitanteId());
            parameters.addValue("saldo_real", req.getSaldoReal());
            parameters.addValue("saldo_promedio", req.getSaldoPromedio());
            parameters.addValue("saldo_acumulado", req.getSaldoAcumulado());
            parameters.addValue("fecha_corte", req.getFechaCorte());
            parameters.addValue("fecha_deposito", req.getFechaDeposito());
            parameters.addValue("intereses", req.getIntereses());
            parameters.addValue("iva", req.getIva());
            parameters.addValue("isr", req.getIsr());
            parameters.addValue("retenciones", req.getRetenciones());
            parameters.addValue("desviacion", req.getDesviacion());
            parameters.addValue("dias", req.getDias());
            parameters.addValue("saldo_disponible", req.getSaldoDisponible());
            parameters.addValue("ahorro_saldos_id", req.getAhorroSaldosId());
            parameters.addValue("ide", req.getIde());

            String query = "ahorro_saldos.registraAhorroSaldos";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(req.getAhorroSaldosId()));
            } else {
                log.error("No se logro registrar el ahorro saldos");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el ahorro saldos");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO actualizaAhorroSaldos(AhorroSaldosOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("saldo_real", req.getSaldoReal());
            parameters.addValue("saldo_acumulado", req.getSaldoAcumulado());
            parameters.addValue("saldo_disponible", req.getSaldoDisponible());
            parameters.addValue("ahorro_saldos_id", req.getAhorroSaldosId());

            String query = "ahorro_saldos.actualizaAhorroSaldos";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(req.getAhorroSaldosId()));
            } else {
                log.error("No se logro actualizar el ahorro saldos");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el ahorro saldos");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registraAhorroTransferencia(AhorroTransferenciaOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setId(obtenerSecuenciaAhorroTransferencias());

            parameters.addValue("id", req.getId());
            parameters.addValue("cuenta_ori", req.getCuentaOrigen());
            parameters.addValue("cuenta_des", req.getCuentaDestino());
            parameters.addValue("fecha", req.getFecha());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("movimiento_id", req.getMovimientoId());
            parameters.addValue("creado_por", req.getCreadoPor());
            parameters.addValue("deposito_id", req.getDepositoId());
            parameters.addValue("disposicion_id", req.getDisposicionId());

            String query = "ahorro_transferencias.registraAhorroTransaferencia";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el ahorro transferencia");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el ahorro transferencia");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaMovimientosCaja() {
        try {
            String psql = queries.getProperty("secuencias.sigSecMovimientosCaja");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de movimientos caja:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaAhorroDisposicion() {
        try {
            String psql = queries.getProperty("secuencias.sigSecAhorroDisposicion");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de ahorro disposicion:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaAhorroSaldos() {
        try {
            String psql = queries.getProperty("secuencias.sigSecAhorroSaldos");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de ahorro saldos:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaAhorroTransferencias() {
        try {
            String psql = queries.getProperty("secuencias.sigSecAhorroTransferencias");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de ahorro transferencias:: " + e.getMessage()) {
            };
        }
    }
}
