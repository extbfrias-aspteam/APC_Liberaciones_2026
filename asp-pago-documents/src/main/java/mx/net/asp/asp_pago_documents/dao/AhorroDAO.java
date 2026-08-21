package mx.net.asp.asp_pago_documents.dao;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.QueryLoader;
import mx.net.asp.asp_pago_documents.model.BeneficiarioDetalleOBJ;
import mx.net.asp.asp_pago_documents.model.BeneficiarioOBJ;
import mx.net.asp.asp_pago_documents.model.CuentaAspPagoOBJ;
import mx.net.asp.asp_pago_documents.model.InfoCuentaCeroOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
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
public class AhorroDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public AhorroDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public BeneficiarioOBJ obtenerBeneficiarioCuentaAhByCuentaAh(String cuentaah) {
        BeneficiarioOBJ beneficiarioOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_beneficiarios.obtenerBeneficiarioCuentaAhByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                beneficiarioOBJ = new BeneficiarioOBJ();

                beneficiarioOBJ.setCuentaId((Integer) row.get("cuenta_id"));
                beneficiarioOBJ.setCuentaah((String) row.get("cuenta"));
                beneficiarioOBJ.setNombreBeneficiario((String) row.get("nombre_beneficiario"));
                beneficiarioOBJ.setRelacionId((Integer) row.get("relacion_id"));
                beneficiarioOBJ.setDetalleEstatus((Boolean) row.get("detalle_estatus"));
                beneficiarioOBJ.setPorcentaje((Integer) row.get("porcentaje"));
            } else {
                log.error("No se encontro beneficiario para la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            beneficiarioOBJ = null;
        }
        return beneficiarioOBJ;
    }


    public CuentaAspPagoOBJ obtenerDatosGeneralesCuentaAhByCuentaAh(String cuentaah) {
        CuentaAspPagoOBJ cuentaAspPagoObj = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas.obtenerDatosGeneralesCuentaAhByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                cuentaAspPagoObj = new CuentaAspPagoOBJ();
                cuentaAspPagoObj.setCuentaId((Integer) row.get("cuentaId"));
                cuentaAspPagoObj.setCuenta((String) row.get("cuenta"));
                cuentaAspPagoObj.setProducto((String) row.get("producto"));
                cuentaAspPagoObj.setProductoId((Integer) row.get("productoId"));
                cuentaAspPagoObj.setDescripcion((String) row.get("descripcion"));
                cuentaAspPagoObj.setClabe((String) row.get("clabe"));
                cuentaAspPagoObj.setEstatus((String) row.get("estatusah"));
                cuentaAspPagoObj.setPersonaId((String) row.get("persona_id"));
            } else {
                log.error("No se encontraron los datos generales de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            cuentaAspPagoObj = null;
        }
        return cuentaAspPagoObj;
    }

    public InfoCuentaCeroOBJ obtenerDatosCuentaReporteEdoMovCERO(String cuenta) {
        InfoCuentaCeroOBJ infoCuentaCeroOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahcuentas.obtenerDatosCuentaReporteEdoMovCERO";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                infoCuentaCeroOBJ = new InfoCuentaCeroOBJ();
                infoCuentaCeroOBJ.setCuentaClabe((String) row.get("clabe_interbancaria"));
                infoCuentaCeroOBJ.setDescripcion((String) row.get("descripcion"));
            } else {
                log.error("No se encontraron datos del reporte del estado de movimiento para la cuenta {}", cuenta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            infoCuentaCeroOBJ = null;
        }
        return infoCuentaCeroOBJ;
    }

    public BeneficiarioDetalleOBJ obtenerDetalleBeneficiarioByCuentaAh(String cuentaah) {
        BeneficiarioDetalleOBJ beneficiarioDetalleOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "ahcuentas_beneficiarios_detalle.obtenerDetalleBeneficiarioByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                beneficiarioDetalleOBJ = new BeneficiarioDetalleOBJ();

                beneficiarioDetalleOBJ.setId((Long) row.get("id"));
				beneficiarioDetalleOBJ.setIdBeneficiario((Long) row.get("idBeneficiario"));
				beneficiarioDetalleOBJ.setFechaNacimiento((String) row.get("fechaNacimiento"));
				beneficiarioDetalleOBJ.setDomicilio((String) row.get("domicilio"));
				beneficiarioDetalleOBJ.setCodigoPostal((String) row.get("codigoPostal"));
				beneficiarioDetalleOBJ.setColonia((String) row.get("colonia"));
				beneficiarioDetalleOBJ.setCiudad((String) row.get("ciudad"));
				beneficiarioDetalleOBJ.setNumeroInterior((String) row.get("numeroInterior"));
				beneficiarioDetalleOBJ.setCalle((String) row.get("calle"));
				beneficiarioDetalleOBJ.setNumeroExterior((String) row.get("numeroExterior"));
				beneficiarioDetalleOBJ.setNumeroCelular((String) row.get("numeroCelular"));
				beneficiarioDetalleOBJ.setRfc((String) row.get("rfc"));
				beneficiarioDetalleOBJ.setPorcentaje((Integer) row.get("porcentaje_b"));
				beneficiarioDetalleOBJ.setNombreBeneficiario((String) row.get("nombre_b"));
				beneficiarioDetalleOBJ.setRelacionId((Integer) row.get("parentesco_b"));
				beneficiarioDetalleOBJ.setDetalleEstatus((Boolean) row.get("detalle_estatus"));
            } else {
                log.error("No se encontro beneficiario para la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            beneficiarioDetalleOBJ = null;
        }
        return beneficiarioDetalleOBJ;
    }

}
