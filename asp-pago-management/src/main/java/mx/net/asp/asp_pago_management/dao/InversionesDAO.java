package mx.net.asp.asp_pago_management.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;



@Log4j2
@Repository
public class InversionesDAO {

	private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

   

    @Autowired
    public InversionesDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "inversiones.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    

    

    public Boolean validaInversionesActivasyVigentes(String cuenta) {
    	
    	MapSqlParameterSource parameters = new MapSqlParameterSource();
        Boolean resultado = false;
        try {

            parameters.addValue("cuenta", cuenta);

            String query = "inversiones.validaInversionesActivasyVigentes";
            String sql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}", query, parameters.getValues());

            resultado = namedProcreaJdbc.queryForObject(sql, parameters, Boolean.class);
        } catch (Exception e) {
        	 errorHandler.handleException(e);
            throw new DataAccessException("No se pudo obtener la existencia de inversiones activas: " + e.getMessage()) {
            };
        }

        return resultado;

      
    }


   }
