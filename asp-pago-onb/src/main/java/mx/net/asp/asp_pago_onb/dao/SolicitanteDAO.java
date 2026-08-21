package mx.net.asp.asp_pago_onb.dao;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.config.QueryLoader;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Log4j2
@Repository
public class SolicitanteDAO {
    private final Properties queries;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;

    @Autowired
    public SolicitanteDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queries = QueryLoader.loadYaml("procrea", "solicitante.yml");
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
        this.errorHandler = errorHandler;
    }

    public SolicitanteOBJ obtenerInfoBasicaSolicitanteByTelefonoCoDi(String telefonoCoDi) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("telefonoCoDi", telefonoCoDi);

            String query = "solicitante.obtenerDatosBasicosByTelefonoCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));
            } else {
                log.error("No se encontro informacion del solicitante con telefono {}", telefonoCoDi);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public SolicitanteOBJ obtenerInfoGeneralSolicitanteById(String idSolicitante) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("numero", idSolicitante);

            String query = "solicitante.obtenerDatosGeneralesById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setNombre_p((String) row.get("nombre_p"));
                solicitanteOBJ.setApellido_p((String) row.get("apellido_p"));
                solicitanteOBJ.setApellido_m((String) row.get("apellido_M"));
                solicitanteOBJ.setFecha_nacimiento((String) row.get("fecha_nacimiento"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setColonia((Integer) row.get("colonia"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get("telefono"));
                solicitanteOBJ.setCatDomicilio1((Integer) row.get("cat_domicilio1"));
                solicitanteOBJ.setCatDomicilio2((Integer) row.get("cat_domicilio2"));
                solicitanteOBJ.setCatDomicilio3((Integer) row.get("cat_domicilio3"));
                solicitanteOBJ.setCatDomicilio4((Integer) row.get("cat_domicilio4"));
                solicitanteOBJ.setCatDomicilio5((Integer) row.get("cat_domicilio5"));
                solicitanteOBJ.setDescripcionDomicilio1((String) row.get("descripcion_domicilio1"));
                solicitanteOBJ.setDescripcionDomicilio2((String) row.get("descripcion_domicilio2"));
                solicitanteOBJ.setDescripcionDomicilio3((String) row.get("descripcion_domicilio3"));
                solicitanteOBJ.setDescripcionDomicilio4((String) row.get("descripcion_domicilio4"));
                solicitanteOBJ.setDescripcionDomicilio5((String) row.get("descripcion_domicilio5"));
                solicitanteOBJ.setDomicilioObservaciones((String) row.get("domicilo_observaciones"));
                solicitanteOBJ.setDomicilio((String) row.get("domicilio"));
                solicitanteOBJ.setRfc1((String) row.get("rfc1"));
                solicitanteOBJ.setRfc2((String) row.get("rfc2"));
                solicitanteOBJ.setRfc3((String) row.get("rfc3"));
                solicitanteOBJ.setSexo((String) row.get("sexo"));
                solicitanteOBJ.setTPersona((String) row.get("t_persona"));
                solicitanteOBJ.setNumeroCasa((String) row.get("numero_casa"));
                solicitanteOBJ.setCelular((String) row.get("celular"));
                solicitanteOBJ.setTelefono((String) row.get("telefono"));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public RespuestaDTO obtenerSolicitanteByCorreo(String correo) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("correo", correo);

            String query = "solicitante.obtenerIdSolicitanteByCorreo";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("numero"));
            } else {
                log.error("No se encontro solicitante mediante correo {}", correo);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro solicitante mediante correo " + correo);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public SolicitanteOBJ buscarSolicitanteByCurp(String curp) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("curp", curp);

            String query = "solicitante.buscarSolicitanteByCurp";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get("numero"));
                solicitanteOBJ.setNombre((String) row.get("nombre"));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get("correo"));
                solicitanteOBJ.setBloqueado((Integer) row.get("bloqueado"));
                solicitanteOBJ.setApellido_p((String) row.get("apellido_p"));
                solicitanteOBJ.setApellido_m((String) row.get("apellido_m"));
                solicitanteOBJ.setNombre_p((String) row.get("nombre_p"));
                solicitanteOBJ.setTPersona((String) row.get("t_persona"));
            } else {
                log.error("No se encontro informacion del solicitante con curp {}", curp);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public RespuestaDTO buscarSolicitanteCertificadoByCurp(String curp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("curp", curp);

            String query = "solicitante_ASPPAgo_RFCVALIDO.buscarSolicitanteCertificadoByCurp";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("numero"));
            } else {
                log.warn("No se encontro solicitante certificado con curp {}", curp);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró solicitante certificado con curp " + curp);
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarNuevoSolicitante(SolicitanteNewOBJ s) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            // Generar el número de solicitante
            s.setNumero(obtenerSecuenciaSolicitante());
            Long numeroSolicitante = Long.parseLong(s.getNumero());
            boolean existeSolicitante;

            log.info("Solicitante obtenido de secuencia: {}", s.getNumero());

            // Validar si el solicitante ya existe y ajustar el número si es necesario
            do {
                parameters.addValue("numero", String.valueOf(numeroSolicitante));
                String psqlbool = queries.getProperty("solicitante.validaExisteSolicitante");
                existeSolicitante = Boolean.TRUE.equals(namedProcreaJdbc.queryForObject(psqlbool, parameters, Boolean.class));
                if (existeSolicitante) {
                    numeroSolicitante++;
                    log.info("### Solicitante ya existe, incrementando secuencia");
                } else {
                    s.setNumero(String.valueOf(numeroSolicitante));
                }
            } while (existeSolicitante);

            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("nombre", s.getNombre());
            parameters.addValue("numero", s.getNumero());
            parameters.addValue("nombreP", s.getNombre_p());
            parameters.addValue("apellidos", s.getApellido_p());
            parameters.addValue("apellidoM", s.getApellido_m());
            parameters.addValue("domicilio", s.getDomicilio());
            parameters.addValue("colonia", s.getColonia());
            parameters.addValue("celular", s.getCelular());
            parameters.addValue("telefono", s.getTelefono());
            parameters.addValue("sexo", s.getSexo());
            parameters.addValue("edoCivil", s.getEdoCivil());
            parameters.addValue("rfc1", s.getRfc1());
            parameters.addValue("rfc2", s.getRfc2());
            parameters.addValue("rfc3", s.getRfc3());
            parameters.addValue("curp", s.getCurp());
            parameters.addValue("tPersona", s.getTPersona());
            parameters.addValue("correo", s.getCorreo());
            parameters.addValue("numeroCasa", s.getNumeroCasa());
            parameters.addValue("edoNacId", s.getEdoNacId());
            parameters.addValue("cveOcupacion", s.getCveOcupacion());
            parameters.addValue("giro", s.getGiroId());
            parameters.addValue("puesto", s.getPuesto());
            parameters.addValue("descripcionDomicilio1", s.getDescripcionDomicilio1());

            String query = "solicitante.registrarNuevoSolicitante";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Solicitante registrado exitosamente, numero: {}", s.getNumero());
                respuesta.setCodigo(0);
                respuesta.setMensaje("Solicitante registrado exitosamente");
                respuesta.setData(s.getNumero());
            } else {
                log.error("No se logro registrar el solicitante");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el solicitante");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarNuevoDirectorioTelefonico(DirectorioTelefonicoOBJ dt) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            // Generar el id del directorio telefonico
            dt.setIdDirectorioTelefonico(obtenerSecuenciaDirectorioTelefonico());
            Integer telefonoCoDi = dt.getIdDirectorioTelefonico();
            boolean existeTelefono;

            log.info("Id directorio telefonico obtenido de secuencia: {}", dt.getIdDirectorioTelefonico());

            // Validar si el id del directorio telefonico ya existe y ajustar el número si es necesario
            do {
                parameters.addValue("id", telefonoCoDi);
                String psqlbool = queries.getProperty("directorio_telefonico.validaExisteIdDirTelefonico");
                existeTelefono = Boolean.TRUE.equals(namedProcreaJdbc.queryForObject(psqlbool, parameters, Boolean.class));
                if (existeTelefono) {
                    telefonoCoDi++;
                    log.info("### Id directorio telefonico ya existe, incrementando secuencia");
                } else {
                    dt.setIdDirectorioTelefonico(telefonoCoDi);
                }
            } while (existeTelefono);

            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("id", dt.getIdDirectorioTelefonico());
            parameters.addValue("personaId", dt.getIdSolicitante());
            parameters.addValue("idcatTelefono", dt.getIdCatTelefono());
            parameters.addValue("telefonoCoDi", dt.getTelefono());
            parameters.addValue("observaciones", dt.getObservaciones());

            String query = "directorio_telefonico.registraTelefonoCoDi";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Telefono CoDi registrado exitosamente, telefono: {}", dt.getTelefono());
                respuesta.setCodigo(0);
                respuesta.setMensaje("Telefono CoDi registrado exitosamente");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el Telefono CoDi");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el Telefono CoDi");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO borrarConceptobySolicitantedId(String personaId, Integer conceptoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters;
        int rows;
        try {
            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("personaId", personaId);
            parameters.addValue("conceptoId", conceptoId);

            String query = "conceptos_solicitantes.borrarConceptoBySolicitanteId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Concepto {} del solicitante {} eliminado correctamente", conceptoId, personaId);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro eliminar el concepto {} del solicitante {}", conceptoId, personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró reliminar el concepto del solicitante");
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarConceptoSolicitante(String personaId, Integer conceptoId, String valor) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters;
        int rows;
        try {
            // Preparar parametros para la consulta
            parameters = new MapSqlParameterSource();
            parameters.addValue("personaId", personaId);
            parameters.addValue("conceptoId", conceptoId);
            parameters.addValue("valor", valor);

            String query = "conceptos_solicitantes.registrarConceptoBySolicitanteId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                log.info("Concepto {} del solicitante {} registrado correctamente", conceptoId, personaId);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el concepto {} del solicitante {}", conceptoId, personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar el concepto del solicitante");
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

    public List<ConceptoSolicitanteOBJ> obtenerListadoConceptosSolicitanteByPersonaId(String personaId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        List<ConceptoSolicitanteOBJ> conceptosList = new ArrayList<>();
        try {
            parameters.addValue("personaId", personaId);

            String query = "conceptos_solicitantes.obtenerListadoConceptosSolicitanteByPersonaId";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                for (Map<String, Object> row : rows) {
                    ConceptoSolicitanteOBJ conceptoSolicitanteOBJ = new ConceptoSolicitanteOBJ();
                    conceptoSolicitanteOBJ.setId(((Integer) row.get("id")));
                    conceptoSolicitanteOBJ.setClave((String) row.get("clave"));
                    conceptosList.add(conceptoSolicitanteOBJ);
                }
            } else {
                log.error("No se encontro listado de conceptos cuenta Simplificada (cero)");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList(); // Retorna lista vacía en caso de error
        }
        return conceptosList.isEmpty() ? Collections.emptyList() : conceptosList;
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO guardarConceptoSolicitante(String clave, String valor, String personaId, String optext) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("clave", clave);
            parameters.addValue("valor", valor);
            parameters.addValue("personaId", personaId);
            parameters.addValue("optext", optext);

            String query = "conceptos_solicitantes.guardarConceptoSolicitante";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro guardar concepto del solicitante con clave {}", clave);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró guardar concepto del solicitante con clave " + clave);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO actualizarConceptoSolicitanteById(Integer id, String valor, String optext) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("id", id);
            parameters.addValue("valor", valor);
            parameters.addValue("optext", optext);

            String query = "conceptos_solicitantes.actualizarConceptoSolicitanteById";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar concepto del solicitante con id {}", id);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar concepto del solicitante con id " + id);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO actualizarDatosSolicitante(String numero, String domicilio, String celular, String correo) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("numero", numero);
            parameters.addValue("domicilo_observaciones", domicilio);
            parameters.addValue("celular", celular);
            parameters.addValue("correo", correo);

            String query = "solicitante.actualizarDatosSolicitante";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro actualizar datos del solicitante con id {}", numero);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró actualizar datos del solicitante con id " + numero);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public RespuestaDTO registrarBitacoraCambio(String control, String tipo, Integer usuario_aut, String actual, String nuevo, String obs) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("control", control);
            parameters.addValue("tipo", tipo);
            parameters.addValue("usuario_aut", usuario_aut);
            parameters.addValue("actual", actual);
            parameters.addValue("nuevo", nuevo);
            parameters.addValue("obs", obs);

            String query = "solicitante.registrarBitacoraCambio";
            String psql = queries.getProperty(query);
            log.info("Ejecutando consulta: {}, parametros: {}", query, parameters.getValues());

            rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.error("No se logro registrar el cambio de datos del solicitante con id {}", control);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logro registrar el cambio de datos del solicitante con id " + control);
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

    @Transactional(transactionManager = "transactionManagerProcrea")
    public String obtenerSecuenciaSolicitante() {
        try {
            String psql = queries.getProperty("secuencias.sigSecSolicitante");
            return procreaJdbc.queryForObject(psql, String.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de solicitante:: " + e.getMessage()) {
            };
        }
    }

    @Transactional(transactionManager = "transactionManagerProcrea")
    public Integer obtenerSecuenciaDirectorioTelefonico() {
        try {
            String psql = queries.getProperty("secuencias.sigSecDirectorioTelefonico");
            return procreaJdbc.queryForObject(psql, Integer.class);
        } catch (Exception e) {
            throw new DataAccessException("No se pudo obtener la secuencia de directorio telefonico:: " + e.getMessage()) {
            };
        }
    }
}
