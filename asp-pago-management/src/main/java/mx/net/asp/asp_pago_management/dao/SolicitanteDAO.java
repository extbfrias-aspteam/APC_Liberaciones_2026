package mx.net.asp.asp_pago_management.dao;

import java.math.BigInteger;
import java.util.*;

import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.model.Token2FAOBJ;
import mx.net.asp.asp_pago_management.config.QueryLoader;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.SolicitanteOBJ;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Log4j2
@Repository
public class SolicitanteDAO {

    private final Properties queriesSolicitante;
    private final Properties queriesCodigos;
    private final JdbcTemplate procreaJdbc;
    private final NamedParameterJdbcTemplate namedProcreaJdbc;
    private final ErrorHandler errorHandler;
    private final String nombre = "nombre";
    private final String numero = "numero";
    private final String codigo = "codigo";
    private final String telefono = "telefono";
    private final String correo = "correo";
    private final String ejecutandoMessage = "Ejecutando consulta: {}, parametros: {}";

    public SolicitanteDAO(
            @Qualifier("procreaJdbcTemplate") JdbcTemplate procreaJdbc,
            @Qualifier("namedProcreaJdbcTemplate") NamedParameterJdbcTemplate namedProcreaJdbc,
            ErrorHandler errorHandler
    ) {
        this.queriesSolicitante = QueryLoader.loadYaml("procrea", "solicitante.yml");
        this.queriesCodigos = QueryLoader.loadYaml("procrea", "codigos.yml");
        this.errorHandler = errorHandler;
        this.procreaJdbc = procreaJdbc;
        this.namedProcreaJdbc = namedProcreaJdbc;
    }

    public SolicitanteOBJ obtenerInfoBasicaSolicitanteById(String idSolicitante) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            // Se definen los parametros para el query
            parameters.addValue(numero, idSolicitante);

