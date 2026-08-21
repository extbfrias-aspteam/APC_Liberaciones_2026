package mx.net.asp.asp_pago_documents.dao;

import java.util.List;
import java.util.Properties;

import mx.net.asp.asp_pago_documents.config.ApiException;
import mx.net.asp.asp_pago_documents.model.DocumentacionAspPagoOBJ;
import mx.net.asp.asp_pago_documents.utilerias.TraceIdUtil;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresStorage;
import org.apache.batik.anim.timing.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.model.TraceIdSospechosoOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    @Transactional(transactionManager = "transactionManagerCero")
    public Long guardarRegistroDocAspPago(DocumentacionAspPagoOBJ documentacionAspPagoOBJ) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("categoria", documentacionAspPagoOBJ.getCategoria());
            parameters.addValue("identificador", documentacionAspPagoOBJ.getIdentificador());
            parameters.addValue("nombre_archivo", documentacionAspPagoOBJ.getNombreArchivo());
            parameters.addValue("ruta_archivo", documentacionAspPagoOBJ.getRutaArchivo());
            parameters.addValue("storage_tipo", documentacionAspPagoOBJ.getStorageTipo());
            parameters.addValue("origen", documentacionAspPagoOBJ.getOrigen());
            parameters.addValue("estado", documentacionAspPagoOBJ.getEstado());
            parameters.addValue("uuid_peticion", TraceIdUtil.getTraceId());

            String query = "documentacion_asppago.nuevoRegistroDocumentacionAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedCeroJdbc.queryForObject(psql, parameters, Long.class);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            throw new ApiException(
                    ErroresStorage.ERROR_GUARDADO_ARCHIVO_BD,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_GUARDADO_ARCHIVO_BD)
            );
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public int actualizarRegistroDocAspPago(Long id, String ruta_archivo, String estado, String storage_tipo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("id", id);
            parameters.addValue("ruta_archivo", ruta_archivo);
            parameters.addValue("estado", estado);
            parameters.addValue("storage_tipo", storage_tipo);

            String query = "documentacion_asppago.actualizaRegistroDocumentacionAspPago";
            String psql = queries.getProperty(query);

            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            return namedCeroJdbc.update(psql, parameters);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return 0;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void registraLogDocumentacionAspPago(Long biometria_id, String ruta_archivo, String evento, String observaciones) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("biometria_id", biometria_id);
            parameters.addValue("ruta_archivo", ruta_archivo);
            parameters.addValue("evento", evento);
            parameters.addValue("observaciones", observaciones);

            String query = "documentacion_asppago.registraLogDocumentacionAspPago";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            namedCeroJdbc.update(psql, parameters);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public int actualizarRegistroDocPendienteAspPago(String idpersona) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            parameters.addValue("idpersona", idpersona);
            parameters.addValue("uuid_peticion", TraceIdUtil.getTraceId());

            String query = "documentacion_asppago.actualizaDocPendienteAspPago";
            String psql = queries.getProperty(query);

            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            int registrosActualizados = namedCeroJdbc.update(psql, parameters);

            return registrosActualizados;
        } catch (ApiException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            throw new ApiException(
                    ErroresStorage.ERROR_ACTUALIZAR_DOCUMENTACION_PENDIENTE,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_ACTUALIZAR_DOCUMENTACION_PENDIENTE)
            );
        }
    }

    public boolean existeDocPendienteAspPago(String idpersona) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("idpersona", idpersona);

            String query = "documentacion_asppago.consultaDocPendienteAspPago";
            String psql = queries.getProperty(query);

            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            List<Integer> result = namedCeroJdbc.queryForList(psql, parameters, Integer.class);

            return !result.isEmpty();

        } catch (Exception e) {
            errorHandler.handleException(e);
            throw new ApiException(
                    ErroresStorage.ERROR_CONSULTAR_DOCUMENTACION_PENDIENTE,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_CONSULTAR_DOCUMENTACION_PENDIENTE)
            );
        }
    }
}