package mx.net.asp.asp_pago_management.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.model.AhorroRendimientoVigentesOBJ;

@Log4j2
@Repository
public class AhorroRendimientoVigenteDAO {

    private final Properties queriesProcrea;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    public AhorroRendimientoVigenteDAO(
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.errorHandler = errorHandler;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }

    public AhorroRendimientoVigentesOBJ buscarByCuenta(String cuenta) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        AhorroRendimientoVigentesOBJ obj = new AhorroRendimientoVigentesOBJ();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_rendimiento_vigente.buscarRendimientoByCuenta";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                obj.setRendimientoVigenteId((Integer) rows.getFirst().get("rendimiento_vigente_id"));
                obj.setCuenta((String) rows.getFirst().get("cuenta"));
                obj.setTipoAhorroId((Integer) rows.getFirst().get("tipo_ahorro_id"));
                obj.setPeriodicidad((Integer) rows.getFirst().get("periodicidad"));
                obj.setPlazo((Integer) rows.getFirst().get("plazo"));
                obj.setTipoTasa((String) rows.getFirst().get("tipo_tasa"));
                obj.setCalculoIva((String) rows.getFirst().get("calculo_iva"));
                obj.setTasaInt((Double) rows.getFirst().get("tasa_int"));
                obj.setTasaId((Integer) rows.getFirst().get("tasa_id"));
                obj.setBase((Integer) rows.getFirst().get("base"));
                obj.setFormula((String) rows.getFirst().get("formula"));
                obj.setTipoCorte((String) rows.getFirst().get("tipo_corte"));
                obj.setDeposito((String) rows.getFirst().get("deposito"));
                obj.setNumDias((Integer) rows.getFirst().get("num_dias"));
                obj.setDiasGracia((Integer) rows.getFirst().get("dias_gracia"));
                obj.setCtaContable((String) rows.getFirst().get("cta_contable"));
                obj.setVigenciaDesde((Date) rows.getFirst().get("vigencia_desde"));
                obj.setVigenciaHasta((Date) rows.getFirst().get("vigencia_hasta"));
                obj.setCondicionesRetiros((String) rows.getFirst().get("condiciones_retiros"));
                obj.setCondicionesApertura((String) rows.getFirst().get("condiciones_apertura"));
                obj.setCreadoPor((Integer) rows.getFirst().get("creado_por"));
                obj.setFechaCreacion((Timestamp) rows.getFirst().get("fecha_creacion"));
                obj.setModificadoPor((Integer) rows.getFirst().get("modificado_por"));
                obj.setFechaModificacion((Timestamp) rows.getFirst().get("fecha_modificacion"));
                obj.setRendimientoId((Integer) rows.getFirst().get("rendimiento_id"));
                obj.setGat((Double) rows.getFirst().get("gat"));
                obj.setTipoCapitalizarId((Integer) rows.getFirst().get("tipo_capitalizar_id"));
                obj.setNoDisposicion((Integer) rows.getFirst().get("no_disposicion"));
                obj.setEstatus((String) rows.getFirst().get("estatus"));
                obj.setTipo((Integer) rows.getFirst().get("tipo"));
                obj.setCapital((Double) rows.getFirst().get("capital"));
                obj.setFechaInicio((Date) rows.getFirst().get("fecha_inicio"));
                obj.setFechaFinal((Date) rows.getFirst().get("fecha_final"));
                obj.setInteres((Double) rows.getFirst().get("interes"));
                obj.setFechaDeposito((Date) rows.getFirst().get("fecha_deposito"));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        return obj;
    }

}
