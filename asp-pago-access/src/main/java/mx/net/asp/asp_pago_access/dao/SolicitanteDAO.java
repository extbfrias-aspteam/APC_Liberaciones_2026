package mx.net.asp.asp_pago_access.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.config.QueryLoader;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public SolicitanteOBJ obtenerInfoBasicaSolicitanteById(String idSolicitante) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("numero", idSolicitante);

            String query = "solicitante.obtenerDatosBasicosById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public SolicitanteOBJ obtenerInfoBasicaSolicitanteByTelefonoCoDi(String telefonoCoDi) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("telefonoCoDi", telefonoCoDi);

            String query = "solicitante.obtenerDatosBasicosByTelefonoCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));
            } else {
                log.error("No se encontro informacion del solicitante con telefono {}", telefonoCoDi);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarBitacoraCambio(String control, String tipo, Integer usuario_aut, String actual, String nuevo, String obs) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("control", control);
            parameters.addValue("tipo", tipo);
            parameters.addValue("usuario_aut", usuario_aut);
            parameters.addValue("actual", actual);
            parameters.addValue("nuevo", nuevo);
            parameters.addValue("obs", obs);

            String query = "solicitante.registrarBitacoraCambio";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el cambio de datos del solicitante con id {}", control);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el cambio de datos del solicitante con id " + control);
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
}
