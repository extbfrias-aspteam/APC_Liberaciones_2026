package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.OnboardingEjecutadoOBJ;
import mx.net.asp.asp_pago_onb.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_onb.model.RegistroServiciosDigitalesOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Repository
public class CoDiDAO {
    private final Properties queries;
    private final JdbcTemplate ceroJdbc;
    private final NamedParameterJdbcTemplate namedCeroJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public CoDiDAO(
            @Qualifier("ceroJdbcTemplate") JdbcTemplate ceroJdbc,
            @Qualifier("namedCeroJdbcTemplate") NamedParameterJdbcTemplate namedCeroJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("cero", "codi.yml");
        this.ceroJdbc = ceroJdbc;
        this.namedCeroJdbc = namedCeroJdbc;
        this.errorHandler = errorHandler;
    }

    public List<RegistroCoDiOBJ> obtenerListRegistroCoDiByCuentasAh(List<String> cuentasAh) {
        List<RegistroCoDiOBJ> registros = new ArrayList<>();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            // Parámetro para la lista
            parameters.addValue("cuentasAh", cuentasAh);
    
            String query = "registro_codi.obtenerListRegistroCoDiByCuentasAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());
    
            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
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
                    registros.add(regCoDiOBJ);
                }
            } else {
                log.error("No se encontraron registros codi para las cuentas {}", cuentasAh);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
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
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
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
        }
        return regCoDiOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO guardarRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("cuenta", registroCoDiOBJ.getCuenta());
            parameters.addValue("personaId", registroCoDiOBJ.getSolicitanteId());
            parameters.addValue("folio", registroCoDiOBJ.getFolioContrato());
            parameters.addValue("estatus", registroCoDiOBJ.getEstatus());
            parameters.addValue("numero_celular", registroCoDiOBJ.getTelefonoCoDi());
            parameters.addValue("principal", registroCoDiOBJ.getPrincipal());

            String query = "registro_codi.guardarRegistroCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el registro CoDi la cuenta {}", registroCoDiOBJ.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro guardar el registro CoDi");
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
    public RespuestaDTO guardarRegistroServiciosDigitales(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("cuentaah", registroCoDiOBJ.getCuenta());
            parameters.addValue("password", registroCoDiOBJ.getPassword());
            parameters.addValue("folio", registroCoDiOBJ.getFolioContrato());
            parameters.addValue("estatus", registroCoDiOBJ.getEstatus());
            parameters.addValue("registroNuevaApp", registroCoDiOBJ.getRegistroNuevaApp());
            parameters.addValue("telefono_validado", registroCoDiOBJ.getTelefono_validado());

            String query = "registro_servicios_digitales.guardarRegistroServiciosDigitales";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar el registro servicios digitales la cuenta {}", registroCoDiOBJ.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro guardar el registro servicios digitales");
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
    public RespuestaDTO actualizarRegistroCoDiActivo(String cuentaah, String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("personaId", personaId);

            String query = "registro_codi.actualizarRegistroCoDiActivo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar la cuenta activa en registro CoDi: {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro ctualizar la cuenta activa en registro CoDi");
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
    public RespuestaDTO actualizarRegistroCoDiById(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", registroCoDiOBJ.getId());
            parameters.addValue("folio", registroCoDiOBJ.getFolioContrato());
            parameters.addValue("estatus", registroCoDiOBJ.getEstatus());
            parameters.addValue("numero_celular", registroCoDiOBJ.getTelefonoCoDi());
            parameters.addValue("principal", registroCoDiOBJ.getPrincipal());
            parameters.addValue("solicitante", registroCoDiOBJ.getSolicitanteId());

            String query = "registro_codi.actualizarRegistroCoDiById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el registro CoDi la cuenta {}", registroCoDiOBJ.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro guardar el registro CoDi");
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
    public RespuestaDTO actualizarRegistroServiciosDigitalesById(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", registroCoDiOBJ.getId());
            parameters.addValue("password", registroCoDiOBJ.getPassword());
            parameters.addValue("folio", registroCoDiOBJ.getFolioContrato());
            parameters.addValue("estatus", registroCoDiOBJ.getEstatus());
            parameters.addValue("registroNuevaApp", registroCoDiOBJ.getRegistroNuevaApp());
            parameters.addValue("telefono_validado", registroCoDiOBJ.getTelefono_validado());

            String query = "registro_servicios_digitales.actualizarRegistroServiciosDigitalesById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el registro servicios digitales la cuenta {}", registroCoDiOBJ.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar el registro servicios digitales");
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
    public RespuestaDTO actualizarVersionAppRegistroServiciosDigitales(String cuentaah, String versionApp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("cuentaah", cuentaah);
            parameters.addValue("registroNuevaApp", versionApp);

            String query = "registro_servicios_digitales.actualizarVersionAppRegistroServiciosDigitales";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar la version de la app de la cuenta {}", cuentaah);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro actualizar la version de la app");
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
    public RespuestaDTO actualizarBloquearRegistroCoDi(RegistroCoDiOBJ registroCoDi) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("bloqueado", registroCoDi.getBloqueado());
            parameters.addValue("fbloqueo", registroCoDi.getFechaBloqueo());
            parameters.addValue("cuentaah", registroCoDi.getCuenta());

            String query = "registro_codi.actualizarBloquearRegistroCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar el estatus del bloqueo de la cuenta {}", registroCoDi.getCuenta());
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar el estatus del bloqueo de la cuenta " + registroCoDi.getCuenta());
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

    public RegistroCoDiOBJ obtenerRegistroCoDiIndividualByCuentaAh(String cuentaah) {
        RegistroCoDiOBJ regCoDiOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "registro_codi.obtenerRegistroCoDiIndividualByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                regCoDiOBJ = new RegistroCoDiOBJ();

                regCoDiOBJ.setId((Integer) row.get("id"));
                regCoDiOBJ.setCuenta((String) row.get("cuenta"));
                regCoDiOBJ.setSolicitanteId((String) row.get("solicitante_id"));
                regCoDiOBJ.setIdHardware((String) row.get("id_hardware"));
                regCoDiOBJ.setBloqueado((Integer) rows.getFirst().get("bloqueado"));
                regCoDiOBJ.setFechaBloqueo((Timestamp) rows.getFirst().get("fecha_bloqueo"));
                regCoDiOBJ.setBloqueosTemporales((Integer) rows.getFirst().get("bloqueos_temporales"));
                regCoDiOBJ.setIntentos((Integer) rows.getFirst().get("intentos"));
            } else {
                log.error("No se encontro registro codi de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return regCoDiOBJ;
    }

    public RegistroServiciosDigitalesOBJ obtenerRegistroServiciosDigitalesIndividualByCuentaAh(String cuentaah) {
        RegistroServiciosDigitalesOBJ registroServiciosDigitalesOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("cuentaah", cuentaah);

            String query = "registro_servicios_digitales.obtenerRegistroServiciosDigitalesIndividualByCuentaAh";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                registroServiciosDigitalesOBJ = new RegistroServiciosDigitalesOBJ();

                registroServiciosDigitalesOBJ.setId((Integer) row.get("id"));
                registroServiciosDigitalesOBJ.setCuenta((String) row.get("cuenta"));
                registroServiciosDigitalesOBJ.setPassword((String) row.get("password"));

            } else {
                log.error("No se encontro registro servicios digitales de la cuenta {}", cuentaah);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return registroServiciosDigitalesOBJ;
    }

    public OnboardingEjecutadoOBJ buscaOnboardingEjecutadoPendiente(String rfc, String curp, String telefono) {
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        OnboardingEjecutadoOBJ onboardingEjecutadoOBJ = null;
        try {
            //Se definen los parametros para el query
            parameters.addValue("rfc", rfc);
            parameters.addValue("curp", curp);
            parameters.addValue("telefono", telefono);

            String query = "onboardings_ejecutados.buscaOnboardingEjecutadoPendiente";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                onboardingEjecutadoOBJ = new OnboardingEjecutadoOBJ();
                onboardingEjecutadoOBJ.setId((Integer) row.get("id"));
                onboardingEjecutadoOBJ.setFechaCreacion(((Timestamp) row.get("fecha_creacion"))); // Convierte a String
            } else {
                log.warn("No se encontro onboarding pendiente con los siguientes datos:: {}, {}, {}", rfc, curp, telefono);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return onboardingEjecutadoOBJ;
    }

    @Transactional(transactionManager = "transactionManagerCero")
    public RespuestaDTO registraOnboardingEjecutado(String rfc, String curp, String telefono) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Integer idOnboardingEjecutado;
        try {
            parameters.addValue("rfc", rfc);
            parameters.addValue("curp", curp);
            parameters.addValue("telefono", telefono);

            String query = "onboardings_ejecutados.registraOnboardingEjecutado";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                idOnboardingEjecutado = (Integer) rows.getFirst().get("id");
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(idOnboardingEjecutado));
            } else {
                log.error("No se logro registrar el onboarding pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el onboarding pendiente");
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
    public RespuestaDTO finalizaOnboardingEjecutado(Integer id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);

            String query = "onboardings_ejecutados.finalizaOnboardingEjecutado";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedCeroJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro finalizar el onboarding pendiente");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro finalizar el onboarding pendiente");
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
}
