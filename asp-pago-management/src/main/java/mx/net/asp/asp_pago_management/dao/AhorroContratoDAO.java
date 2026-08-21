package mx.net.asp.asp_pago_management.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.model.AhorroContrato;

@Log4j2
@Repository
public class AhorroContratoDAO {
    private final Properties queriesProcrea;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    public AhorroContratoDAO(
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public AhorroContrato buscarByCuenta(String cuenta) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        AhorroContrato obj = new AhorroContrato();
        List<Map<String, Object>> rows;

        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_contrato.buscarCuentaByCuenta";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                obj.setSolicitanteNombre((String) rows.getFirst().get("nombre"));
                obj.setSolicitante((String) rows.getFirst().get("SOLICITANTE_ID"));
                obj.setCuenta((String) rows.getFirst().get("CUENTA"));
                obj.setPin((String) rows.getFirst().get("PIN"));
                obj.setReferencia((String) rows.getFirst().get("REFERENCIA"));
                obj.setContrato((String) rows.getFirst().get("CONTRATO"));
                obj.setTipoAhorroId((Integer) rows.getFirst().get("TIPO_AHORRO_ID"));
                obj.setRendimientoId((Integer) rows.getFirst().get("RENDIMIENTO_ID"));
                obj.setFechaApertura((Date) rows.getFirst().get("FECHA_APERTURA"));
                obj.setFechaDeposito((Date) rows.getFirst().get("FECHA_DEPOSITO"));
                obj.setTitularId((String) rows.getFirst().get("TITULAR_ID"));
                obj.setSucursalApertura((String) rows.getFirst().get("SUCURSAL_APERTURA"));

                obj.setAhorroContratoId((Integer) rows.getFirst().get("AHORRO_CONTRATO_ID,"));
                obj.setSaldo((Double) rows.getFirst().get("SALDO"));

                obj.setActividadId((Integer) rows.getFirst().get("ACTIVIDAD_ID"));
                obj.setSucursalId((Integer) rows.getFirst().get("SUCURSAL_ID"));
                obj.setCuentaClabe((String) rows.getFirst().get("CUENTA_CLABE"));
                obj.setTelefonoCoDi((String) rows.getFirst().get("TELEFONO_CODI"));
                obj.setFechaNacimiento((String) rows.getFirst().get("FECHA_NACIMIENTO"));

                obj.setCorreoEdocuenta((String) rows.getFirst().get("CORREO_EDOCUENTA"));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        return obj;
    }
}
