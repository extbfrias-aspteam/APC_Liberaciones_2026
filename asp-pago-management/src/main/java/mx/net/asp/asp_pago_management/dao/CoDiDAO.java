package mx.net.asp.asp_pago_management.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.FavoritoOBJ;
import mx.net.asp.asp_pago_management.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_management.request.RegistraFavoritoReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Log4j2
@Repository
public class CoDiDAO {
    private final Properties queries;
    private final ErrorHandler errorHandler;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final String numeroCuentaCodi = "numero_cuenta_codi";
    private final String idInstitucionBeneficiario = "id_institucion_beneficiario";
    private final String numeroCuentaBeneficiario = "numero_cuenta_beneficiario";
    private final String cuentaString = "cuenta";
    private final String nombreBeneficiario = "nombre_beneficiario";
    private final String alias = "alias";
    private final String idTipoCuentaBeneficiario = "id_tipo_cuenta_beneficiario";
    private final String ejecutadoParamsMessage = "Ejecutando consulta: {}, parametros: {}";
    private final String nombreInstitucion = "nombre_institucion";
    private final String personaId = "persona_id";

    public CoDiDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.errorHandler = errorHandler;
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
    }

    @Value("${trx.minutos.espera.favorito}")
    private Integer minutosEsperaFavorito;

    public RegistroCoDiOBJ obtenerRegistroCoDiByCuentaAh(String cuentaah) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "registro_codi.obtenerRegistroCoDiByCuentaAh";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get(cuentaString));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) rows.getFirst().get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) rows.getFirst().get("activo"));
                regCoDiOBJ.setTelefono_validado((String) rows.getFirst().get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) rows.getFirst().get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) rows.getFirst().get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) rows.getFirst().get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) rows.getFirst().get("intentos"));
            } else {
                log.error("No se encontro registro codi completo de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }

    public List<RegistroCoDiOBJ> obtenerListRegistroCoDiByPersonaId(String personaId) {
        List<RegistroCoDiOBJ> registros = new ArrayList<>();
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        try {
            // Se definen los parámetros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.obtenerListRegistroCoDiByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            List<Map<String, Object>> rows = namedCeroJdbc.queryForList(psql, parameters);

            for (Map<String, Object> row : rows) {
                RegistroCoDiOBJ regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) row.get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) row.get("activo"));
                regCoDiOBJ.setTelefono_validado((String) row.get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) row.get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) row.get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) row.get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) row.get("intentos"));
                regCoDiOBJ.setPrincipal((Boolean) rows.getFirst().get("principal"));

                registros.add(regCoDiOBJ);
            }

            if (registros.isEmpty()) {
                log.warn("No se encontró registro CoDi para el solicitante: {}", personaId);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            registros = Collections.emptyList();
        }

        return registros;
    }

    public RegistroCoDiOBJ consultarRegistroCoDiCliente(String personaId) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.consultarRegistroCoDiCliente";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get(cuentaString));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) rows.getFirst().get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) rows.getFirst().get("activo"));
                regCoDiOBJ.setTelefono_validado((String) rows.getFirst().get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) rows.getFirst().get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) rows.getFirst().get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) rows.getFirst().get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) rows.getFirst().get("intentos"));
            } else {
                log.error("No se encontro registro codi del solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }

    public RegistroCoDiOBJ obtenerRegistroCoDiPrincipal(String personaId) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.obtenerRegistroCoDiPrincipal";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get(cuentaString));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setPassword((String) row.get("password"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setRegistroNuevaApp((String) rows.getFirst().get("registro_nueva_app"));
                regCoDiOBJ.setActivo((Boolean) rows.getFirst().get("activo"));
                regCoDiOBJ.setTelefono_validado((String) rows.getFirst().get("telefono_validado"));
                regCoDiOBJ.setBloqueado((Integer) rows.getFirst().get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) rows.getFirst().get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) rows.getFirst().get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) rows.getFirst().get("intentos"));
            } else {
                log.error("No se encontro registro codi del solicitante {}", personaId);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            regCoDiOBJ = null;
        }
        return regCoDiOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO actualizarIdHardwareBySolicitanteId(String dispositivoId, String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("dispositivoId", dispositivoId);
            parameters.addValue("solicitante_id", solicitanteId);

            String query = "registro_codi.actualizarIdHardwareBySolicitanteId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar id hardware del solicitante {}", solicitanteId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar id hardware del solicitante " + solicitanteId);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public FavoritoOBJ obtenerFavoritoCuentaById(Integer id) {
        FavoritoOBJ favoritoOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("id", id);

            String query = "favoritos_transferencias.obtenerFavoritoCuentaById";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                favoritoOBJ = new FavoritoOBJ();

                favoritoOBJ.setId((Integer) row.get("id"));
                favoritoOBJ.setNumeroCuentaCoDi((String) row.get(numeroCuentaCodi));
                favoritoOBJ.setNombreInstitucion((String) row.get(nombreInstitucion));
                favoritoOBJ.setNombreBeneficiario((String) row.get(nombreBeneficiario));
                favoritoOBJ.setAlias((String) row.get(alias));
                favoritoOBJ.setNumeroCuentaBeneficiario((String) row.get(numeroCuentaBeneficiario));
                favoritoOBJ.setIdInstitucionBeneficiario((Integer) row.get(idInstitucionBeneficiario));
                favoritoOBJ.setTipoCuentaBeneficiario((Integer) row.get(idTipoCuentaBeneficiario));
            } else {
                log.error("No se encontro favorito con id {}", id);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            favoritoOBJ = null;
        }
        return favoritoOBJ;
    }

    public List<FavoritoOBJ> obtenerListadoFavoritosCuentaByCuentaAh(String cuentaah) {
        List<FavoritoOBJ> favoritosList = new ArrayList<>();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {

            //Se definen los parametros para el query
            parameters.addValue("cuenta_codi", cuentaah);

            String query = "favoritos_transferencias.obtenerListadoFavoritosCuentaByCuenta";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

           
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    FavoritoOBJ favoritoOBJ = new FavoritoOBJ();

                    favoritoOBJ.setId((Integer) row.get("id"));
                    favoritoOBJ.setNumeroCuentaCoDi((String) row.get(numeroCuentaCodi));
                    favoritoOBJ.setNombreInstitucion((String) row.get(nombreInstitucion));
                    favoritoOBJ.setNombreBeneficiario((String) row.get(nombreBeneficiario));
                    favoritoOBJ.setAlias((String) row.get(alias));
                    favoritoOBJ.setNumeroCuentaBeneficiario((String) row.get(numeroCuentaBeneficiario));
                    favoritoOBJ.setIdInstitucionBeneficiario((Integer) row.get(idInstitucionBeneficiario));
                    favoritoOBJ.setTipoCuentaBeneficiario((Integer) row.get(idTipoCuentaBeneficiario));
                    favoritosList.add(favoritoOBJ);
                }
            } else {
                log.error("No se encontraron favoritos para la cuenta {}", cuentaah);

            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return favoritosList.isEmpty() ? Collections.emptyList() : favoritosList;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO eliminaFavoritoCuentaById(Integer id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            //Se definen los parametros para el query
            parameters.addValue("id", id);

            String query = "favoritos_transferencias.eliminaFavoritoCuentaById";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar el operacion pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro finalizar el operacion pendiente");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public Boolean validarFavoritoExistente(String cuenta, String cuentaBeneficiario) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Boolean resultado = false;
        try {
            //Se definen los parametros para el query
            parameters.addValue(cuentaString, cuenta);
            parameters.addValue("cuentaBeneficiario", cuentaBeneficiario);

            String query = "favoritos_transferencias.validarFavoritoExistente";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            Integer count = namedCeroJdbc.queryForObject(psql, parameters, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return resultado;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registraFavoritoCuenta(RegistraFavoritoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            Instant now = Instant.now();

            req.setId(obtenerSecuenciaFavoritoTansferencias());

            parameters.addValue("id", req.getId());
            parameters.addValue(numeroCuentaCodi, req.getNumeroCuentaCoDi());
            parameters.addValue(nombreBeneficiario, req.getNombreBeneficiario());
            parameters.addValue(numeroCuentaBeneficiario, req.getNumeroCuentaBeneficiario());
            parameters.addValue(idTipoCuentaBeneficiario, req.getTipoCuentaBeneficiario());
            parameters.addValue(idInstitucionBeneficiario, req.getIdInstitucionBeneficiario());
            parameters.addValue(nombreInstitucion, req.getNombreInstitucion());
            parameters.addValue("correo_beneficiario", req.getCorreoBeneficiario());
            parameters.addValue(alias, req.getAlias());
            parameters.addValue("fecha_creacion", Timestamp.from(now));

            String query = "favoritos_transferencias.registraFavoritoCuenta";
            String psql = queries.getProperty(query);
            log.info(ejecutadoParamsMessage, query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {

                respuesta.setCodigo(0);
                respuesta.setMensaje("Registro realizado");
                if (minutosEsperaFavorito > 0) {
                    respuesta.setCodigo(1);
                    String mensajeFinal = String.format("Podrás realizar transferencias a este beneficiario después de %s minutos.", minutosEsperaFavorito);
                    respuesta.setMensaje(mensajeFinal);
                }
            } else {
                log.error("No se logro registrar el favorito para la cuenta {}", req.getNumeroCuentaCoDi());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el favorito para la cuenta");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public Integer obtenerSecuenciaFavoritoTansferencias() {
        try {
            String psql = queries.getProperty("secuencias.sigSecFavTrans");
            return ceroJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de favoritos transferencias:: " + e.getMessage()) {
            };
        }
    }
}
