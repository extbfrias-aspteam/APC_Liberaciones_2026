package mx.net.asp.asp_pago_services_v2.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.CatalogoOBJ;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
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
public class PldDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public PldDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "pld.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerIngresosMensuales() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> ingresosList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_ingresos.obtnenerCatIngresos");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    ingresosList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(ingresosList));
            } else {
                log.error("No se encontraron registros en el catalogo de Ingresos");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Ingresos");
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

    public RespuestaDTO obtenerAhorrosMensuales() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> ahorrosList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_ahorros.obtnenerCatAhorros");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    ahorrosList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(ahorrosList));
            } else {
                log.error("No se encontraron registros en el catalogo de Ahorros");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Ahorros");
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

    public RespuestaDTO obtenerOcupaciones() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> ocupacionesList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_ocupacion.obtnenerCatOcupaciones");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    ocupacionesList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(ocupacionesList));
            } else {
                log.error("No se encontraron registros en el catalogo de Ocupaciones");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Ocupaciones");
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

    public RespuestaDTO obtenerFrecuencia() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> frecuenciaList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_frecuencia.obtnenerCatFrecuencia");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    frecuenciaList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(frecuenciaList));
            } else {
                log.error("No se encontraron registros en el catalogo de Frecuencia");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Frecuencia");
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

    public RespuestaDTO obtenerObjetivos() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> objetivosList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_objetivo.obtnenerCatObjetivos");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    objetivosList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(objetivosList));
            } else {
                log.error("No se encontraron registros en el catalogo de Objetivos");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Objetivos");
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

    public RespuestaDTO obtenerActividades() {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> actividadesList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            String psql = queries.getProperty("pldcat_actividad.obtnenerCatActividades");
            log.info("Ejecutando consulta: {}", psql);
            rows = ceroJdbc.queryForList(psql);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    actividadesList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(actividadesList));
            } else {
                log.error("No se encontraron registros en el catalogo de Actividades");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Actividades");
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

    public RespuestaDTO obtenerGirosByActividadId(Integer actividadId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> actividadesList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("actividadId", actividadId);
        try {
            String psql = queries.getProperty("pldcat_giro.obtnenerCatGirosByActividadId");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    CatalogoOBJ r = new CatalogoOBJ();
                    r.setId((Long) row.get("id"));
                    r.setValor((String) row.get("valor"));
                    actividadesList.add(r);
                }
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(actividadesList));
            } else {
                log.error("No se encontraron registros en el catalogo de Giros con actividadId: {}", actividadId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros en el catálogo de Giros con actividadId: " + actividadId);
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

    public RespuestaDTO obtenerFormularioPld(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        List<CatalogoOBJ> actividadesList = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", cuentaah);
        try {
            String psql = queries.getProperty("pldformulario.obtenerPLDFormularioByCuentaAh");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.queryForList(psql, parameters);
            log.info("Registros encontrados de formulario PLD:: {}", rows.size());
            if (rows.size() > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se encontro formulario PLD de la cuenta: {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró formulario PLD de la cuenta: " + cuentaah);
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
    public RespuestaDTO guardarFormularioPLD(SubirNivelCuentaRemotaReq req) {
        Integer rows = 0;
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        parameters.addValue("cuentaah", req.getCuentaah());
        parameters.addValue("ingresos_id", req.getIngresoId());
        parameters.addValue("cat_ahorros_id", req.getAhorroId());
        parameters.addValue("frecuencia_id", req.getFrecuenciaId());
        parameters.addValue("actividad_id", req.getActividadId());
        parameters.addValue("giro_id", req.getGiroId());
        parameters.addValue("ocupacion_id", req.getOcupacionId());
        parameters.addValue("objetivo_id", req.getObjetivoId());
        parameters.addValue("terminos", req.getAcepta_term());
        parameters.addValue("latitud", req.getLatitud());
        parameters.addValue("longitud", req.getLongitud());
        try {
            String psql = queries.getProperty("pldformulario.guardarPldFormulario");
            log.info("Ejecutando consulta: {}, parametros: {}", psql, parameters);
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Formulario PLD guardado con exito");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el formulario PLD");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar el formulario PLD");
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
}
