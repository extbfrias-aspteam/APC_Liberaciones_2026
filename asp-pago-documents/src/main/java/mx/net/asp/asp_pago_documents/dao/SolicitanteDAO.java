package mx.net.asp.asp_pago_documents.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.SolicitanteOBJ;
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
public class SolicitanteDAO {
    private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SolicitanteDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "solicitante.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public SolicitanteOBJ obtenerInfoGeneralSolicitanteById(String idSolicitante) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("numero", idSolicitante);

            String query = "solicitante.obtenerDatosGeneralesById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setNombre_p((String) row.get("nombre_p"));
                solicitanteOBJ.setApellido_p((String) row.get("apellido_p"));
                solicitanteOBJ.setApellido_m((String) row.get("apellido_M"));
                solicitanteOBJ.setFecha_nacimiento((String) row.get("fecha_nacimiento"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setColonia((Integer) row.get("colonia"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));
                solicitanteOBJ.setCatDomicilio1((Integer) row.get("cat_domicilio1"));
                solicitanteOBJ.setCatDomicilio2((Integer) row.get("cat_domicilio2"));
                solicitanteOBJ.setCatDomicilio3((Integer) row.get("cat_domicilio3"));
                solicitanteOBJ.setCatDomicilio4((Integer) row.get("cat_domicilio4"));
                solicitanteOBJ.setCatDomicilio5((Integer) row.get("cat_domicilio5"));
                solicitanteOBJ.setDescripcionDomicilio1((String) row.get("descripcion_domicilio1"));
                solicitanteOBJ.setDescripcionDomicilio2((String) row.get("descripcion_domicilio2"));
                solicitanteOBJ.setDescripcionDomicilio3((String) row.get("descripcion_domicilio3"));
                solicitanteOBJ.setDescripcionDomicilio4((String) row.get("descripcion_domicilio4"));
                solicitanteOBJ.setDescripcionDomicilio5((String) row.get("descripcion_domicilio5"));
                solicitanteOBJ.setDomicilioObservaciones((String) row.get("domicilo_observaciones"));
                solicitanteOBJ.setDomicilio((String) row.get("domicilio"));
                solicitanteOBJ.setRfc1((String) row.get("rfc1"));
                solicitanteOBJ.setRfc2((String) row.get("rfc2"));
                solicitanteOBJ.setRfc3((String) row.get("rfc3"));
                solicitanteOBJ.setSexo((String) row.get("sexo"));
                solicitanteOBJ.setTPersona((String) row.get("t_persona"));
                solicitanteOBJ.setNumeroCasa((String) row.get("numero_casa"));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public RespuestaDTO obtenerConceptoSolicitanteByClave(String claveConcepto, String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("claveConcepto", claveConcepto);
            parameters.addValue("personaId", personaId);

            String query = "conceptos_solicitantes.obtenerConceptoSolicitanteByClave";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("valor"));
            } else {
                log.error("No se encontro concepto {} del solicitante {}", claveConcepto, personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró concepto " + claveConcepto + " del solicitante " + personaId);
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
}
