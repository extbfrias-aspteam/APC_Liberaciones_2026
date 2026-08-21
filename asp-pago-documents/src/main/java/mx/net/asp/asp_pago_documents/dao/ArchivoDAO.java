package mx.net.asp.asp_pago_documents.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.model.BeneficiarioDetalleOBJ;
import mx.net.asp.asp_pago_documents.model.BeneficiarioOBJ;
import mx.net.asp.asp_pago_documents.model.CuentaAspPagoOBJ;
import mx.net.asp.asp_pago_documents.model.InfoCuentaCeroOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.ws.asp.request.ExpedienteReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class ArchivoDAO {
    private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public ArchivoDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "ahorro_expediente.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public boolean insertarInfoArchivo(ExpedienteReq obj) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows = 0;
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", obj.getCuenta());
            parameters.addValue("ruta_alfresco", obj.getRuta());
            parameters.addValue("observaciones", obj.getExpediente().getObservaciones());
            parameters.addValue("nombre", obj.getExpediente().getNombre() + "." + obj.getExpediente().getExtension());
            parameters.addValue("usuario_creacion", Integer.valueOf(obj.getUsuario_creacion()));
            parameters.addValue("documentos_ahorro_id", obj.getExpediente().getIdentificador_documento());

            String query = "ahorro_expediente.insertaAhorroExpediente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return rows > 0;
    }
}
