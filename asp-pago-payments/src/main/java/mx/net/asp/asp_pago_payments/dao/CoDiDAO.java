package mx.net.asp.asp_pago_payments.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.config.QueryLoader;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.request.ProcesamientoSpeiSimpleReq;
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
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class CoDiDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public CoDiDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RegistroCoDiOBJ consultarRegistroCoDiCliente(String personaId) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.consultarRegistroCoDiCliente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) rows.getFirst().get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) rows.getFirst().get("activo"));
                regCoDiOBJ.setTelefono_validado((String) rows.getFirst().get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) rows.getFirst().get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) rows.getFirst().get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) rows.getFirst().get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) rows.getFirst().get("intentos"));
            } else {
                log.error("No se encontro registro codi del solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }

    public OperacionEjecutadaOBJ buscaOperacionEjecutadaPendiente(String cuentaah) {
        OperacionEjecutadaOBJ operacionEjecutadaOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "operaciones_ejecutadas.buscaOperacionEjecutadaPendiente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                operacionEjecutadaOBJ = new OperacionEjecutadaOBJ();
                operacionEjecutadaOBJ.setId((Integer) row.get("id"));
                operacionEjecutadaOBJ.setFechaCreacion(((Timestamp) row.get("fecha_creacion")));
            } else {
                log.warn("No se encontro operacion pendiente con los siguientes datos:: {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            operacionEjecutadaOBJ = null;
        }
        return operacionEjecutadaOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registraOperacionEjecutada(OperacionEjecutadaOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer idOnboardingEjecutado;
        try {
            req.setId(obtenerSecuenciaOperacionEjecutada());

            parameters.addValue("cuenta", req.getCuenta());
            parameters.addValue("id", req.getId());
            parameters.addValue("id_operacion", req.getIdOperacion());
            parameters.addValue("id_cat_operacion_ejecutada", req.getIdCatOperacionEjecutada());
            parameters.addValue("completada", req.getCompletado());

            String query = "operaciones_ejecutadas.registraOperacionEjecutada";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                idOnboardingEjecutado = (Integer) rows.getFirst().get("id");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(idOnboardingEjecutado));
            } else {
                log.error("No se logro registrar el operacion pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el operacion pendiente");
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
    public RespuestaDTO finalizaOperacionEjecutada(Integer id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);

            String query = "operaciones_ejecutadas.finalizaOperacionEjecutada";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar el operacion pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro finalizar el operacion pendiente");
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
    public RespuestaDTO finalizaOperacionEjecutadaWithObs(Integer id, String obs) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("observacion", obs);

            String query = "operaciones_ejecutadas.finalizaOperacionEjecutadaWithObs";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar el operacion pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro finalizar el operacion pendiente");
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
    public Integer obtenerSecuenciaOperacionEjecutada() {
        try {
            String psql = queries.getProperty("secuencias.sigSecOperacionEjecutada");
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de operaciones ejecutadas:: " + e.getMessage()) {
            };
        }
    }

    public boolean validaEstaEnListaNegra(String valor) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("valor", valor);

            String query = "listanegra_asppago.estaEnListaNegra";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parámetros: {}", query, parameters.getValues());

            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);
            return !rows.isEmpty();

        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void insertarMovimientoSospechoso(ProcesamientoSpeiSimpleReq req, String observaciones, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ctaAhorro", req.getCuentaAhorro())
                .addValue("ctaOrdenante", datosOrdenanteOBJ.getClabe())
                .addValue("ctaBeneficiaria", req.getCuentaBeneficiario())
                .addValue("idBancoBeneficiario", req.getIdInstitucionBeneficiario())
                .addValue("monto", req.getMonto())
                .addValue("refNumerica", req.getReferenciaNumerica())
                .addValue("concepto", req.getConceptoPago())
                .addValue("nombreOrdenante", datosOrdenanteOBJ.getNombreOrdenante())
                .addValue("nombreBeneficiario", req.getNombreBeneficiario())
                .addValue("observaciones", observaciones);

        final String key = "movimientos_asppago_sospechosos.registraMovimientoSospechoso";
        String sql = queries.getProperty(key);

        log.info("Ejecutando INSERT RETURNING: {}, params: {}", key, params.getValues());
        Integer id = namedCeroJdbc.queryForObject(sql, params, Integer.class);
        log.info("Movimiento sospechoso registrado con id: {}", id);
    }

    public FavoritoOBJ obtenerFavoritoPorCuentaBeneficiario(String numeroCuentaOrdenante, String numeroCuentaBeneficiario) {
        FavoritoOBJ favoritoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            parameters.addValue("numero_cuenta_codi", numeroCuentaOrdenante);
            parameters.addValue("numero_cuenta_beneficiario", numeroCuentaBeneficiario);

            String query = "favoritos_transferencias.obtenerFavoritoCuentaByCuentaBeneficiario";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);

            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                favoritoOBJ = new FavoritoOBJ();

                favoritoOBJ.setId((Integer) row.get("id"));
                favoritoOBJ.setNumeroCuentaCoDi((String) row.get("numero_cuenta_codi"));
                favoritoOBJ.setNombreInstitucion((String) row.get("nombre_institucion"));
                favoritoOBJ.setNombreBeneficiario((String) row.get("nombre_beneficiario"));
                favoritoOBJ.setAlias((String) row.get("alias"));
                favoritoOBJ.setNumeroCuentaBeneficiario((String) row.get("numero_cuenta_beneficiario"));
                favoritoOBJ.setIdInstitucionBeneficiario((Integer) row.get("id_institucion_beneficiario"));
                favoritoOBJ.setTipoCuentaBeneficiario((Integer) row.get("id_tipo_cuenta_beneficiario"));
                favoritoOBJ.setFecha_creacion(((Timestamp) row.get("fecha_creacion")).toInstant());
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            favoritoOBJ = null;
        }

        return favoritoOBJ;
    }

    public boolean favoritoConTiempoValido(Integer idFavorito, int minutos) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idFavorito", idFavorito);
        parameters.addValue("minutos", minutos);

        String query = "favoritos_transferencias.esFavoritoConTiempoValido";
        String psql = queries.getProperty(query);

        log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

        return namedCeroJdbc.queryForObject(psql, parameters, Boolean.class);
    }
}
