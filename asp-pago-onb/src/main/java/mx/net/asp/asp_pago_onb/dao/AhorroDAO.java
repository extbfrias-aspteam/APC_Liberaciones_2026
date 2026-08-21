package mx.net.asp.asp_pago_onb.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
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
        this.procreaJdbc = procreaJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public CuentaAspPagoOBJ obtenerDatosGeneralesCuentaAhByCuentaAh(String cuentaah) {
        CuentaAspPagoOBJ cuentaAspPagoObj = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerDatosGeneralesCuentaAhByCuentaAh";
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
                cuentaAspPagoObj.setEstatus((String) row.get("estatusah"));
                cuentaAspPagoObj.setPersonaId((String) row.get("persona_id"));
            } else {
                log.error("No se encontro la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            cuentaAspPagoObj = null;
        }
        return cuentaAspPagoObj;
    }

    public Boolean validaCuentaTieneBeneficiarioByCuentaAh(String cuentaah) {
        boolean resultado;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_beneficiarios.obtenerBeneficiarioCuentaAhByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                resultado = true;
            } else {
                log.error("No se encontro beneficiario para la cuenta {}", cuentaah);
                resultado = false;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            resultado = false;
        }
        return resultado;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registrarBeneficiarioCuentaAh(BeneficiarioOBJ beneficiario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        Integer id;
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaId", beneficiario.getCuentaId());
            parameters.addValue("nombreBeneficiario", beneficiario.getNombreBeneficiario());
            parameters.addValue("relacionId", beneficiario.getRelacionId());
            parameters.addValue("porcentaje", beneficiario.getPorcentaje());

            String query = "ahcuentas_beneficiarios.guardarBeneficiarioCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                id = (Integer) rows.getFirst().get("id");
                log.info("Id beneficiario:: {}", id);
                beneficiario.setId(id);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(beneficiario));
            } else {
                log.error("No se logro registrar el beneficiario para la cuenta {}", beneficiario.getCuentaah());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el beneficiario para la cuenta " + beneficiario.getCuentaah());
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
    public RespuestaDTO altaCuentaAhorroSimplificada(AhCuentaOBJ cuentaOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer id;
        try {
            // Mapeo de los parametros para el query
            parameters.addValue("cuenta", cuentaOBJ.getCuenta());
            parameters.addValue("estatus_id", cuentaOBJ.getEstatusId());
            parameters.addValue("persona_id", cuentaOBJ.getCliente_id());
            parameters.addValue("producto_ahorro_id", cuentaOBJ.getProductoAhorroId());
            parameters.addValue("monto_apertura", cuentaOBJ.getMontoApertura());
            parameters.addValue("sucursal_id", cuentaOBJ.getSucursalId());
            parameters.addValue("rendimiento", cuentaOBJ.getRendimiento());
            parameters.addValue("gat_nominal", cuentaOBJ.getGatNominal());
            parameters.addValue("gat_real", cuentaOBJ.getGatReal());
            parameters.addValue("moneda_id", cuentaOBJ.getMonedaId());
            parameters.addValue("asesor_id", cuentaOBJ.getAsesorId());
            parameters.addValue("como_entero_id", cuentaOBJ.getComoEnteroId());
            parameters.addValue("como_entero_obs", cuentaOBJ.getComoEnteroObs());
            parameters.addValue("clabe_interbancaria", cuentaOBJ.getClabeInterbancaria());
            parameters.addValue("referencia", cuentaOBJ.getReferencia());
            parameters.addValue("tipo_cliente", cuentaOBJ.getTipoCliente());

            String query = "ahcuentas.altaCuentaAhorroSimplificada";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                id = (Integer) rows.getFirst().get("id");
                log.info("Id de la nueva cuenta: {}", id);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(id));
            } else {
                log.error("No se logro registrar la cuenta: {}", cuentaOBJ.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la cuenta");
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

    public RespuestaDTO buscaCuentaExistenteByCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerCuentaExistenteByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("cuenta"));
            } else {
                log.error("No se encontro cuenta de ahorro con valor cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro cuenta de ahorro");
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

    public RespuestaDTO buscaCuentaExistenteByCuentaClabe(String clabe) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("clabe", clabe);

            String query = "ahcuentas.obtenerCuentaExistenteByCuentaClabe";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("clabe_interbancaria"));
            } else {
                log.error("No se encontro cuenta de ahorro con valor clabe {}", clabe);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro cuenta de ahorro");
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

    public RespuestaDTO obtenerMontoMaxAhorro(String clave) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("clave", clave);

            String query = "ahnivel_cuenta_ahorro.obtenerMontoMaxAhorro";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(row.get("monto_max_mxn")));
            } else {
                log.error("No se encontro monto maximo ahorro con clave {}", clave);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro monto maximo ahorro");
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
    public RespuestaDTO registrarInformacionCuentaPLD(InformacionPldCuentaAhorroOBJ pldOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Mapeo de los parametros para el query
            parameters.addValue("cuenta_id", pldOBJ.getCuentaId());
            parameters.addValue("monto_max_aho", pldOBJ.getMontoMaxAho());
            parameters.addValue("ingresos_men", pldOBJ.getIngresosMen());
            parameters.addValue("puesto", pldOBJ.getPuesto());
            parameters.addValue("estatus_id", pldOBJ.getEstatusId());

            String query = "ahcuentas_pld.guardarInformacionPld";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar la informacion de PLD para la cuenta : {}", pldOBJ.getCuentaId());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la informacion de PLD");
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
    public RespuestaDTO registrarConceptoPld(ConceptoPldOBJ conceptoPldOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Mapeo de los parametros para el query
            parameters.addValue("cuenta_id", conceptoPldOBJ.getCuenta_id());
            parameters.addValue("conceptoId", conceptoPldOBJ.getConceptos_id());
            parameters.addValue("valor", conceptoPldOBJ.getValor());

            String query = "ahconceptos_pld.guardarConceptoPld";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el concepto pld la cuenta : {}", conceptoPldOBJ.getCuenta_id());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el concepto de PLD");
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

    public List<CuentaOBJ> obtenerListadoCuentasCeroByPersonaId(String personaId, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);
            parameters.addValue("productoId", productoId);

            String query = "ahcuentas.obtenerListadoCuentasCeroByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

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

    public List<CuentaOBJ> listarCuentasCeroMovimientoReciente(List<String> cuentas, Integer productoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<CuentaOBJ> cuentasList = new ArrayList<>();
        try {
            parameters.addValue("cuentas", cuentas);

            String query = "ahorro_contrato.listarCuentasCeroMovimientoReciente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

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
                    cuentasList.add(cuentaOBJ);
                }
            } else {
                log.error("No se logro enlistar las cuentas proporcionadas por movimiento mas reciente {} (cero)", cuentas);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
    }

    public Boolean validaReferenciaUtilizada(Integer referencia) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("referencia", referencia);
            String query = "ahcuentas.validaReferenciaAhorro";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
            Integer count = namedCeroJdbc.queryForObject(psql, parameters, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return false;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Integer obtenerSecuenciaReferenciaAhorro() {
        try {
            String psql = queries.getProperty("secuencias.sigSecReferenciaAhorro");
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de referencia ahorro:: " + e.getMessage()) {
            };
        }
    }
}
