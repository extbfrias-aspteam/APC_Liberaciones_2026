package mx.net.asp.asp_pago_payments.dao;

import com.google.gson.Gson;
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class AhorroDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public AhorroDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
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

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO grabarTraspasoCuentas(String tipoTraspaso, String cuentaOrigen, String cuentaDetino, Integer idRetiro, Integer idDeposito, Double monto) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        Integer idSecTraspaso;
        try {
            idSecTraspaso = obtenerSecuenciaTraspasoCuentas();

            parameters.addValue("id", idSecTraspaso);
            parameters.addValue("tipo_trasp", tipoTraspaso);
            parameters.addValue("cuenta_ori", cuentaOrigen);
            parameters.addValue("cuenta_dest", cuentaDetino);
            parameters.addValue("id_retiro", idRetiro);
            parameters.addValue("id_deposito", idDeposito);
            parameters.addValue("monto", monto);

            String query = "ahtraspasos_cuentas.grabarTraspasoCuentas";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el traspaso entre cuentas");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el traspaso entre cuentas");
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
    public RespuestaDTO registraAhorroTraspasoEiYu(TraspasoEiYuOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        Long id;
        try {

            parameters.addValue("idretiro", req.getIdRetiro());
            parameters.addValue("iddeposito", req.getIdDeposito());
            parameters.addValue("ctaorigen", req.getCuentaOrigen());
            parameters.addValue("nomborigen", req.getNombreOrdenante());
            parameters.addValue("ctadestino", req.getCuentaDestino());
            parameters.addValue("nombdestino", req.getNombreBeneficiario());
            parameters.addValue("monto", req.getMonto());
            parameters.addValue("conceptopago", req.getConceptoPago());
            parameters.addValue("refnum", req.getReferenciaNumerica());
            parameters.addValue("bancoorigen", req.getBancoOrigen());
            parameters.addValue("idtipopago", req.getIdTipoPago());
            parameters.addValue("cverastreo", req.getCveRastreo());
            parameters.addValue("estatus", req.getEstatus());
            parameters.addValue("observaciones", req.getObservaciones());
            parameters.addValue("no_intento", req.getIntentos());
            parameters.addValue("tipocuentaah", req.getTipoCuentaAh());
            parameters.addValue("latitud", req.getLatitud());
            parameters.addValue("longitud", req.getLongitud());

            String query = "ahtraspasos_eiyu.registraAhorroTraspasoEiYu";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                id = (Long) rows.getFirst().get("id");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(id));
            } else {
                log.error("No se logro registrar el traspaso de EiYu");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el traspaso de EiYu");
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
    public RespuestaDTO actualizaEstatusTraspasoEiYu(TraspasoEiYuOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        Long id;
        try {

            parameters.addValue("estatus", req.getEstatus());
            parameters.addValue("cverastreo", req.getCveRastreo());
            parameters.addValue("refnum", req.getReferenciaNumerica());
            parameters.addValue("ctaorigen", req.getCuentaOrigen());

            String query = "ahtraspasos_eiyu.actualizaEstatusTraspasoEiYu";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                id = (Long) rows.getFirst().get("id");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(id));
            } else {
                log.error("No se logro actualizar el traspaso de EiYu");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el traspaso de EiYu");
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
    public RespuestaDTO registraAhorroTraspasoEiYuHistorico(Long id, String estatus, String observaciones) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        try {

            parameters.addValue("idtraspasoeiyu", id);
            parameters.addValue("estatus", estatus);
            parameters.addValue("observaciones", observaciones);

            String query = "ahtraspasos_eiyu_historico.registraAhorroTraspasoEiYuHistorico";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el traspaso de EiYu en historico");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el traspaso de EiYu en historico");
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

    public RespuestaDTO actualizarTraspasoEiYu(String estatus, String obs, Long idTraspaso) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer rows = 0;

        try {
            parameters.addValue("estatus", estatus);
            parameters.addValue("id", idTraspaso);
            parameters.addValue("obs", obs);

            String query = "ahtraspasos_eiyu.updateTraspasoEiYuById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
            } else {
                respuesta.setCodigo(1);
                respuesta.setMensaje("No se logró actualizar el traspaso");
            }
        } catch (Exception e) {
            respuesta.setCodigo(-1);
            respuesta.setMensaje("Error inesperado al querer actualizar traspaso EiYu");
            log.error("Error inesperado al querer actualizar traspaso EiYu");
            errorHandler.handleException(e);
        }
        return respuesta;
    }

    public RespuestaDTO registrarHistoricoTraspasoEiYu(String estatus, String obs, Long idTraspasoEiYu) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer rows = 0;

        try {
            parameters.addValue("estatus", estatus);
            parameters.addValue("obs", obs);
            parameters.addValue("idtraspasoeiyu", idTraspasoEiYu);

            String query = "ahtraspasos_eiyu_historico.insertHistoricoTraspasoEiYu";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
            } else {
                respuesta.setCodigo(1);
                respuesta.setMensaje("No se logró registrar en historico traspaso EiYu");
            }
        } catch (Exception e) {
            respuesta.setCodigo(-1);
            respuesta.setMensaje("Error inesperado al querer registrar historico traspaso EiYu");
            log.error("Error inesperado al querer registrar historico traspaso EiYu");
            errorHandler.handleException(e);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Integer obtenerSecuenciaTraspasoCuentas() {
        try {
            String psql = queries.getProperty("secuencias.sigSecTraspasoCuentas");
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de traspaso cuentas:: " + e.getMessage()) {
            };
        }
    }

    public boolean esPagoCreditoCFReciente(String cuentaah, Double monto, String claveMovimiento, String segundos) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("monto", monto);
            parameters.addValue("claveMovimiento", claveMovimiento);
            parameters.addValue("segundos", segundos);

            String query = "ahtransacciones_cuentas.esPagoCreditoCFReciente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parámetros: {}", query, parameters.getValues());
    
            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);
            return !rows.isEmpty();
    
        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
    }
}
