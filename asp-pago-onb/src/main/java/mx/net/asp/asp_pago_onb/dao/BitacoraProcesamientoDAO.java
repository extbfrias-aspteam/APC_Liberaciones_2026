package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.model.BitacoraProcesamientoOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Properties;

@Log4j2
@Repository
public class BitacoraProcesamientoDAO {

    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public BitacoraProcesamientoDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Integer nuevoBitPro(BitacoraProcesamientoOBJ bitPro) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            Integer idReq = sigSecBitPro();
            parameters.addValue("id", idReq);
            parameters.addValue("cuenta", bitPro.getCuenta());
            parameters.addValue("operacion_id", bitPro.getOperacionId());
            parameters.addValue("fhInicio", bitPro.getFechaInicio());
            parameters.addValue("fhFin", bitPro.getFechaFin());
            parameters.addValue("idProcesamiento", bitPro.getIdProcesamiento());

            String query = "bitacora_procesamiento.nuevoBitPro";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            namedCeroJdbc.update(psql, parameters);
            return idReq;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return 0;
        }
    }

    public Integer sigSecBitPro() {
        try {
            String query = "bitacora_procesamiento.sigSecBitPro";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return 0;
        }
    }

    public Integer sigSecProId() {
        try {
            String query = "bitacora_procesamiento.sigSecProId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query);
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return 0;
        }
    }
}
