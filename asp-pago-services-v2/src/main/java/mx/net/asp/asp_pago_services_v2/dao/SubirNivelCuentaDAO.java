package mx.net.asp.asp_pago_services_v2.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.QueryLoader;
import mx.net.asp.asp_pago_services_v2.config.ServicioException;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;

@Log4j2
@Repository
public class SubirNivelCuentaDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SubirNivelCuentaDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "ahorro.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarNivelCuenta(String cuentaah, String nivelCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer rowsCuenta = 0;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {

            // Actualizar nivel de cuenta
            parameters = new MapSqlParameterSource();
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("ctaNivel", nivelCuenta);

            String psqlNivelCuenta = queries.getProperty("ahcuentas.actualizarNivelCuentaAh");
            log.info("Ejecutando consulta para actualizar nivel de cuenta: {}, parametros: {}", psqlNivelCuenta, parameters);
            rowsCuenta = namedCeroJdbc.update(psqlNivelCuenta, parameters);

            if (rowsCuenta <= 0) {
                log.error("No se logró actualizar el nivel de la cuenta {}", cuentaah);
                // Lanza una excepción personalizada con el código y mensaje
                throw new ServicioException(-106, "No se logró actualizar el nivel de la cuenta " + cuentaah);
            }

            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(null);

        } catch (ServicioException e) {
            log.error("ServicioException Error: ", e);
            // Devolvemos el mensaje y código de la excepción personalizada
            respuesta.setCodigo(e.getCodigo());
            respuesta.setMensaje(e.getMensaje());
            respuesta.setData(null);
        } catch (Exception e) {
            errorHandler.handleException(e);
            if (rowsCuenta <= 0)
                throw new ServicioException(-106, "No se logró actualizar el nivel de la cuenta " + cuentaah);
            else
                throw new ServicioException(-200, "Error interno");
        }
        return respuesta;
    }
}
