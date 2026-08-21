package mx.net.asp.asp_pago_services_v2.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.CatalogoOBJ;
import mx.net.asp.asp_pago_services_v2.model.SesionIncodeOBJ;
import mx.net.asp.asp_pago_services_v2.model.TraceIdSospechosoOBJ;
import mx.net.asp.asp_pago_services_v2.requests.ActualizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeManualReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.GuardarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class SeguridadDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SeguridadDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "seguridad.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerSesionIncode(String cuentaah, boolean activo, String sesionIncodeId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        parameters.addValue("activo", activo);
        try {
            String psql = queries.getProperty("seincode_proceso.obtenerSesionIncode");

            String whereClause = StringUtils.isBlank(sesionIncodeId) ? "" : "and irp.sesion_incode_id = :sesionId";
            psql = psql.replace("?where", whereClause);

            if (!StringUtils.isBlank(sesionIncodeId))
                parameters.addValue("sesionId", sesionIncodeId);

            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();
                SesionIncodeOBJ sesionOBJ = new SesionIncodeOBJ();

                sesionOBJ.setSesionIncodeId((String) row.get("sesion_incode_id"));
                sesionOBJ.setModuloDescripcion((String) row.get("descripcion"));
                sesionOBJ.setToken((String) row.get("token_incode"));
                sesionOBJ.setModuloId(((Long) row.get("incat_modulo_id")).intValue());

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(sesionOBJ));
            } else {
                if (!StringUtils.isBlank(sesionIncodeId)) {
                    log.error("No se encontro sesion de incode con id {} y con la cuenta {}", sesionIncodeId, cuentaah);
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("No se encontró sesión de incode con id " + sesionIncodeId + " y con la cuenta " + cuentaah);
                    respuesta.setData(null);
                } else {
                    log.error("No se encontro sesion de incode con la cuenta {}", cuentaah);
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("No se encontró sesión de incode con la cuenta " + cuentaah);
                    respuesta.setData(null);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO guardarSesionIncode(GuardarSesionIncodeReq req) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", req.getSesionIncodeId());
        parameters.addValue("token", req.getToken());
        parameters.addValue("moduloId", req.getModuloId());
        parameters.addValue("cuentaah", req.getCuentaah());
        try {
            String psql = queries.getProperty("seincode_proceso.guardarSesionIncode");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Sesion de Incode guardada con exito con id {}", req.getSesionIncodeId());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar la sesion de Incode");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar la sesión de Incode");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarSesionIncode(ActualizarSesionIncodeReq req) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", req.getSesionIncodeId());
        parameters.addValue("moduloId", req.getModuloId());
        parameters.addValue("cuentaah", req.getCuentaah());
        try {
            String psql = queries.getProperty("seincode_proceso.actualizarSesionIncode");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Sesion de Incode con id {} actualizada con exito", req.getSesionIncodeId());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar la sesion de Incode");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar la sesión de Incode");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO finalizarSesionIncode(FinalizarSesionIncodeReq req, Integer usuario) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", req.getSesionIncodeId());
        parameters.addValue("cuentaah", req.getCuentaah());
        parameters.addValue("estatus", req.getEstatus());
        parameters.addValue("usuario", usuario);
        try {
            String psql = queries.getProperty("seincode_proceso.finalizarSesionIncode");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Sesion de Incode con id {} finalizada con exito", req.getSesionIncodeId());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar la sesion de Incode");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró finalizar la sesión de Incode");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarEstatusSesionIncode(FinalizarSesionIncodeReq req, Integer usuario) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", req.getSesionIncodeId());
        parameters.addValue("cuentaah", req.getCuentaah());
        parameters.addValue("estatus", req.getEstatus());
        parameters.addValue("usuario", usuario);
        try {
            String psql = queries.getProperty("seincode_proceso.actualizarEstatusSesionIncode");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Estatus de sesion de Incode con id {} actualizado con exito", req.getSesionIncodeId());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el estatus de la sesion de Incode");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el estatus de la sesión de Incode");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public RespuestaDTO obtenerModuloIncodeById(Integer moduloId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> actividadesList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("moduloId", moduloId);
        try {
            String psql = queries.getProperty("seincode_cat_modulos.seincode_cat_modulos");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            log.info("Registros encontrados de cat incode modulos:: {}", rows.size());
            if (rows.size() > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se encontró el módulo proporcionado: {}", moduloId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró el módulo proporcionado");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO finalizarSesionIncodeManual(FinalizarSesionIncodeManualReq req, Integer usuario) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", req.getInterviewId());
        parameters.addValue("estatus", req.getOnboardingStatus());
        parameters.addValue("usuario", usuario);
        try {
            String psql = queries.getProperty("seincode_proceso.finalizarSesionIncodeManual");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Sesion manual de Incode con id {} finalizada con exito", req.getInterviewId());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar la sesion manual de Incode");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró finalizar la sesión manual de Incode");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return respuesta;
    }

    public String obtenerCuentaBySesionId(String sesionId) {
        String cuenta = "";
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("sesionId", sesionId);
        try {
            String psql = queries.getProperty("seincode_proceso.obtenerCuentaBySesionId");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();
                cuenta = (String) row.get("cuentaah");
            } 
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return cuenta;
    }

    public List<String> obtenerCorreos() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<String> cuentasList = new ArrayList<>();
        try {
            String psql = queries.getProperty("seincode_proceso.obtenerCorreos");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    cuentasList.add((String) row.get("correo"));
                }
            } else {
                log.warn("No se encontraron correos activos");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
        return cuentasList.isEmpty() ? Collections.emptyList() : cuentasList;
    }

    public void registraUUIDSospechoso(TraceIdSospechosoOBJ traceIdSospechosoOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("uuid_txt", traceIdSospechosoOBJ.getTraceId());
            parameters.addValue("uuid_signed", traceIdSospechosoOBJ.getTraceIdSigned());
            parameters.addValue("endpoint", traceIdSospechosoOBJ.getEndpoint());
            parameters.addValue("ip_cliente", traceIdSospechosoOBJ.getIpCliente());
            parameters.addValue("observaciones", traceIdSospechosoOBJ.getObservaciones());

            String query = "seuuid_sospechosos.registraUUIDSospechoso";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro registrar el UUID sospechoso");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }
}
