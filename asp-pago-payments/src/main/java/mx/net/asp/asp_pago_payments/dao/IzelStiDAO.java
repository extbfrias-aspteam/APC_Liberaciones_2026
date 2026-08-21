package mx.net.asp.asp_pago_payments.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.config.QueryLoader;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.model.spei.Aplicaciones;
import mx.net.asp.asp_pago_payments.model.spei.Cuentas;
import mx.net.asp.asp_pago_payments.model.spei.SpeiOutgoing;
import mx.net.asp.asp_pago_payments.model.spei.UsuariosAutorizadosOBJ;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Repository
public class IzelStiDAO {
    private final Properties queries;
    private final JdbcTemplate izelSTIJdbc;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedIzelSTIJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public IzelStiDAO(
            @Qualifier("izelJdbcTemplate") JdbcTemplate izelSTIJdbc,
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedIzelSTIJdbcTemplate") NamedParameterJdbcTemplate namedIzelSTIJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("izelSTI", "public.yml");
        this.izelSTIJdbc = izelSTIJdbc;
        this.ceroJdbc = ceroJdbc;
        this.namedIzelSTIJdbc = namedIzelSTIJdbc;
        this.errorHandler = errorHandler;
    }

    public List<CatInstiOBJ> obtenerListadoInstituciones() {
        List<Map<String, Object>> rows;
        List<CatInstiOBJ> institucionesList = new ArrayList<>();
        try {
            String query = "spei_instituciones.obtenerListadoInstituciones";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            rows = izelSTIJdbc.queryForList(psql);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    CatInstiOBJ instiObj = new CatInstiOBJ();
                    instiObj.setClave_institucion((Integer) row.get("id_institucion"));
                    instiObj.setNombreCorto(((String) row.get("descripcion")).trim());
                    institucionesList.add(instiObj);
                }
            } else {
                log.error("No se encontraron registros de instituciones bancarias");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return institucionesList.isEmpty() ? Collections.emptyList() : institucionesList;
    }