            String query = "solicitante.obtenerDatosBasicosById";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get(numero));
                solicitanteOBJ.setNombre((String) row.get(nombre));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get(correo));
                solicitanteOBJ.setTelefonoCoDi((String) row.get(telefono));
                solicitanteOBJ.setNombres((String) row.get("nombre_p"));
                solicitanteOBJ.setApellidoPaterno((String) row.get("apellidos"));
                solicitanteOBJ.setApellidoMaterno((String) row.get("apellido_m"));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public SolicitanteOBJ obtenerInfoBasicaSolicitanteByTelefonoCoDi(String telefonoCoDi) {
        SolicitanteOBJ solicitanteOBJ = null;
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        //Se definen los parametros para el query
        try {
            parameters.addValue("telefonoCoDi", telefonoCoDi);

            String query = "solicitante.obtenerDatosBasicosByTelefonoCoDi";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get(numero));
                solicitanteOBJ.setNombre((String) row.get(nombre));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get(correo));
                solicitanteOBJ.setTelefonoCoDi((String) row.get(telefono));
            } else {
                log.error("No se encontro informacion del solicitante con telefono {}", telefonoCoDi);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            solicitanteOBJ = null;
        }
        return solicitanteOBJ;
    }

    public RespuestaDTO obtenerInfoGeneralSolicitanteById(String idSolicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Gson gson = new Gson();
        try {
            // Se definen los parametros para el query
            parameters.addValue(numero, idSolicitante);

            String query = "solicitante.obtenerDatosGeneralesById";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                SolicitanteOBJ solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get(numero));
                solicitanteOBJ.setNombre((String) row.get(nombre));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get(correo));
                solicitanteOBJ.setColonia((Integer) row.get("colonia"));
                solicitanteOBJ.setTelefonoCoDi((String) row.get(telefono));
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

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(solicitanteOBJ));
            } else {
                log.error("No se encontro informacion del solicitante {}", idSolicitante);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontró información del solicitante");
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
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                solicitanteOBJ = new SolicitanteOBJ();

                solicitanteOBJ.setNumero((String) row.get(numero));
                solicitanteOBJ.setNombre((String) row.get(nombre));
                solicitanteOBJ.setCurp((String) row.get("curp"));
                solicitanteOBJ.setRfc((String) row.get("rfc"));
                solicitanteOBJ.setCorreo((String) row.get(correo));
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

    public String buscarIdSolicitanteByTelefonoCoDi(String telefono) {
        String personaId = "";
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("telefonoCoDi", telefono);

            String query = "directorio_telefonico.buscarIdSolicitanteByTelefonoCoDi";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                personaId = (String) row.get("id_solicitante");
            } else {
                log.warn("No se encontro telefono CoDi registrado con telefono {}", telefono);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            personaId = "";
        }
        return personaId;
    }

    public RespuestaDTO obtenerDomicilioSolicitanteV2(String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            // Se definen los parametros para el query
            parameters.addValue(numero, solicitanteId);

            String query = "solicitante.obtenerDomicilioByNumero";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get("domicilio"));
            } else {
                log.error("No se encontro domicilio con el id del solicitante {}", solicitanteId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro domicilio con el id del solicitante " + solicitanteId);
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

    public RespuestaDTO buscarSolicitanteCertificadoByCurp(String curp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> rows;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("curp", curp);

            String query = "solicitante_ASPPAgo_RFCVALIDO.buscarSolicitanteCertificadoByCurp";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.getFirst();

                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData((String) row.get(numero));
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
    public RespuestaDTO insertarTokenMensaje(String tokenCifrado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        List<Map<String, Object>> rows;
        try {
            parameters.addValue(codigo, tokenCifrado);

            String query = "codigos2fa.insertarTokenMensaje";
            String psql = queriesCodigos.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            rows = namedProcreaJdbc.queryForList(psql, parameters);
            if (!rows.isEmpty()) {
                Object idObj = rows.getFirst().get("id");
                BigInteger idToken = (idObj != null) ? new BigInteger(idObj.toString()) : BigInteger.ZERO;
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(idToken != null ? idToken.toString() : "0");
            } else {
                log.error("No se logro registrar el token: {}", tokenCifrado);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se logró registrar la cuenta");
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
    public RespuestaDTO actualizaToken(Long idToken) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            // Definición de parámetros para la actualización
            parameters.addValue(codigo, idToken);

            String query = "codigos2fa.actualizaToken";
            String psql = queriesCodigos.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            int rows = namedProcreaJdbc.update(psql, parameters);
            if (rows > 0) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
            } else {
                log.info("No se logro actualizar el token con id: {}", idToken);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se realizó la actualización");
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

    public Token2FAOBJ obtenerToken(String telefono, String tokenEncriptado) {
        Token2FAOBJ token2FAOBJ = null;
        Map<String, Object> row;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        try {
            //Se definen los parametros para el query
            parameters.addValue("celular", telefono);
            parameters.addValue(codigo, tokenEncriptado);

            String query = "codigos2fa.obtenerToken";
            String psql = queriesCodigos.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

            row = namedProcreaJdbc.queryForMap(psql, parameters);
            if (!row.isEmpty()) {
                token2FAOBJ = new Token2FAOBJ();
                token2FAOBJ.setStatus((Integer) row.get("status"));
                token2FAOBJ.setMinutos((Integer) row.get("minutos"));
                token2FAOBJ.setId_codigo((Long) row.get("id_codigo"));
            } else {
                log.info("No se encontró ningún registro para el token: {}", tokenEncriptado);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            token2FAOBJ = null;
        }
        return token2FAOBJ;
    }

    public RespuestaDTO obtenerFechaHora() {
        RespuestaDTO respuesta = new RespuestaDTO();
        Map<String, Object> row;
        try {
            String query = "tiempo.obtenerFechaHora";
            String psql = queriesSolicitante.getProperty(query);
            log.info("Ejecutando consulta: {}", query);

            row = procreaJdbc.queryForMap(psql);
            if (row != null && row.containsKey("hora")) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("Consulta exitosa");
                respuesta.setData(String.valueOf(row.get("hora")));
            } else {
                log.warn("No se encontró información para la consulta de fecha y hora");
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontraron registros");
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
    public RespuestaDTO registrarBitacoraCambio(String control, String tipo, Integer usuarioAut, String actual, String nuevo, String obs) {
        RespuestaDTO respuesta = new RespuestaDTO();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        int rows;
        try {
            parameters.addValue("control", control);
            parameters.addValue("tipo", tipo);
            parameters.addValue("usuario_aut", usuarioAut);
            parameters.addValue("actual", actual);
            parameters.addValue("nuevo", nuevo);
            parameters.addValue("obs", obs);

            String query = "solicitante.registrarBitacoraCambio";
            String psql = queriesSolicitante.getProperty(query);
            log.info(ejecutandoMessage, query, parameters.getValues());

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
}
