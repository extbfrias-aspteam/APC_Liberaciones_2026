package mx.net.asp.asp_pago_management.dao;

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
import mx.net.asp.asp_pago_management.model.AhorroSaldosOBJ;

@Log4j2
@Repository
public class AhorroSaldosDAO {

    private final Properties queriesProcrea;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    public AhorroSaldosDAO(
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesProcrea = QueryLoader.loadYaml("procrea", "procrea.yml");
        this.errorHandler = errorHandler;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }

    public AhorroSaldosOBJ buscarByCuenta(String cuenta) {
        AhorroSaldosOBJ obj = new AhorroSaldosOBJ();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuenta", cuenta);

            String query = "ahorro_saldos.buscarSaldoByCuenta";
            String psql = queriesProcrea.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                obj.setCuenta((String) rows.getFirst().get("cuenta"));
                obj.setSolicitanteId((String) rows.getFirst().get("solicitante_id"));
                obj.setSaldoReal((Double) rows.getFirst().get("saldo_real"));
                obj.setSaldoPromedio((Double) rows.getFirst().get("saldo_promedio"));
                obj.setSaldoAcumulado((Double) rows.getFirst().get("saldo_acumulado"));
                obj.setFechaCorte((Date) rows.getFirst().get("fecha_corte"));
                obj.setFechaDeposito((Date) rows.getFirst().get("fecha_deposito"));
                obj.setIntereses((Double) rows.getFirst().get("intereses"));
                obj.setIva((Double) rows.getFirst().get("iva"));
                obj.setIsr((Double) rows.getFirst().get("isr"));
                obj.setRetenciones((Double) rows.getFirst().get("retenciones"));
                obj.setDesviacion((Double) rows.getFirst().get("desviacion"));
                obj.setDias((Integer) rows.getFirst().get("dias"));
                obj.setSaldoDisponible((Double) rows.getFirst().get("saldo_disponible"));
                obj.setAhorroSaldosId((Integer) rows.getFirst().get("ahorro_saldos_id"));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
        }

        return obj;
    }


}
