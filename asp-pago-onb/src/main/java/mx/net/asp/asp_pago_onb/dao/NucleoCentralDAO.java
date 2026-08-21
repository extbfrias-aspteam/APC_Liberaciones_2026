package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.DatosPosGlobalOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
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
public class NucleoCentralDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public NucleoCentralDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "nucleocentral.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registrarCuentaPosicionGlobal(DatosPosGlobalOBJ datosPosGlobalOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters;
        int rows;
        try {
            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("estatusah", datosPosGlobalOBJ.getEstatus());
            parameters.addValue("tipocuentaah", datosPosGlobalOBJ.getTipoCuenta());
            parameters.addValue("cuentaah", datosPosGlobalOBJ.getCuenta());
            parameters.addValue("referenciaah", datosPosGlobalOBJ.getReferencia());
            parameters.addValue("saldodisponibleah", datosPosGlobalOBJ.getSaldoDisponible());
            parameters.addValue("saldototalah", datosPosGlobalOBJ.getSaldoTotal());
            parameters.addValue("idpersona", datosPosGlobalOBJ.getIdPersona());
            parameters.addValue("cuenta_clabe", datosPosGlobalOBJ.getCuentaClabe());
            
            String query = "ncposicionglobalah.registrarCuentaPosicionGlobal";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
            
            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Cuenta {} del solicitante {} registrada correctamente en la posicion global", datosPosGlobalOBJ.getCuenta(), datosPosGlobalOBJ.getIdPersona());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar la cuenta {} del solicitante {} en la posicion global", datosPosGlobalOBJ.getCuenta(), datosPosGlobalOBJ.getIdPersona());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la cuenta en la posicion global");
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

    public RespuestaDTO obtenerCuentaOnboarding(String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("personaId", personaId);
            
            String query = "nc_solicitantes_cuenta_onboarding.obtenerCuentaOnboardingByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("tipo_cuenta"));
            } else {
                log.warn("No se encontro cuenta onboarding del solicitante {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro cuenta onboarding");
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

    public boolean validaPosicionGlobal(String cuentaah) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("cuentaah", cuentaah);

            String query = "ncposicionglobalah.validaCuentaExistePosicionGlobal";
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
