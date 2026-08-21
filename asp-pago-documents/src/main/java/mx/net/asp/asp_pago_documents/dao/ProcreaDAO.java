package mx.net.asp.asp_pago_documents.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AhorroContratoOBJ;
import mx.net.asp.asp_pago_documents.model.InfoCuentaProcreaOBJ;
import mx.net.asp.asp_pago_documents.model.ReportesParamOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        this.queriesAhorroContrato = QueryLoader.loadYaml("procrea", "ahorro_contrato.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO obtenerRecaByProductoId(Integer productoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("productoId", productoId);

            String query = "reca_ahorro.obtenerRecaByProductoId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("reca"));
            } else {
                log.error("No se encontro reca para el producto {}", productoId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró reca para el producto proporcionado:: " + productoId);
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

    public RespuestaDTO obtenerParentescoById(Integer Id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id", Id);

            String query = "cat_relacion.obtenerParentescoById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("parentesco"));
            } else {
                log.error("No se encontro parentesco para el id {}", Id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró parentesco para el id:: " + Id);
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

    public InfoCuentaProcreaOBJ obtenerDatosCuentaReporteEdoMovPROCREA(String cuenta) {
        InfoCuentaProcreaOBJ infoCuentaProcreaOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_contrato.obtenerDatosCuentaReporteEdoMovPROCREA";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                infoCuentaProcreaOBJ = new InfoCuentaProcreaOBJ();
                infoCuentaProcreaOBJ.setCuentaClabe((String) row.get("cuenta_clabe"));
                infoCuentaProcreaOBJ.setDescripcion((String) row.get("descripcion"));
            } else {
                log.error("No se encontraron datos del reporte del estado de movimiento para la cuenta {}", cuenta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            infoCuentaProcreaOBJ = null;
        }
        return infoCuentaProcreaOBJ;
    }

    public RespuestaDTO obtenerConceptoColonia(String numero) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id_solicitante", numero);

            String query = "conceptos_solicitantes.obtenerConceptoColonia";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {

                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("valor"));
            } else {
                log.error("No encontro concepto colonia de solicitante {}", numero);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No encontro concepto colonia de solicitante " + numero);
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

    public AhorroContratoOBJ obtenerDatosAhorroContratoByCuenta(String cuenta) {
        AhorroContratoOBJ ahorroContratoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_contrato.obtenerAhorroContratoByCuenta";
            String psql = queriesAhorroContrato.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                ahorroContratoOBJ = new AhorroContratoOBJ();
                ahorroContratoOBJ.setCuenta((String) row.get("cuenta"));
                ahorroContratoOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                ahorroContratoOBJ.setCorreo((String) row.get("correo"));
                ahorroContratoOBJ.setNombre_solicitante((String) row.get("nombre"));
                ahorroContratoOBJ.setTelefono_codi((String) row.get("telefono_codi"));
            } else {
                log.error("No se encontro el ahorro contrato de la cuenta {}", cuenta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            ahorroContratoOBJ = null;
        }
        return ahorroContratoOBJ;
    }

    public ReportesParamOBJ obtenerDatosReportesParamByLlave(String llave) {
        ReportesParamOBJ reportesParamOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("llave", llave);

            String query = "reportes_param.obtenerDatosReportesParamByLLave";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                reportesParamOBJ = new ReportesParamOBJ();
                reportesParamOBJ.setLlave((String) row.get("llave"));
                reportesParamOBJ.setParametro1((String) row.get("parametro1"));
                reportesParamOBJ.setParametro2((String) row.get("parametro2"));
                reportesParamOBJ.setParametro3((String) row.get("parametro3"));
                reportesParamOBJ.setParametro4((String) row.get("parametro4"));
                reportesParamOBJ.setParametro5((String) row.get("parametro5"));
                reportesParamOBJ.setParametro6((String) row.get("parametro6"));
                reportesParamOBJ.setParametro7((String) row.get("parametro7"));
                reportesParamOBJ.setParametro8((String) row.get("parametro8"));
                reportesParamOBJ.setParametro9((String) row.get("parametro9"));
                reportesParamOBJ.setParametro10((String) row.get("parametro10"));
                reportesParamOBJ.setParametro11((String) row.get("parametro11"));
                reportesParamOBJ.setParametro12((String) row.get("parametro12"));
                reportesParamOBJ.setParametro13((String) row.get("parametro13"));
                reportesParamOBJ.setParametro14((String) row.get("parametro14"));
                reportesParamOBJ.setParametro52((String) row.get("parametro52"));
                reportesParamOBJ.setParametro53((String) row.get("parametro53"));
            } else {
                log.error("No se encontraron datos en reportes_param para la llave {}", llave);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            reportesParamOBJ = null;
        }
        return reportesParamOBJ;
    }

}
