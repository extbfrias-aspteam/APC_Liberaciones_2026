package mx.net.asp.asp_pago_payments.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.sql.Timestamp;
import java.util.*;

@Log4j2
@Repository
public class ProcreaDAO {
    private final Properties queries;
    private final Properties queriesAhorroContrato;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public ProcreaDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.queriesAhorroContrato = QueryLoader.loadYaml("procrea", "ahorrocontrato.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerAhorroContratoByCuentaah(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd") // Configurar formato de fecha correcto
                .create();
        //Se definen los parametros para el query
        try {
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_contrato.obtenerAhorroContratoByCuentaah";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                AhorroContratoOBJ obj = new AhorroContratoOBJ();

                obj.setSolicitanteNombre((String) row.get("nombre"));
                obj.setSolicitante((String) row.get("solicitante_id"));
                obj.setCuenta((String) row.get("cuenta"));
                obj.setPin((String) row.get("pin"));
                obj.setReferencia((String) row.get("referencia"));
                obj.setContrato((String) row.get("contrato"));
                obj.setTipoAhorroId((Integer) row.get("tipo_ahorro_id"));
                obj.setRendimientoId((Integer) row.get("rendimiento_id"));
                obj.setFechaApertura((Date) row.get("fecha_apertura"));
                obj.setFechaDeposito((Date) row.get("fecha_deposito"));
                obj.setTitularId((String) row.get("titular_id"));
                obj.setSucursalApertura((String) row.get("sucursal_apertura"));

                obj.setAhorroContratoId((Integer) row.get("ahorro_contrato_id"));
                obj.setSaldo((Double) row.get("saldo"));

                obj.setActividadId((Integer) row.get("actividad_id"));
                obj.setSucursalId((Integer) row.get("sucursal_id"));
                obj.setCuentaClabe((String) row.get("cuenta_clabe"));
                obj.setTelefonoCoDi((String) row.get("telefono_codi"));
                obj.setFechaNacimiento((String) row.get("fecha_nacimiento"));

                obj.setCorreoEdocuenta((String) row.get("correo_edocuenta"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(obj));
            } else {
                log.error("No se encontro inflacion vigente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró inflacion vigente");
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
    public RespuestaDTO actualizarSaldoAhorroContrato(String cuentaah, Double saldo) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("saldo", saldo);

            String query = "ahorro_contrato.actualizarSaldoAhorroContrato";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el saldo de ahorro contrto");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el saldo de ahorro contrtos");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerRendimientoVigenteByCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Gson gson = new Gson();
        //Se definen los parametros para el query
        try {
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_rendimiento_vigente.obtenerRendimientoVigenteByCuentaAh";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                AhorroRendimientoVigentesOBJ obj = new AhorroRendimientoVigentesOBJ();

                obj.setRendimientoVigenteId((Integer) row.get("rendimiento_vigente_id"));
                obj.setCuenta((String) row.get("cuenta"));
                obj.setTipoAhorroId((Integer) row.get("tipo_ahorro_id"));
                obj.setPeriodicidad((Integer) row.get("periodicidad"));
                obj.setPlazo((Integer) row.get("plazo"));
                obj.setTipoTasa((String) row.get("tipo_tasa"));
                obj.setCalculoIva((String) row.get("calculo_iva"));
                obj.setTasaInt((Double) row.get("tasa_int"));
                obj.setTasaId((Integer) row.get("tasa_id"));
                obj.setBase((Integer) row.get("base"));
                obj.setFormula((String) row.get("formula"));
                obj.setTipoCorte((String) row.get("tipo_corte"));
                obj.setDeposito((String) row.get("deposito"));
                obj.setNumDias((Integer) row.get("num_dias"));
                obj.setDiasGracia((Integer) row.get("dias_gracia"));
                obj.setCtaContable((String) row.get("cta_contable"));
                obj.setVigenciaDesde((Date) row.get("vigencia_desde"));
                obj.setVigenciaHasta((Date) row.get("vigencia_hasta"));
                obj.setCondicionesRetiros((String) row.get("condiciones_retiros"));
                obj.setCondicionesApertura((String) row.get("condiciones_apertura"));
                obj.setCreadoPor((Integer) row.get("creado_por"));
                obj.setFechaCreacion((Timestamp) row.get("fecha_creacion"));
                obj.setModificadoPor((Integer) row.get("modificado_por"));
                obj.setFechaModificacion((Timestamp) row.get("fecha_modificacion"));
                obj.setRendimientoId((Integer) row.get("rendimiento_id"));
                obj.setGat((Double) row.get("gat"));
                obj.setTipoCapitalizarId((Integer) row.get("tipo_capitalizar_id"));
                obj.setNoDisposicion((Integer) row.get("no_disposicion"));
                obj.setEstatus((String) row.get("estatus"));
                obj.setTipo((Integer) row.get("tipo"));
                obj.setCapital((Double) row.get("capital"));
                obj.setFechaInicio((Date) row.get("fecha_inicio"));
                obj.setFechaFinal((Date) row.get("fecha_final"));
                obj.setInteres((Double) row.get("interes"));
                obj.setFechaDeposito((Date) row.get("fecha_deposito"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(obj));
            } else {
                log.error("No se encontro rendimiento vigente para la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró rendimiento vigente");
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
    public RespuestaDTO actualizarRendimientoVigenteById(AhorroRendimientoVigentesOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("tipoAhorroId", req.getTipoAhorroId());
            parameters.addValue("periodicidad", req.getPeriodicidad());
            parameters.addValue("plazo", req.getPlazo());
            parameters.addValue("tipoTasa", req.getTipoTasa());
            parameters.addValue("calculoIva", req.getCalculoIva());
            parameters.addValue("tasaInt", req.getTasaInt());
            parameters.addValue("tasaId", req.getTasaId());
            parameters.addValue("base", req.getBase());
            parameters.addValue("formula", req.getFormula());
            parameters.addValue("tipoCorte", req.getTipoCorte());
            parameters.addValue("deposito", req.getDeposito());
            parameters.addValue("numDias", req.getNumDias());
            parameters.addValue("diasGracia", req.getDiasGracia());
            parameters.addValue("ctaContable", req.getCtaContable());
            parameters.addValue("vigenciaDesde", req.getVigenciaDesde());
            parameters.addValue("vigenciaHasta", req.getVigenciaHasta());
            parameters.addValue("condicionesRetiros", req.getCondicionesRetiros());
            parameters.addValue("condicionesApertura", req.getCondicionesApertura());
            parameters.addValue("creadoPor", req.getCreadoPor());
            parameters.addValue("fechaCreacion", req.getFechaCreacion());
            parameters.addValue("modificadoPor", req.getModificadoPor());
            parameters.addValue("fechaModificacion", req.getFechaModificacion());
            parameters.addValue("rendimientoId", req.getRendimientoId());
            parameters.addValue("gat", req.getGat());
            parameters.addValue("tipoCapitalizarId", req.getTipoCapitalizarId());
            parameters.addValue("noDisposicion", req.getNoDisposicion());
            parameters.addValue("estatus", req.getEstatus());
            parameters.addValue("tipo", req.getTipo());
            parameters.addValue("capital", req.getCapital());
            parameters.addValue("fechaInicio", req.getFechaInicio());
            parameters.addValue("fechaFinal", req.getFechaFinal());
            parameters.addValue("interes", req.getInteres());
            parameters.addValue("fechaDeposito", req.getFechaDeposito());
            parameters.addValue("rendimientoVigenteId", req.getRendimientoVigenteId());

            String query = "ahorro_rendimiento_vigente.actualizarRendimientoVigenteById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el rendimiento vigente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el rendimiento vigente");
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

    public RespuestaDTO obtenerAhorroPagareByCunetah(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Gson gson = new Gson();
        //Se definen los parametros para el query
        try {
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_pagares.obtenerAhorroPagareByCunetah";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                AhorroPagareOBJ obj = new AhorroPagareOBJ();

                obj.setPagareId((Integer) row.get("pagare_id"));
                obj.setCuenta((String) row.get("cuenta"));
                obj.setNumero((Long) row.get("numero"));
                obj.setMonto((Double) row.get("monto"));
                obj.setFechaInicio((Date) row.get("fecha_inicio"));
                obj.setFechaFinal((Date) row.get("fecha_final"));
                obj.setCreadoPor((Integer) row.get("creado_por"));
                obj.setFechaCreacion((Date) row.get("fecha_creacion"));
                obj.setModificadoPor((Integer) row.get("modificado_por"));
                obj.setFechaModificacion((Date) row.get("fecha_modificacion"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(obj));
            } else {
                log.error("No se encontro ahorro pagare para la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró ahorro pagare");
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
    public RespuestaDTO nuevoAhorroPagare(AhorroPagareOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setPagareId(obtenerSecuenciaAhorroPagare());

            parameters.addValue("pagare_id", req.getPagareId());
            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("numero", req.getNumero());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("fecha_inicio", req.getFechaInicio());
            parameters.addValue("fecha_final", req.getFechaFinal());
            parameters.addValue("creado_por", req.getCreadoPor());
            parameters.addValue("fecha_creacion", req.getFechaCreacion());
            parameters.addValue("modificado_por", req.getModificadoPor());
            parameters.addValue("fecha_modificacion", req.getFechaModificacion());

            String query = "ahorro_pagares.nuevoAhorroPagare";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el ahorro pagare");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro guardar el ahorro pagare");
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

    public RespuestaDTO realizarPagoCreditoProcrea(String cuentaah, String control, Double monto, Integer accesoId, Integer banco, Integer formaPago) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            //Se definen los parametros para el query
            parameters.addValue("ctaOrigen", cuentaah);
            parameters.addValue("control", control);
            parameters.addValue("monto", monto);
            parameters.addValue("accesoId", accesoId);
            parameters.addValue("bancoId", banco);
            parameters.addValue("formaPago", formaPago);

            String query = "funciones.ahorro_pagocredito_cero";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                String resultado = (String) row.get("valor");
                if (resultado.equals("OK")) {
                    respuesta.setCodigo(0);
                    respuesta.setMensaje("OK");
                    respuesta.setData(null);
                } else {
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("No se logró realizar el pago del credito");
                    respuesta.setData(null);
                }
            } else {
                log.error("No se logró realizar el pago del credito {}", control);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró realizar el pago del credito");
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

    public Boolean validaCreditoConsolidado(String control, String operacion) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Boolean resultado = false;
        try {
            //Se definen los parametros para el query
            parameters.addValue("control", control);
            parameters.addValue("operacion", operacion);

            String query = "funciones.valida_cre_consolidado";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                resultado = (Boolean) row.get("resultado");
            } else {
                log.error("No se logró validar el credito {}", control);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró validar el credito");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return resultado;
    }

    public Boolean validaPermisoTesorero(Integer usuarioId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Boolean resultado = false;
        try {
            //Se definen los parametros para el query
            parameters.addValue("usuario", usuarioId);

            String query = "funciones.valida_permiso_tesorero";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                resultado = (Boolean) row.get("permiso");
            } else {
                log.error("No se logró validar el permiso tesorero del usuario {}", usuarioId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró validar el permiso tesorero");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return resultado;
    }

    public String obtenerEstatusCuentaAh(String cuentaah) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        String estatus = null;
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahorro_contrato.obtenerEstatusCuentaAh";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                estatus = (String) row.get("estatus");
            } else {
                log.error("No se encontro estatus de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return estatus;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaAhorroPagare() {
        try {
            String psql = queriesAhorroContrato.getProperty("secuencias.sigSecAhorroPagare");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de ahorro pagare:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaAccesoId() {
        try {
            String psql = queries.getProperty("secuencias.siSecAccesoId");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de acceso id:: " + e.getMessage()) {
            };
        }
    }

    public boolean esPagoCreditoProcreaReciente(String cuentaah, Double monto, Integer idMovimiento, String segundos) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("monto", monto);
            parameters.addValue("idMovimiento", idMovimiento);
            parameters.addValue("segundos", segundos);

            String query = "movimientos_caja.esPagoCreditoProcreaReciente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parámetros: {}", query, parameters.getValues());
    
            List<Map<String, Object>> rows = namedProcreaJdbc.queryForList(psql, parameters);
            return !rows.isEmpty();
    
        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
    }
}
