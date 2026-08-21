package mx.net.asp.asp_pago_jwt.service;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.dao.CoDiDAO;
import mx.net.asp.asp_pago_jwt.dao.SeguridadDAO;
import mx.net.asp.asp_pago_jwt.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.model.*;
import mx.net.asp.asp_pago_jwt.request.RefreshTokenReq;
import mx.net.asp.asp_pago_jwt.request.ValidaJWTReq;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.IdentificadorHelper;
import mx.net.asp.asp_pago_jwt.utilerias.JwtUtil;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresJwtToken;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresValidaJwtToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidacionesJwtService {

    private final CoDiDAO codiDAO;
    private final SeguridadDAO seguridadDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final ErrorHandler errorHandler;
    private final JwtUtil jwtUtil;
    private final IdentificadorHelper identificadorHelper;
    private final Gson gson;

    @Value("${version.app}")
    private String versionApp;
    @Value("${jwt.estatus.valid}")
    private String jwtValid;
    @Value("${jwt.estatus.expired}")
    private String jwtExpired;
    @Value("${jwt.estatus.revoked}")
    private String jwtRevoked;
    @Value("${path.login.version}")
    private String pathLogInV;
    @Value("${path.pwd.reset}")
    private String pathPwdReset;

    public RespuestaDTO validaJWTTokenInicial(ValidaJWTReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = validaJWT(req);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            AccessTokenOBJ accessTokenOBJ = gson.fromJson(respuesta.getData(), AccessTokenOBJ.class);
            respuesta = validaClaimsJWTInicial(req, accessTokenOBJ);

            if (respuesta.getCodigo() == 0) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "OK", "JWT VALIDADO EXITOSAMENTE", 0, null, req.getIdCanal());
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());

            seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                    req.getJwtToken(), "ERROR", e.getMessage(),
                    ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());
        }
        return respuesta;
    }

    public RespuestaDTO validaJWT(ValidaJWTReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String jwtHeader = req.getJwtToken();
            if (jwtHeader == null || jwtHeader.isEmpty()) {
                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                        null, "ERROR", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_MISSING),
                        ErroresValidaJwtToken.JWT_MISSING, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        -401,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_MISSING));
            }

            JwtUtil.TokenInvalidReason reason = jwtUtil.getTokenInvalidationReason(jwtHeader);
            AccessTokenOBJ accessTokenOBJ;
            if (reason == JwtUtil.TokenInvalidReason.VALID) {
                // Es un JWT valido
                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                        jwtHeader, "INFO", "JWT TOKEN VALIDO, SE PROCEDE A BUSCAR COMO VIGENTE EN BD",
                        1, null, req.getIdCanal());

                // Validamos que este VIGENTE en BD
                accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtValid);
                if (accessTokenOBJ == null) {
                    // JWT Valido pero no VIGENTE en BD
                    // Registrar log
                    seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                            jwtHeader, "INFO", "JWT TOKEN VALIDO, NO ENCONTRADO COMO VIGENTE EN BD, SE PROCEDE A BUSCAR COMO REVOCADO EN BD",
                            1, null, req.getIdCanal());

                    // Validamos que el JWT token enviado este en BD como REVOCADO
                    accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtRevoked);
                    if (accessTokenOBJ != null) {
                        // JWT VALIDO encontrado como REVOCADO en BD, regresamos -402
                        // Registrar log
                        seguridadDAO.registraJwtTokenLog(
                                accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                                accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_VALID_REVOKED),
                                ErroresValidaJwtToken.JWT_VALID_REVOKED, null, req.getIdCanal()
                        );

                        return RespuestaUtils.asignarError(respuesta,
                                -402,
                                "JWT token revoked");
                    }

                    // JWT EXPIRADO NO encontrado como VIGENTE o REVOCADO en BD, regresamos -403
                    // Registrar log
                    seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                            jwtHeader, "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_VALID_NOT_FOUND),
                            ErroresValidaJwtToken.JWT_VALID_NOT_FOUND, null, req.getIdCanal());

                    return RespuestaUtils.asignarError(respuesta,
                            -402,
                            "Invalid JWT token");
                } else {
                    seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                            jwtHeader, "INFO", "JWT TOKEN VALIDO, ENCONTRADO COMO VIGENTE EN BD",
                            1, null, req.getIdCanal());
                }
            } else if (reason == JwtUtil.TokenInvalidReason.EXPIRED) {
                // Es un JWT EXPIRADO pero valido por la firma, buscamos si esta VIGENTE en BD
                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                        jwtHeader, "INFO", "JWT TOKEN EXPIRADO, SE PROCEDE A BUSCAR COMO VIGENTE EN BD",
                        1, null, req.getIdCanal());

                accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtValid);
                if (accessTokenOBJ != null) {
                    // JWT EXPIRADO encontrado como VIGENTE en BD procedemos a Expirar en la BD y regresamos -402
                    seguridadDAO.expirarAccessToken(accessTokenOBJ.getId());
                    // Registrar log
                    seguridadDAO.registraJwtTokenLog(
                            accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                            accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_EXPIRED),
                            ErroresValidaJwtToken.JWT_EXPIRED, null, req.getIdCanal()
                    );

                    return RespuestaUtils.asignarError(respuesta,
                            -402,
                            "JWT token expired");
                } else {
                    // JWT EXPIRADO NO encontrado como VIGENTE en BD procedemos a buscarlo como EXPIRADO en BD
                    seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                            jwtHeader, "INFO", "JWT TOKEN EXPIRADO, NO ENCONTRADO COMO VIGENTE EN BD, SE PROCEDE A BUSCAR COMO EXPIRADO EN BD",
                            1, null, req.getIdCanal());

                    accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtExpired);
                    if (accessTokenOBJ != null) {
                        // JWT EXPIRADO encontrado como EXPIRADO en BD, regresamos -402 (este seria el caso de SAID)
                        // Registrar log
                        seguridadDAO.registraJwtTokenLog(
                                accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                                accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_EXPIRED_BD_EXPIRED),
                                ErroresValidaJwtToken.JWT_EXPIRED_BD_EXPIRED, null, req.getIdCanal()
                        );

                        return RespuestaUtils.asignarError(respuesta,
                                -402,
                                "JWT token expired");
                    } else {

                        seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                                jwtHeader, "INFO", "JWT TOKEN EXPIRADO, NO ENCONTRADO COMO EXPIRADO EN BD, SE PROCEDE A BUSCAR COMO REVOCADO EN BD",
                                1, null, req.getIdCanal());

                        // Validamos que el JWT token enviado este en BD como REVOCADO
                        accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtRevoked);
                        if (accessTokenOBJ != null) {
                            // JWT INVALIDO encontrado como REVOCADO en BD, regresamos -402
                            // Registrar log
                            seguridadDAO.registraJwtTokenLog(
                                    accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                                    accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_EXPIRED_REVOKED),
                                    ErroresValidaJwtToken.JWT_EXPIRED_REVOKED, null, req.getIdCanal()
                            );

                            return RespuestaUtils.asignarError(respuesta,
                                    -402,
                                    "JWT token revoked");
                        }

                        // JWT EXPIRADO NO encontrado como VIGENTE, EXPIRADO o REVOCADO  en BD, regresamos -403
                        // Registrar log
                        seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                                jwtHeader, "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_EXPIRED_NOT_FOUND),
                                ErroresValidaJwtToken.JWT_EXPIRED_NOT_FOUND, null, req.getIdCanal());

                        return RespuestaUtils.asignarError(respuesta,
                                -402,
                                "Invalid JWT token");

                    }
                }
            } else if (reason == JwtUtil.TokenInvalidReason.SIGNATURE_INVALID) {
                // JWT Token mal firmado
                // La idea es que cuando haya cambio de firmas, todos los token con estatus VIGENTE, pasen a REVOCADO
                // Esto provocara que clientes manden tokens firmados con la llave anterior y provoquen este error
                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                        jwtHeader, "INFO", "JWT TOKEN FIRMA INVALIDA, SE PROCEDE A BUSCAR COMO REVOCADO EN BD",
                        1, null, req.getIdCanal());

                // Validamos que el JWT token enviado este en BD como REVOCADO
                accessTokenOBJ = seguridadDAO.obtenerAccessTokenByTokenEstatus(jwtHeader, jwtRevoked);
                if (accessTokenOBJ != null) {
                    // JWT INVALIDO encontrado como REVOCADO en BD, regresamos -402
                    // Registrar log
                    seguridadDAO.registraJwtTokenLog(
                            accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                            accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_REVOKED),
                            ErroresValidaJwtToken.JWT_REVOKED, null, req.getIdCanal()
                    );

                    return RespuestaUtils.asignarError(respuesta,
                            -402,
                            "JWT token revoked");
                } else {
                    //Caso contrario error de JWT INVALIDO MAL FIRMADO, regresamos -403
                    // Registrar log
                    seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                            jwtHeader, "INFO", ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.JWT_REVOKED_NOT_FOUND, jwtHeader),
                            ErroresValidaJwtToken.JWT_REVOKED_NOT_FOUND, null, req.getIdCanal());

                    return RespuestaUtils.asignarError(respuesta,
                            -402,
                            "Invalid JWT token");
                }
            } else {
                // En cualquier otro caso (malformado, etc.) es INVALIDO
                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                        jwtHeader, "ERROR", ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.JWT_INVALID, reason.name()),
                        ErroresValidaJwtToken.JWT_INVALID, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        -403,
                        "Invalid JWT token");
            }

            respuesta = RespuestaUtils.respuestaExito(respuesta, gson.toJson(accessTokenOBJ));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());

            seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", req.getEndpoint(),
                    req.getJwtToken(), "ERROR", e.getMessage(),
                    ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());
        }
        return respuesta;
    }

    public RespuestaDTO validaClaimsJWTInicial(ValidaJWTReq req, AccessTokenOBJ accessTokenOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        SolicitanteOBJ solicitanteOBJ = null;
        try {
            IdentificadorHelper.TipoIdentificador tipo = identificadorHelper.determinarIdentificador(req);

            if (tipo == IdentificadorHelper.TipoIdentificador.TELEFONO) {
                // lógica para teléfono
                solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
                if (solicitanteOBJ == null) {
                    seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                            accessTokenOBJ.getToken(), "ERROR",
                            ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_TELEFONO, req.getTelefono()),
                            ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_TELEFONO, null, req.getIdCanal());

                    return RespuestaUtils.asignarError(respuesta,
                            401,
                            ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_TELEFONO, req.getTelefono()));
                }
            } else if (tipo == IdentificadorHelper.TipoIdentificador.PERSONA_ID) {
                // lógica para persona ID
                solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(req.getPersonaId());
                if (solicitanteOBJ == null) {
                    seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                            accessTokenOBJ.getToken(), "ERROR",
                            ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_PERSONA_ID, req.getPersonaId()),
                            ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_PERSONA_ID, null, req.getIdCanal());

                    return RespuestaUtils.asignarError(respuesta,
                            401,
                            ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.SOLICITANTE_NOT_FOUND_PERSONA_ID, req.getPersonaId()));
                }
            } else if (tipo == IdentificadorHelper.TipoIdentificador.ID_CANAL) {
                return validaClaimsJWTUsuarioExterno(req, accessTokenOBJ);
            } else if (tipo == IdentificadorHelper.TipoIdentificador.NINGUNO) {
                return RespuestaUtils.respuestaExito(respuesta);
            }

            return validaClaimsJWTUsuarioAspPago(req, accessTokenOBJ, solicitanteOBJ);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());

            seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                    accessTokenOBJ.getToken(), "ERROR", e.getMessage(),
                    ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());
        }
        return respuesta;
    }

    public RespuestaDTO validaClaimsJWTUsuarioAspPago(ValidaJWTReq req, AccessTokenOBJ accessTokenOBJ, SolicitanteOBJ solicitanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Claims claims = jwtUtil.obtenerClaims(req.getJwtToken());

            String telefonoDesdeToken = (String) claims.get("telefono");
            String dispositivoIdDesdeToken = (String) claims.get("dispositivoId");

            if (StringUtils.isBlank(telefonoDesdeToken) || StringUtils.isBlank(dispositivoIdDesdeToken)) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.JWT_INVALID, "Claims vacios (telefono y dispositivoId)"),
                        ErroresValidaJwtToken.JWT_INVALID, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        -403,
                        "Invalid JWT token");
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "ERROR", respuesta.getMensaje(), respuesta.getCodigo(), null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        401,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_UNAUTHORIZED));
            }

            if (!telefonoDesdeToken.equals(solicitanteOBJ.getTelefonoCoDi())) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.ERROR_TELEFONO, telefonoDesdeToken, solicitanteOBJ.getTelefonoCoDi()),
                        ErroresValidaJwtToken.ERROR_TELEFONO, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        401,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_UNAUTHORIZED));
            }

            if (regCoDiOBJ.getIdHardware() == null) {
                //primera vez, registrar en bd id hardware

            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoIdDesdeToken) && req.getEndpoint().contains(pathPwdReset)) {
                log.warn("Id dispisitivo diferente al registrado, se dejara pasar por solicitud de cambio de pwd");

                String mensaje = String.format("Generacion de token con ID Hardware diferente para cambio de password: " +
                                "TEL: %s, DISPOSITIVO_ID: %s, PATH: %s",
                        req.getTelefono(), dispositivoIdDesdeToken, req.getEndpoint());

                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "WARN", mensaje,
                        ErroresJwtToken.ID_HARDWARE_RST_PWD, null, req.getIdCanal());

                RespuestaUtils.respuestaExito(respuesta);
            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoIdDesdeToken) && req.getEndpoint().contains(pathLogInV)) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "WARN", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.ACTUALIZA_ID_HARDWARE),
                        ErroresValidaJwtToken.ACTUALIZA_ID_HARDWARE, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaJwtToken.ACTUALIZA_ID_HARDWARE,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.ACTUALIZA_ID_HARDWARE));
            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoIdDesdeToken)) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.ERROR_ID_HARDWARE, dispositivoIdDesdeToken, regCoDiOBJ.getIdHardware()),
                        ErroresValidaJwtToken.ERROR_ID_HARDWARE, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        401,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_UNAUTHORIZED));
            }
        } catch (ExpiredJwtException e) {
            manejarExpiredJwtException(e, accessTokenOBJ, req);
            RespuestaUtils.asignarError(respuesta,
                    -402,
                    "JWT token expired");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());

            seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                    accessTokenOBJ.getToken(), "ERROR", e.getMessage(),
                    ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());
        }
        return respuesta;
    }

    public RespuestaDTO validaClaimsJWTUsuarioExterno(ValidaJWTReq req, AccessTokenOBJ accessTokenOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Claims claims = jwtUtil.obtenerClaims(req.getJwtToken());

            Integer idCanalDesdeToken = (Integer) claims.get("idCanal");
            String tenantIdDesdeToken = (String) claims.get("tenantId");

            if (idCanalDesdeToken == null || StringUtils.isBlank(tenantIdDesdeToken)) {
                seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                        accessTokenOBJ.getToken(), "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.JWT_INVALID, "Claims vacios (idCanal y tenantId)"),
                        ErroresValidaJwtToken.JWT_INVALID, null, req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaJwtToken.JWT_UNAUTHORIZED,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_UNAUTHORIZED));
            }

            if (!req.getIdCanal().equals(idCanalDesdeToken)) {
                seguridadDAO.registraJwtTokenLog(
                        accessTokenOBJ.getId(),
                        "ACCESS",
                        "VALIDATION",
                        req.getEndpoint(),
                        accessTokenOBJ.getToken(),
                        "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.ID_CANAL_NOT_MATCH, String.valueOf(idCanalDesdeToken)),
                        ErroresValidaJwtToken.ID_CANAL_NOT_MATCH,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaJwtToken.ID_CANAL_NOT_MATCH,
                        ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.ID_CANAL_NOT_MATCH));
            }

            //1.- Buscar registro en seguridad.sejwt_tenant_id mediante el id canal
            log.info("Buscando tenantId por idCanal y tenantIdClaims | idCanal={} | tenantId={}", idCanalDesdeToken, tenantIdDesdeToken);
            TenantIdOBJ tenantIdOBJ = seguridadDAO.obtenerTenantIdByIdCanalAndByTenantIdClaims(idCanalDesdeToken, tenantIdDesdeToken);
            if (tenantIdOBJ == null) {
                log.info("No se encontro tenantId para el idCanal={} y tenantIdClaims={}", idCanalDesdeToken, tenantIdDesdeToken);

                seguridadDAO.registraJwtTokenLog(
                        accessTokenOBJ.getId(),
                        "ACCESS",
                        "VALIDATION",
                        req.getEndpoint(),
                        accessTokenOBJ.getToken(),
                        "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.TENANT_ID_NOT_FOUND, String.valueOf(idCanalDesdeToken)),
                        ErroresValidaJwtToken.TENANT_ID_NOT_FOUND,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaJwtToken.TENANT_ID_NOT_FOUND,
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.TENANT_ID_NOT_FOUND, String.valueOf(idCanalDesdeToken)));
            }

            //2.- Validar el que el tenant id este activo
            if (!Boolean.TRUE.equals(tenantIdOBJ.getActivo())) {
                log.info("El tenantId {} esta inactivo para el idCanal {}", idCanalDesdeToken, tenantIdDesdeToken);

                seguridadDAO.registraJwtTokenLog(
                        accessTokenOBJ.getId(),
                        "ACCESS",
                        "VALIDATION",
                        req.getEndpoint(),
                        accessTokenOBJ.getToken(),
                        "ERROR",
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.TENANT_ID_NOT_ACTIVE, String.valueOf(idCanalDesdeToken)),
                        ErroresValidaJwtToken.TENANT_ID_NOT_ACTIVE,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidaJwtToken.TENANT_ID_NOT_ACTIVE,
                        ErroresValidaJwtToken.getMensajeError(ErroresValidaJwtToken.TENANT_ID_NOT_ACTIVE, String.valueOf(idCanalDesdeToken)));
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (ExpiredJwtException e) {
            manejarExpiredJwtException(e, accessTokenOBJ, req);
            RespuestaUtils.asignarError(respuesta,
                    -402,
                    "JWT token expired");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());

            seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                    accessTokenOBJ.getToken(), "ERROR", e.getMessage(),
                    ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());
        }
        return respuesta;
    }

    public RespuestaDTO validaDatosGeneracionTokenUsuarioAspPago(String telefono, String dispositivoId, String path) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //obtener id cliente mediante telefono
            if (telefono == null || !telefono.matches("[0-9]{10}"))
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.FORMATO_TELEFONO,
                        ErroresJwtToken.descError.get(ErroresJwtToken.FORMATO_TELEFONO));

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(telefono);
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.SOLICITANTE_NOT_FOUND,
                        ErroresJwtToken.descError.get(ErroresJwtToken.SOLICITANTE_NOT_FOUND));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            if (regCoDiOBJ.getIdHardware() == null) {
                //primera vez, registrar en bd id hardware

            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoId) && path.contains(pathPwdReset)) {
                log.warn("Id dispisitivo diferente al registrado, se dejara pasar por solicitud de cambio de pwd");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.ID_HARDWARE_RST_PWD,
                        ErroresJwtToken.descError.get(ErroresJwtToken.ID_HARDWARE_RST_PWD));
            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoId) && path.contains(pathLogInV)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.ACTUALIZA_ID_HARDWARE,
                        ErroresJwtToken.descError.get(ErroresJwtToken.ACTUALIZA_ID_HARDWARE));
            } else if (!regCoDiOBJ.getIdHardware().equals(dispositivoId)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.ERROR_ID_HARDWARE,
                        ErroresJwtToken.descError.get(ErroresJwtToken.ERROR_ID_HARDWARE));
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    e.getMessage());
        }
        return respuesta;
    }

    public RespuestaDTO validaClaimsContraRequestRefreshToken(RefreshTokenOBJ refreshTokenOBJ, RefreshTokenReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Claims claims = jwtUtil.obtenerClaims(refreshTokenOBJ.getToken());

            String telefonoEnToken = (String) claims.get("telefono");
            if (!req.getTelefono().equals(telefonoEnToken)) {
                String mensaje = String.format("Intento de renovacion de token con datos distintos a los guardados en los claims: " +
                                "TEL: %s, DISPOSITIVO_ID: %s",
                        req.getTelefono(), req.getDispositivoId());

                seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "REFRESH", "/refresh-token",
                        refreshTokenOBJ.getToken(), "ERROR", mensaje, 401, req.getHeader(), req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.UNAUTHORIZED_GENERIC,
                        ErroresJwtToken.descError.get(ErroresJwtToken.UNAUTHORIZED_GENERIC));
            }

            String dispositivoIdEnToken = (String) claims.get("dispositivoId");
            if (!req.getDispositivoId().equals(dispositivoIdEnToken) && !req.getPath().contains(pathLogInV)) {
                String mensaje = String.format("Intento de renovacion de token con datos distintos a los guardados en los claims: " +
                                "TEL: %s, DISPOSITIVO_ID: %s",
                        req.getTelefono(), req.getDispositivoId());

                seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "REFRESH", "/refresh-token",
                        refreshTokenOBJ.getToken(), "ERROR", mensaje, 401, req.getHeader(), req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.UNAUTHORIZED_GENERIC,
                        ErroresJwtToken.descError.get(ErroresJwtToken.UNAUTHORIZED_GENERIC));
            } else if (!req.getDispositivoId().equals(dispositivoIdEnToken)) {
                seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "REFRESH", "/refresh-token",
                        refreshTokenOBJ.getToken(), "INFO", "VALIDACION DE DISPOSITIVO", -4, req.getHeader(), req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.ACTUALIZA_ID_HARDWARE,
                        ErroresJwtToken.descError.get(ErroresJwtToken.ACTUALIZA_ID_HARDWARE));
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO RespuestaDTO = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(RespuestaDTO,
                    ErroresJwtToken.REGISTRO_CODI_NOT_FOUND,
                    ErroresJwtToken.descError.get(ErroresJwtToken.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(RespuestaDTO,
                    ErroresJwtToken.VERSION_APP,
                    ErroresJwtToken.descError.get(ErroresJwtToken.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(RespuestaDTO);
    }

    private void manejarExpiredJwtException(ExpiredJwtException e, AccessTokenOBJ accessTokenOBJ, ValidaJWTReq req) {
        log.info("Excepcion al obtener los claims del token:");
        errorHandler.handleException(e);
        seguridadDAO.registraJwtTokenLog(accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                accessTokenOBJ.getToken(), "ERROR", e.getMessage(),
                ErroresGenerales.ERROR_INTERNO, null, req.getIdCanal());

        log.info("Se procede a expirar el acces token {}", accessTokenOBJ.getId());
        // JWT EXPIRADO encontrado como VIGENTE en BD procedemos a Expirar en la BD y regresamos -402
        seguridadDAO.expirarAccessToken(accessTokenOBJ.getId());
        // Registrar log
        seguridadDAO.registraJwtTokenLog(
                accessTokenOBJ.getId(), "ACCESS", "VALIDATION", req.getEndpoint(),
                accessTokenOBJ.getToken(), "INFO", ErroresValidaJwtToken.descError.get(ErroresValidaJwtToken.JWT_EXPIRED),
                ErroresValidaJwtToken.JWT_EXPIRED, null, req.getIdCanal());
    }
}