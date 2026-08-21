package mx.net.asp.asp_pago_services_v2.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.NivelCuentaOBJ;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public RespuestaDTO obtenerNivelCuentaAh(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queries.getProperty("ahcuentas.obtenerNivelCuentaAh");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();
                NivelCuentaOBJ nvlCuentaOBJ = new NivelCuentaOBJ();

                nvlCuentaOBJ.setProductoId((Integer) row.get("producto_ahorro_id"));
                nvlCuentaOBJ.setNivelCuenta((String) row.get("nivel_cuenta"));

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(nvlCuentaOBJ));
            } else {
                log.error("No se encontro nivel de la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró nivel de la cuenta " + cuentaah);
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
    public RespuestaDTO actualizarNivelCuentaAh(String cuentaah, String nivelCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer rows = 0;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        parameters.addValue("ctaNivelas", nivelCuenta);
        try {
            String psql = queries.getProperty("ahcuentas.actualizarNivelCuentaAh");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Nivel de cuenta {} actualizado con exito a nivel {}", cuentaah, 123);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el nivel de la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el nivel de la cuenta " + cuentaah);
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
    public RespuestaDTO registraBitacoraNcBitacora(String dato, String observaciones, String claveTipoBitacora) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer rows = 0;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("claveTipoBitacora", claveTipoBitacora);
        parameters.addValue("dato", dato);
        parameters.addValue("observaciones", observaciones);
        try {
            String psql = queries.getProperty("ahcuentas.guardarNcBitacora");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Registro en ncbitacora exitoso");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro realizar el registro en ncbitacora");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró realizar el registro en bitacora");
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

    public String obtenerPersonaIdByCuenta(String cuentaah) {
        String personaId = "";
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queries.getProperty("ahcuentas.obtenerPersonaIdByCuenta");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                Map<String, Object> row = rows.getFirst();

                // String personaIdString = (String) row.get("persona_id");
                // personaId = Integer.valueOf(personaIdString);
                personaId = (String) row.get("persona_id");

            } else {
                log.error("No se encontro persona id de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return personaId;
    }
}