    public Boolean buscarSpeiDuplicados(String cuentaOrdenante, String cuentaBeneficiario, String conceptoPago,
                                        BigDecimal monto, Integer idInstitucionBen, Date fechaCaptura, Integer referencia, String claveRastreo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        boolean resultado = false;
        SimpleDateFormat sdf;
        Date dtfecha;
        String sfecha;
        try {

            sdf = new SimpleDateFormat("yyyy/MM/dd");
            sfecha = sdf.format(fechaCaptura);
            dtfecha = sdf.parse(sfecha);

            //Se definen los parametros para el query
            parameters.addValue("cuenta_ordenante", cuentaOrdenante);
            parameters.addValue("cuenta_beneficiario", cuentaBeneficiario);
            parameters.addValue("concepto_pago", conceptoPago);
            parameters.addValue("monto", monto);
            parameters.addValue("id_institucion_ben", idInstitucionBen);
            parameters.addValue("fecha_captura", dtfecha);
            parameters.addValue("referencia_numerica", referencia);
            parameters.addValue("clave_rastreo", claveRastreo);

            String query = "spei_outgoing.buscarSpeiDupliclados";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                resultado = true;
            } else {
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return resultado;
    }

    public RespuestaDTO buscarAplicacionCompleto(Long aplicacionId, Long empresaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("aplicacionId", aplicacionId);
            parameters.addValue("empresaId", empresaId);

            String query = "aplicaciones.buscarAplicacionCompleto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                Aplicaciones obj = new Aplicaciones();

                obj.setId((String) row.get("id"));
                obj.setEstatusId((String) row.get("estatus_id"));
                obj.setFechaBaja((Date) row.get("fecha_baja"));
                obj.setMotivoBajaId((String) row.get("motivo_baja_id"));
                obj.setFechaCreacion((Date) row.get("fecha_creacion"));
                obj.setEmpresaId((String) row.get("empresa_id"));
                obj.setDescripcion((String) row.get("descripcion"));
                obj.setClave((String) row.get("clave"));
                obj.setClaveCore((String) row.get("clave_core"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(obj));
            } else {
                log.error("No se encontro aplicacion para el id {}", aplicacionId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro aplicacion");
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

    public RespuestaDTO buscarCuentaSpeiCompleto(String clabe) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("clabe", clabe);

            String query = "spei_cuentas.buscarCuentaSpeiCompleto";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                Cuentas obj = new Cuentas();

                obj.setId((String) row.get("ID"));
                obj.setEstatusId((String) row.get("ESTATUS_ID"));
                obj.setFechaBaja((Date) row.get("FECHA_BAJA"));
                obj.setMotivoBajaId((String) row.get("MOTIVO_BAJA_ID"));
                obj.setFechaCreacion((Date) row.get("FECHA_CREACION"));
                obj.setEmpresaId((String) row.get("EMPRESA_ID"));
                obj.setCuentaClabe((String) row.get("CUENTA_CLABE"));
                obj.setAplicacionId((String) row.get("APLICACION_ID"));
                obj.setClabe2((String) row.get("CLABE2"));
                obj.setTarjeta((String) row.get("TARJETA"));
                obj.setTelefono((String) row.get("TELEFONO"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(obj));
            } else {
                log.error("No se encontro cuenta spei para la clabe {}", clabe);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro cuenta spei");
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

    public RespuestaDTO obtenerUsuariosAutorizados(Long usuario, Long modulo, Long empresaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<UsuariosAutorizadosOBJ> usuariosList = new ArrayList<>();
        try {
            parameters.addValue("usuario", usuario);
            parameters.addValue("modulo", modulo);
            parameters.addValue("empresaId", empresaId);

            String query = "usuarios_autorizados.buscarUsuariosAutorizados";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    UsuariosAutorizadosOBJ obj = new UsuariosAutorizadosOBJ();
                    obj.setId((String) row.get("id"));
                    obj.setEstatusId((String) row.get("estatus_id"));
                    obj.setFechaBaja((java.sql.Date) row.get("fecha_baja"));
                    obj.setMotivoBajaId((String) row.get("motivo_bajaId"));
                    obj.setFechaCreacion((java.sql.Date) row.get("fecha_creacion"));
                    obj.setEmpresaId((String) row.get("empresa_id"));
                    obj.setMontoInferior((BigDecimal) row.get("monto_inferior"));
                    obj.setMontoSuperior((BigDecimal) row.get("monto_superior"));
                    obj.setModulo((String) row.get("modulo"));
                    obj.setAutorizado((String) row.get("autorizado"));
                    obj.setFechaIni((java.sql.Date) row.get("fecha_ini"));
                    obj.setFechaFin((java.sql.Date) row.get("fecha_fin"));
                    obj.setCodigo((String) row.get("codigo"));
                    obj.setNombreUsuario((String) row.get("nombre_usuario"));
                    usuariosList.add(obj);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(usuariosList));
            } else {
                log.error("No se encontraron usuarios autorizados");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron usuarios autorizados");
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

    @Transactional(transactionManager = "transactionManagerIzelSTI")
    public RespuestaDTO registraSpeiOutgoingSti(SpeiOutgoing req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            req.setId(obtenerSecuenciaSpeiOutgoingSTI());

            parameters.addValue("id_spei_outgoing", req.getId());
            parameters.addValue("nombre_ordenante", req.getNombreOrdenante());
            parameters.addValue("id_tipo_cuenta_ordenante", req.getIdTipoCuentaOrdenante());
            parameters.addValue("cuenta_ordenante", req.getCuentaOrdenante());
            parameters.addValue("rfc_ordenante", req.getRfcOrdenante());
            parameters.addValue("nombre_beneficiario", req.getNombreBeneficiario());
            parameters.addValue("id_tipo_cuenta_beneficiario", req.getIdTipoCuentaBeneficiario());
            parameters.addValue("cuenta_beneficiario", req.getCuentaBeneficiario());
            parameters.addValue("rfc_beneficiario", req.getRfcBeneficiario());
            parameters.addValue("concepto_pago", req.getConceptoPago());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("iva", req.getIva());
            parameters.addValue("referencia_numerica", req.getReferenciaNumerica());
            parameters.addValue("referencia", req.getReferencia());
            parameters.addValue("id_tipo_pago", req.getIdTipoPago());
            parameters.addValue("id_institucion_ben", req.getIdInstitucionBen());
            parameters.addValue("status", req.getStatus());
            parameters.addValue("envio_automatico", req.getEnvioAutomatico());
            parameters.addValue("tipo_operacion", req.getIdTipoOperacion());
            parameters.addValue("status_operacion", req.getStatusOperacion());
            parameters.addValue("clave_rastreo", req.getClaveRastreo());
            parameters.addValue("id_devolucion", req.getIdDevolucion());
            parameters.addValue("fecha_captura", req.getFechaCaptura());
            parameters.addValue("fh_operacion", req.getFhOperacion());
            parameters.addValue("id_operacion", req.getIdOperacion());
            parameters.addValue("referencia_cobranza", req.getReferenciaCobranza());
            parameters.addValue("id_area_emite", req.getIdAreaEmite());
            parameters.addValue("topologia", req.getTopologia());
            parameters.addValue("verificado", req.getVerificado());
            parameters.addValue("clave_pago", req.getClavePago());
            parameters.addValue("prioridad", req.getPrioridad());
            parameters.addValue("inf_adicional", req.getInfAdicional());
            parameters.addValue("app", req.getApp());
            parameters.addValue("modificado_por", req.getModificadoPor());
            parameters.addValue("fecha_modificacion", req.getFechaModificacion());
            parameters.addValue("usuario_id", req.getUsuarioId());
            parameters.addValue("sucursal_id", req.getSucursalId());
            parameters.addValue("empresa_id", req.getEmpresaId());
            parameters.addValue("usuario", req.getUsuario());
            parameters.addValue("correo_electronico", req.getCorreoElectronico());
            parameters.addValue("correo_beneficiario", req.getCorreoBeneficiario());
            parameters.addValue("procesado", req.getProcesado());
            parameters.addValue("core_id", req.getCoreId());
            parameters.addValue("observaciones", req.getObservaciones());
            parameters.addValue("firma", req.getFirma());
            parameters.addValue("llave_aes", req.getLlaveAES());
            parameters.addValue("cuenta_ordenante_cf", req.getCuentaOrdenanteCf());
            parameters.addValue("nombre_ordenante_cf", req.getNombreOrdenanteCf());
            parameters.addValue("cuenta_beneficiario_cf", req.getCuentaBeneficiarioCf());
            parameters.addValue("nombre_beneficiario_cf", req.getNombreBeneficiarioCf());
            parameters.addValue("monto_cf", req.getMontoCf());
            parameters.addValue("iva_cf", req.getIvaCf());
            parameters.addValue("concepto_pago_cf", req.getConceptoPagoCf());
            parameters.addValue("movil_ordenante", req.getMovilOrdenante());
            parameters.addValue("movil_ordenante_cfr", req.getMovilOrdenanteCfr());
            parameters.addValue("movil_beneficiario", req.getMovilBeneficiario());
            parameters.addValue("movil_beneficiario_cfr", req.getMovilBeneficiarioCfr());
            parameters.addValue("num_folio_esquema_codi", req.getNumFolioEsquemaCoDi());
            parameters.addValue("num_folio_esquema_codi_cfr", req.getNumFolioEsquemaCoDiCfr());
            parameters.addValue("pago_comision", req.getPagoComision());
            parameters.addValue("pago_comision_cfr", req.getPagoComisionCfr());
            parameters.addValue("monto_comision", req.getMontoComision());
            parameters.addValue("monto_comision_cfr", req.getMontoComisionCfr());
            parameters.addValue("fh_hr_limite_de_pago", req.getFhHrLimiteDePago());
            parameters.addValue("num_serie_cert_comercio", req.getNumSerieCertComercio());
            parameters.addValue("num_serie_cert_comercio_cfr", req.getNumSerieCertComercioCfr());
            parameters.addValue("nombre_beneficiario2", req.getNombreBeneficiario2());
            parameters.addValue("nombre_beneficiario2_cfr", req.getNombreBeneficiario2Cfr());
            parameters.addValue("rfc_beneficiario2", req.getRfcBeneficiario2());
            parameters.addValue("rfc_beneficiario2_cfr", req.getRfcBeneficiario2Cfr());
            parameters.addValue("cuenta_beneficiario2", req.getCuentaBeneficiario2());
            parameters.addValue("cuenta_beneficiario2_cfr", req.getCuentaBeneficiario2Cfr());
            parameters.addValue("id_tipo_cuenta_beneficiario2", req.getIdTipoCuentaBeneficiario2());
            parameters.addValue("dv_movil_ordenante", req.getDvMovilOrdenante());
            parameters.addValue("dv_movil_ordenante_cfr", req.getDvMovilOrdenanteCfr());
            parameters.addValue("dv_movil_beneficiario", req.getDvMovilBeneficiario());
            parameters.addValue("dv_movil_beneficiario_cfr", req.getDvMovilBeneficiarioCfr());

            String query = "spei_outgoing.registraSpeiOutgoing";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedIzelSTIJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(req.getId()));
            } else {
                log.error("No se logro registrar el spei out");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el spei out");
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

    @Transactional(transactionManager = "transactionManagerIzelSTI")
    public Integer speiReferenciaSti() {
        try {
            String psql = queries.getProperty("secuencias.speiReferenciaSti");
            return izelSTIJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de referencia Sti:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerIzelSTI")
    public Integer referenciaQR() {
        try {
            String psql = queries.getProperty("secuencias.referenciaQR");
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de referenciaQR:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerIzelSTI")
    public Long obtenerSecuenciaSpeiOutgoingSTI() {
        try {
            String psql = queries.getProperty("secuencias.sigSecSpeiOutgoing");
            return izelSTIJdbc.queryForObject(psql, Long.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de spei outgoing:: " + e.getMessage()) {
            };
        }
    }
}
