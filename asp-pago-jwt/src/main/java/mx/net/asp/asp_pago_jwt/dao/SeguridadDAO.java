package mx.net.asp.asp_pago_jwt.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.config.QueryLoader;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.model.*;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.TraceIdUtil;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

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

    @Transactional(transactionManager = "transactionManagerCero")
    public Long registraAccessToken(String token, Long refreshId, String telefono, String dispositivoId, String tenantId, Integer idCanal, Instant fechaExp) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("refresh_token_id", refreshId);
            parameters.addValue("token", token);
            parameters.addValue("telefono_claims", telefono);
            parameters.addValue("dispositivoid_claims", dispositivoId);
            parameters.addValue("fecha_expiracion", Timestamp.from(fechaExp));
            parameters.addValue("uuid", TraceIdUtil.getTraceId());
            parameters.addValue("tenantid_claims", tenantId);
            parameters.addValue("idcanal_claims", String.valueOf(idCanal));

            String query = "sejwt_acces_token.registrarAccessToken";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = namedCeroJdbc.update(psql, parameters, keyHolder, new String[]{"id"});
            if (rows > 0) {
                return keyHolder.getKey().longValue();
            } else {
                log.warn("No se logró registrar el JWT Access Token");
                return null;
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return null;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Long registraRefreshToken(String token, String telefono, String dispositivoId, String tenantId, Integer idCanal, Instant fechaExp) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("token", token);
            parameters.addValue("telefono_claims", telefono);
            parameters.addValue("dispositivoid_claims", dispositivoId);
            parameters.addValue("fecha_expiracion", Timestamp.from(fechaExp));
            parameters.addValue("uuid", TraceIdUtil.getTraceId());
            parameters.addValue("tenantid_claims", tenantId);
            parameters.addValue("idcanal_claims", String.valueOf(idCanal));

            String query = "sejwt_refresh_token.registrarRefreshToken";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = namedCeroJdbc.update(psql, parameters, keyHolder, new String[]{"id"});
            if (rows > 0) {
                return keyHolder.getKey().longValue();
            } else {
                log.warn("No se logró registrar el JWT Access Token");
                return null;
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return null;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void registraJwtTokenLog(Long tokenId, String tipoToken, String tipoOperacion, String servicio, String tokenUtilizado,
                                  String resultado, String mensaje, Integer codigo, HeaderWS headerWS, Integer idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("token_id", tokenId);
            parameters.addValue("tipo_token", tipoToken);
            parameters.addValue("tipo_operacion", tipoOperacion);
            parameters.addValue("servicio", servicio);
            parameters.addValue("resultado", resultado);
            parameters.addValue("mensaje", mensaje);
            parameters.addValue("codigo", codigo);
            parameters.addValue("id_canal", idCanal);
            parameters.addValue("longitud", headerWS != null ? headerWS.getLongitud() : null);
            parameters.addValue("latitud", headerWS != null ? headerWS.getLatitud() : null);
            parameters.addValue("ip_cliente",  headerWS != null ? headerWS.getIpHost() : null);
            parameters.addValue("token_utilizado",  tokenUtilizado);
            parameters.addValue("uuid", TraceIdUtil.getTraceId());

            String query = "sejwt_token_logs.registraJwtTokenLog";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro registrar el JWT LOG");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    public Optional<RefreshTokenOBJ> obtenerRefreshTokenVigentePorTelefonoDispositivo(String telefono, String dispositivoId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        RefreshTokenOBJ refreshTokenOBJ = null;
        try {
            parameters.addValue("telefono", telefono);
            parameters.addValue("dispositivo_id", dispositivoId);

            String query = "sejwt_refresh_token.obtenerRefreshTokenVigentePorTelefonoDispositivo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                refreshTokenOBJ = new RefreshTokenOBJ();

                refreshTokenOBJ.setId((Long) row.get("id"));
                refreshTokenOBJ.setToken((String) row.get("token"));

                return Optional.of(refreshTokenOBJ);
            } else {
                log.warn("No se encontro refresh token vigente");
                return Optional.empty();
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Optional.empty();
        }
    }

    public Optional<RefreshTokenOBJ> obtenerRefreshTokenVigentePorTenantIdCanalId(String tenandId, Integer idCanal) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        RefreshTokenOBJ refreshTokenOBJ = null;
        try {
            parameters.addValue("tenantid_claims", tenandId);
            parameters.addValue("idcanal_claims", String.valueOf(idCanal));

            String query = "sejwt_refresh_token.obtenerRefreshTokenVigentePorTenantIdCanalId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                refreshTokenOBJ = new RefreshTokenOBJ();

                refreshTokenOBJ.setId((Long) row.get("id"));
                refreshTokenOBJ.setToken((String) row.get("token"));

                return Optional.of(refreshTokenOBJ);
            } else {
                log.warn("No se encontro refresh token vigente");
                return Optional.empty();
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Optional.empty();
        }
    }

    public Optional<RefreshTokenOBJ> obtenerRefreshTokenVigentePorToken(String token) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        RefreshTokenOBJ refreshTokenOBJ = null;
        try {
            parameters.addValue("token", token);

            String query = "sejwt_refresh_token.obtenerRefreshTokenVigentePorToken";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                refreshTokenOBJ = new RefreshTokenOBJ();

                refreshTokenOBJ.setId((Long) row.get("id"));
                refreshTokenOBJ.setToken((String) row.get("token"));

                return Optional.of(refreshTokenOBJ);
            } else {
                log.error("No se encontro refresh token vigente");
                return Optional.empty();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return Optional.empty();
        }
    }

    public Optional<AccessTokenOBJ> obtenerAccessTokenVigentePorRefreshId(Long refreshId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        AccessTokenOBJ accessTokenOBJ = null;
        try {
            parameters.addValue("refresh_token_id", refreshId);

            String query = "sejwt_acces_token.obtenerAccessTokenVigentePorRefreshId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                accessTokenOBJ = new AccessTokenOBJ();

                accessTokenOBJ.setId((Long) row.get("id"));
                accessTokenOBJ.setRefreshTokenId((Long) row.get("refresh_token_id"));
                accessTokenOBJ.setToken((String) row.get("token"));

                return Optional.of(accessTokenOBJ);
            } else {
                log.error("No se encontro access token vigente");
                return Optional.empty();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return Optional.empty();
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void expirarRefreshToken(Long tokenId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("token_id", tokenId);

            String query = "sejwt_refresh_token.expirarRefreshToken";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro expirar el refreh Token con id {}", tokenId);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public void expirarAccessToken(Long tokenId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("token_id", tokenId);

            String query = "sejwt_acces_token.expirarAccessToken";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
            } else {
                log.info("No se logro expirar el access Token con id {}", tokenId);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    public boolean validaTelefonoEstaBloqueado(String telefono) {
        try {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("telefono", telefono);

            String query = "telefonos_bloqueados_jwt.obtenerTelfenoBloqueado";
            String psql = queries.getProperty(query);

            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);
            return !rows.isEmpty();
        } catch (Exception e) {
            errorHandler.handleException(e);
            return false;
        }
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

    public AccessTokenOBJ obtenerAccessTokenByTokenEstatus(String jwtToken, String estatus) {
        AccessTokenOBJ token = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            parameters.addValue("token", jwtToken);
            parameters.addValue("estatus", estatus);

            String query = "sejwt_acces_token.obtenerAccessTokenByTokenEstatus";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                token = new AccessTokenOBJ();
                token.setId((Long) row.get("id"));
                token.setTelefono((String) row.get("telefono"));
                token.setDispositivoId((String) row.get("dispositivoid"));
                token.setEstatus((String) row.get("estatus"));
                token.setToken((String) row.get("token"));
            } else {
                log.warn("No se encontró token con estatus {} en BD para {}", estatus, jwtToken);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            token = null;
        }

        return token;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public int revocaRefreshTokenPorTelefono(String telefono) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("telefono", telefono);

            String query = "sejwt_refresh_token.revocarRefreshTokenByTelefono";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            int rows = namedCeroJdbc.update(psql, parameters);

            if (rows > 0) {
                log.info("Se revocaron {} tokens de tipo REFRESH para el teléfono {}", rows, telefono);
            } else {
                log.warn("No se encontraron tokens de tipo REFRESH vigentes para el teléfono {}", telefono);
            }

            return rows;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return 0;
        }
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public int revocaAccessTokenPorTelefono(String telefono) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            parameters.addValue("telefono", telefono);

            String query = "sejwt_acces_token.revocarAccessTokenByTelefono";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            int rows = namedCeroJdbc.update(psql, parameters);

            if (rows > 0) {
                log.info("Se revocaron {} tokens de tipo ACCESS para el teléfono {}", rows, telefono);
            } else {
                log.warn("No se encontraron tokens de tipo ACCESS vigentes para el teléfono {}", telefono);
            }

            return rows;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            return 0;
        }
    }

    public TenantIdOBJ obtenerTenantIdByIdCanal(Integer idCanal) {
        TenantIdOBJ tenantIdOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            parameters.addValue("idCanal", idCanal);

            String query = "sejwt_tenant_id.obtenerTenantIdByIdCanal";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                tenantIdOBJ = new TenantIdOBJ();
                tenantIdOBJ.setIdCanal((Integer) row.get("id_canal"));
                tenantIdOBJ.setActivo((Boolean) row.get("activo"));
                tenantIdOBJ.setTenantId((String) row.get("tenant_id"));
            } else {
                log.warn("No se encontró tenantId para el canal {}", idCanal);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            tenantIdOBJ = null;
        }

        return tenantIdOBJ;
    }

    public TenantIdOBJ obtenerTenantIdByIdCanalAndByTenantIdClaims(Integer idCanalDesdeToken, String tenantIdDesdeToken) {
        TenantIdOBJ tenantIdOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            parameters.addValue("idCanal", idCanalDesdeToken);
            parameters.addValue("idTenant", tenantIdDesdeToken);

            String query = "sejwt_tenant_id.obtenerTenantIdByIdCanalAndByTenantIdClaims";
            String psql = queries.getProperty(query);

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                tenantIdOBJ = new TenantIdOBJ();
                tenantIdOBJ.setIdCanal((Integer) row.get("id_canal"));
                tenantIdOBJ.setActivo((Boolean) row.get("activo"));
                tenantIdOBJ.setTenantId((String) row.get("tenant_id"));
            } else {
                log.warn("No se encontró tenantId para el canal {}", idCanalDesdeToken);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            tenantIdOBJ = null;
        }

        return tenantIdOBJ;
    }
}
