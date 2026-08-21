package mx.net.asp.asp_pago_documents.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
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
public class PublicProcreaDAO {
    private final Properties queries;
    @Autowired
    @Qualifier("procreaJdbcTemplate")
    private JdbcTemplate procreaJdbc;
    @Autowired
    @Qualifier("namedProcreaJdbcTemplate")
    private NamedParameterJdbcTemplate namedProcreaJdbc;
    @Autowired
    private ErrorHandler errorHandler;

    @Autowired
    public PublicProcreaDAO() {
        // Carga las consultas desde el archivo YAML
        queries = QueryLoader.loadYaml("procrea", "public.yml");
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO insertarInfoArch(String cuenta, String ruta, String nombre, Integer id, Integer usuario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer rows = 0;
        try {
            parameters.addValue("cuenta", cuenta);
            parameters.addValue("ruta", ruta);
            parameters.addValue("observaciones", "");
            parameters.addValue("nombre", nombre);
            parameters.addValue("usuario", usuario);
            parameters.addValue("id", id);

            String query = "ahorro_expediente.insertarInfoArchivoAhorro";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar la informacion del INE para la cuenta {}", cuenta);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la información del INE para la cuenta " + cuenta);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }


}
