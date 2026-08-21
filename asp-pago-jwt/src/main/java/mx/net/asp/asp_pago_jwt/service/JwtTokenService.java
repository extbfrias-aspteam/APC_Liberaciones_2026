package mx.net.asp.asp_pago_jwt.service;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.dao.SeguridadDAO;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.model.*;
import mx.net.asp.asp_pago_jwt.reponse.JwtTokenResp;
import mx.net.asp.asp_pago_jwt.request.LoginAuthReq;
import mx.net.asp.asp_pago_jwt.request.RefreshTokenReq;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.JwtUtil;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresJwtToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class JwtTokenService {

    private final ValidacionesJwtService validacionesJwtService;

    private final SeguridadDAO seguridadDAO;

    private final ErrorHandler errorHandler;
    private final JwtUtil jwtUtil;
    private final Gson gson;

    @Value("${path.login.version}")
    private String pathLogInV;

    @Value("${id.canal.asp.pago}")
    private Integer idCanalAspPago;

    public RespuestaDTO loginAuthInicial(LoginAuthReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (idCanalAspPago.equals(req.getIdCanal()))
                respuesta = validaLoginAuthAspPago(req);
            else
                respuesta = validaLoginAuthUsuarioExterno(req);

            if (respuesta.getCodigo() != 0)
                return respuesta;

            return procesarLoginAuth(req);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    public RespuestaDTO validaLoginAuthAspPago(LoginAuthReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = tieneErroresIniciales(req.getTelefono(), req.getDispositivoId(), req.getPath(),
                    "LOGIN", "/login-auth", req.getHeader(), req.getIdCanal(), null);
            if (respuesta.getCodigo() != 0) {

                if (respuesta.getCodigo() == ErroresJwtToken.ACTUALIZA_ID_HARDWARE)
                    return respuesta;

                if (respuesta.getCodigo() == ErroresJwtToken.SOLICITANTE_NOT_FOUND ||
                        respuesta.getCodigo() == ErroresJwtToken.FORMATO_TELEFONO ||
                        respuesta.getCodigo() == ErroresJwtToken.VERSION_APP)
                    return respuesta;

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.JWT_UNAUTHORIZED,
                        ErroresJwtToken.descError.get(ErroresJwtToken.JWT_UNAUTHORIZED));
            }

            if (telefonoEstaBloqueado(req.getTelefono(), "LOGIN", "/login-auth", req.getHeader(), req.getIdCanal(), null)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.JWT_UNAUTHORIZED,
                        ErroresJwtToken.descError.get(ErroresJwtToken.JWT_UNAUTHORIZED));
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validaLoginAuthUsuarioExterno(LoginAuthReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //1.- Buscar registro en seguridad.sejwt_tenant_id mediante el id canal
            TenantIdOBJ tenantIdOBJ = seguridadDAO.obtenerTenantIdByIdCanal(req.getIdCanal());
            if (tenantIdOBJ == null) {
                //Si es null, regresar mensaje de error, Canal invalido o algo asi
                log.error("No se encontro tenant ID para el canal {}", req.getIdCanal());

                seguridadDAO.registraJwtTokenLog(
                        null,
                        "ACCESS",
                        "LOGIN",
                        "/login-auth",
                        null,
                        "ERROR",
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_NOT_FOUND, String.valueOf(req.getIdCanal())),
                        ErroresJwtToken.TENANT_ID_NOT_FOUND,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.TENANT_ID_NOT_FOUND,
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_NOT_FOUND, String.valueOf(req.getIdCanal())));

            }

            //2.- Si no es null, validar el tenant id del req vs el tenantId de la BD
            if (!tenantIdOBJ.getTenantId().equals(req.getTenantId())) {
                log.error("El tenant ID {} para el canal {} no es igual al registrado en base de datos.", req.getTenantId(), req.getIdCanal());

                seguridadDAO.registraJwtTokenLog(
                        null,
                        "ACCESS",
                        "LOGIN",
                        "/login-auth",
                        null,
                        "ERROR",
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_DIFERENT, req.getTenantId(), String.valueOf(req.getIdCanal())),
                        ErroresJwtToken.TENANT_ID_DIFERENT,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.TENANT_ID_DIFERENT,
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_DIFERENT, req.getTenantId(), String.valueOf(req.getIdCanal())));

            }

            //3.- Validar el que el tenant id este activo
            if (!Boolean.TRUE.equals(tenantIdOBJ.getActivo())) {
                //Regresar mensaje de error
                log.info("Tenant ID inactivo para el canal {}.", req.getIdCanal());

                seguridadDAO.registraJwtTokenLog(
                        null,
                        "ACCESS",
                        "LOGIN",
                        "/login-auth",
                        null,
                        "ERROR",
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_NOT_ACTIVE, String.valueOf(req.getIdCanal())),
                        ErroresJwtToken.TENANT_ID_NOT_ACTIVE,
                        null,
                        req.getIdCanal());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.TENANT_ID_NOT_ACTIVE,
                        ErroresJwtToken.getMensajeError(ErroresJwtToken.TENANT_ID_NOT_ACTIVE, String.valueOf(req.getIdCanal())));
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarLoginAuth(LoginAuthReq req) {
        ClaimsTokenOBJ claimsTokenOBJ = new ClaimsTokenOBJ();
        claimsTokenOBJ.setTelefono(req.getTelefono());
        claimsTokenOBJ.setTenantId(req.getTenantId());
        claimsTokenOBJ.setDispositivoId(req.getDispositivoId());
        claimsTokenOBJ.setIdCanal(req.getIdCanal());
        HeaderWS header = req.getHeader();
        Optional<RefreshTokenOBJ> refreshOpt;

        if (idCanalAspPago.equals(req.getIdCanal())) {
            refreshOpt = seguridadDAO.obtenerRefreshTokenVigentePorTelefonoDispositivo(claimsTokenOBJ.getTelefono(), claimsTokenOBJ.getDispositivoId());
        } else {
            //caso para canales externos
            refreshOpt = seguridadDAO.obtenerRefreshTokenVigentePorTenantIdCanalId(claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal());
        }

        if (refreshOpt.isPresent()) {
            return procesarLoginAuthConRefreshToken(refreshOpt.get(), claimsTokenOBJ, header);
        }
        return generarTokensDesdeCero(claimsTokenOBJ, header);
    }

    private RespuestaDTO procesarLoginAuthConRefreshToken(RefreshTokenOBJ refreshTokenOBJ, ClaimsTokenOBJ claimsTokenOBJ, HeaderWS header) {
        if (!jwtUtil.tokenEsValido(refreshTokenOBJ.getToken())) {
            seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "LOGIN", "/login-auth",
                    refreshTokenOBJ.getToken(), "INFO", "TOKEN EXPIRADO POR JWT ENCONTRADO COMO VIGENTE, SE EXPIRA EN BD.",
                    ErroresJwtToken.JWT_REFRESH_NOT_VIG, header, claimsTokenOBJ.getIdCanal());

            seguridadDAO.expirarRefreshToken(refreshTokenOBJ.getId());

            return generarTokensDesdeCero(claimsTokenOBJ, header);
        }

        Optional<AccessTokenOBJ> accessOpt = seguridadDAO.obtenerAccessTokenVigentePorRefreshId(refreshTokenOBJ.getId());

        if (accessOpt.isPresent() && jwtUtil.tokenEsValido(accessOpt.get().getToken())) {
            log.info("AMBOS TOKEN VIGENTES");
            seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "LOGIN", "/login-auth",
                    refreshTokenOBJ.getToken(), "INFO", "TOKEN VIGENTE REUTILIZADO", 0, header, claimsTokenOBJ.getIdCanal());
            seguridadDAO.registraJwtTokenLog(accessOpt.get().getId(), "ACCESS", "LOGIN", "/login-auth",
                    accessOpt.get().getToken(), "INFO", "TOKEN VIGENTE REUTILIZADO", 0, header, claimsTokenOBJ.getIdCanal());
            //Retornamos refresh y access token ya que ambos siguen vigentes
            return construirRespuestaExitosa(accessOpt.get().getToken(), refreshTokenOBJ.getToken(), jwtUtil.segundosRestantes(accessOpt.get().getToken()), claimsTokenOBJ.getIdCanal());
        } else {
            return renovarAccessToken(refreshTokenOBJ, accessOpt, claimsTokenOBJ, header, "/login-auth", "LOGIN");
        }
    }

    public RespuestaDTO refreshTokenInicial(RefreshTokenReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = tieneErroresIniciales(req.getTelefono(), req.getDispositivoId(), req.getPath(),
                    "REFRESH", "/refresh-token", req.getHeader(), req.getIdCanal(), req.getRefresh_token());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.JWT_UNAUTHORIZED,
                        ErroresJwtToken.descError.get(ErroresJwtToken.JWT_UNAUTHORIZED));
            }

            if (telefonoEstaBloqueado(req.getTelefono(), "REFRESH", "/refresh-token", req.getHeader(), req.getIdCanal(), req.getRefresh_token())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.JWT_UNAUTHORIZED,
                        ErroresJwtToken.descError.get(ErroresJwtToken.JWT_UNAUTHORIZED));
            }

            return procesarRefreshToken(req);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private RespuestaDTO procesarRefreshToken(RefreshTokenReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        ClaimsTokenOBJ claimsTokenOBJ = new ClaimsTokenOBJ();
        claimsTokenOBJ.setTelefono(req.getTelefono());
        claimsTokenOBJ.setTenantId(req.getTenantId());
        claimsTokenOBJ.setDispositivoId(req.getDispositivoId());
        claimsTokenOBJ.setIdCanal(req.getIdCanal());
        HeaderWS header = req.getHeader();

        //Obtener refresh token vigente mediante la cadena refresh token
        Optional<RefreshTokenOBJ> refreshOpt = seguridadDAO.obtenerRefreshTokenVigentePorToken(req.getRefresh_token());
        if (refreshOpt.isPresent()) {
            //Validamos que el refresh token siga  vigente mediante JWT
            RefreshTokenOBJ refreshTokenOBJ = refreshOpt.get();
            if (!jwtUtil.tokenEsValido(refreshTokenOBJ.getToken())) {
                seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "REFRESH", "/refresh-token",
                        refreshTokenOBJ.getToken(), "INFO", "TOKEN EXPIRADO POR JWT ENCONTRADO COMO VIGENTE, SE EXPIRA EN BD.",
                        ErroresJwtToken.JWT_REFRESH_NOT_VIG, header, claimsTokenOBJ.getIdCanal());

                seguridadDAO.expirarRefreshToken(refreshTokenOBJ.getId());

                return RespuestaUtils.asignarError(respuesta,
                        ErroresJwtToken.JWT_REFRESH_NOT_VIG,
                        ErroresJwtToken.descError.get(ErroresJwtToken.JWT_REFRESH_NOT_VIG));
            }

            //Validaciones de claims vs request
            respuesta = validacionesJwtService.validaClaimsContraRequestRefreshToken(refreshTokenOBJ, req);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            //Validar si el access token vinculado al refresh token esta vigente en BD
            Optional<AccessTokenOBJ> accessOpt = seguridadDAO.obtenerAccessTokenVigentePorRefreshId(refreshTokenOBJ.getId());
            //Si encontro token es porque en BD sigue vigente, validamos con JWT la vigencia
            if (accessOpt.isPresent() && jwtUtil.tokenEsValido(accessOpt.get().getToken())) {
                log.info("AMBOS TOKEN VIGENTES");
                seguridadDAO.registraJwtTokenLog(refreshTokenOBJ.getId(), "REFRESH", "REFRESH", "/refresh-token",
                        refreshTokenOBJ.getToken(), "OK", "TOKEN VIGENTE REUTILIZADO", 0, req.getHeader(), req.getIdCanal());
                seguridadDAO.registraJwtTokenLog(accessOpt.get().getId(), "ACCESS", "REFRESH", "/refresh-token",
                        accessOpt.get().getToken(), "OK", "TOKEN VIGENTE REUTILIZADO", 0, req.getHeader(), req.getIdCanal());
                //Retornamos refresh y access token ya que ambos siguen vigentes
                return construirRespuestaExitosa(accessOpt.get().getToken(), refreshTokenOBJ.getToken(), jwtUtil.segundosRestantes(accessOpt.get().getToken()), claimsTokenOBJ.getIdCanal());
            } else if (accessOpt.isPresent()) {
                //El access token ya no esta vigente mediante JWT pero si mediante BD
                return renovarAccessToken(refreshTokenOBJ, accessOpt, claimsTokenOBJ, header, "/refresh-token", "REFRESH");
            }
            Optional<AccessTokenOBJ> accessOptEmpty = Optional.empty();
            //Aqui llega si no encontro acces token vigente en bd
            return renovarAccessToken(refreshTokenOBJ, accessOptEmpty, claimsTokenOBJ, header, "/refresh-token", "REFRESH");
        } else {
            seguridadDAO.registraJwtTokenLog(null, "REFRESH", "REFRESH", "/refresh-token",
                    req.getRefresh_token(), "INFO", "TOKEN NO ENCONTRADO COMO VIGENTE EN BD.",
                    ErroresJwtToken.JWT_REFRESH_VIG_NOT_FOUND, header, claimsTokenOBJ.getIdCanal());

            //retornar mensaje de error de que el refresh token no esta vigente en BD
            return RespuestaUtils.asignarError(respuesta,
                    ErroresJwtToken.JWT_REFRESH_NOT_VIG,
                    ErroresJwtToken.descError.get(ErroresJwtToken.JWT_REFRESH_NOT_VIG));
        }
    }

    private RespuestaDTO renovarAccessToken(RefreshTokenOBJ refresh, Optional<AccessTokenOBJ> access, ClaimsTokenOBJ claimsTokenOBJ, HeaderWS header, String servicio, String tipoOp) {
        if (access.isPresent()) {
            seguridadDAO.registraJwtTokenLog(access.get().getId(), "ACCESS", tipoOp, servicio,
                    access.get().getToken(), "INFO", "TOKEN EXPIRADO POR JWT ENCONTRADO COMO VIGENTE, SE EXPIRA EN BD.",
                    ErroresJwtToken.JWT_REFRESH_NOT_VIG, header, claimsTokenOBJ.getIdCanal());

            seguridadDAO.expirarAccessToken(access.get().getId());
        }

        String nuevoAccess = "";
        if (idCanalAspPago.equals(claimsTokenOBJ.getIdCanal())) {
            nuevoAccess = jwtUtil.generarAccessTokenAspPago(claimsTokenOBJ.getTelefono(), claimsTokenOBJ.getDispositivoId(), claimsTokenOBJ.getIdCanal());
        } else
            nuevoAccess = jwtUtil.generarAccessTokenCanalExterno(claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal());

        Claims accessClaims = jwtUtil.obtenerClaims(nuevoAccess);
        Instant expInstant = accessClaims.getExpiration().toInstant();
        Long accessId = seguridadDAO.registraAccessToken(nuevoAccess, refresh.getId(), claimsTokenOBJ.getTelefono(),
                claimsTokenOBJ.getDispositivoId(), claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal(), expInstant);

        seguridadDAO.registraJwtTokenLog(refresh.getId(), "REFRESH", tipoOp, servicio,
                refresh.getToken(), "OK", "TOKEN USADO PARA RENOVAR ACCESS", 0, header, claimsTokenOBJ.getIdCanal());

        seguridadDAO.registraJwtTokenLog(accessId, "ACCESS", tipoOp, servicio,
                nuevoAccess, "OK", "NUEVO TOKEN GENERADO", 0, header, claimsTokenOBJ.getIdCanal());

        return construirRespuestaExitosa(nuevoAccess, refresh.getToken(), jwtUtil.segundosRestantes(nuevoAccess), claimsTokenOBJ.getIdCanal());
    }

    private RespuestaDTO generarTokensDesdeCero(ClaimsTokenOBJ claimsTokenOBJ, HeaderWS header) {
        String nuevoRefresh = "";
        String nuevoAccess = "";
        if (idCanalAspPago.equals(claimsTokenOBJ.getIdCanal())) {
            nuevoRefresh = jwtUtil.generarRefreshTokenAspPago(claimsTokenOBJ.getTelefono(), claimsTokenOBJ.getDispositivoId(), claimsTokenOBJ.getIdCanal());
        } else
            nuevoRefresh = jwtUtil.generarRefreshTokenCanalExterno(claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal());

        Claims refreshClaims = jwtUtil.obtenerClaims(nuevoRefresh);
        Instant expRefresh = refreshClaims.getExpiration().toInstant();
        Long refreshId = seguridadDAO.registraRefreshToken(nuevoRefresh, claimsTokenOBJ.getTelefono(), claimsTokenOBJ.getDispositivoId(),
                claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal(), expRefresh);

        seguridadDAO.registraJwtTokenLog(refreshId, "REFRESH", "LOGIN", "/login-auth",
                nuevoRefresh, "OK", "NUEVO TOKEN GENERADO", 0, header, claimsTokenOBJ.getIdCanal());

        if (idCanalAspPago.equals(claimsTokenOBJ.getIdCanal())) {
            nuevoAccess = jwtUtil.generarAccessTokenAspPago(claimsTokenOBJ.getTelefono(), claimsTokenOBJ.getDispositivoId(), claimsTokenOBJ.getIdCanal());
        } else
            nuevoAccess = jwtUtil.generarAccessTokenCanalExterno(claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal());

        Claims accessClaims = jwtUtil.obtenerClaims(nuevoAccess);
        Instant expAccess = accessClaims.getExpiration().toInstant();
        Long accessId = seguridadDAO.registraAccessToken(nuevoAccess, refreshId, claimsTokenOBJ.getTelefono(),
                claimsTokenOBJ.getDispositivoId(), claimsTokenOBJ.getTenantId(), claimsTokenOBJ.getIdCanal(), expAccess);

        seguridadDAO.registraJwtTokenLog(accessId, "ACCESS", "LOGIN", "/login-auth",
                nuevoAccess, "OK", "NUEVO TOKEN GENERADO", 0, header, claimsTokenOBJ.getIdCanal());

        return construirRespuestaExitosa(nuevoAccess, nuevoRefresh, jwtUtil.segundosRestantes(nuevoAccess), claimsTokenOBJ.getIdCanal());
    }

    private RespuestaDTO tieneErroresIniciales(String telefono, String dispositivoId, String path,
                                               String tipoOp, String servicio, HeaderWS headerWS, Integer idCanal, String token) {
        RespuestaDTO respuesta = validacionesJwtService.validaDatosGeneracionTokenUsuarioAspPago(telefono, dispositivoId, path);
        if (respuesta.getCodigo() != 0) {
            String tipoToken = tipoOp == "LOGIN" ? "ACCESS" : "REFRESH";
            String mensaje;
            String resultado = "ERROR";

            if (respuesta.getCodigo() == ErroresJwtToken.ACTUALIZA_ID_HARDWARE ||
                    respuesta.getCodigo() == ErroresJwtToken.ID_HARDWARE_RST_PWD)
                resultado = "WARN";

            if (respuesta.getCodigo() == ErroresJwtToken.ID_HARDWARE_RST_PWD) {
                mensaje = String.format("Generacion de token con ID Hardware diferente para cambio de password: " +
                                "TEL: %s, DISPOSITIVO_ID: %s, PATH: %s",
                        telefono, dispositivoId, path);

                seguridadDAO.registraJwtTokenLog(null, tipoToken, tipoOp, servicio,
                        token, resultado, mensaje, respuesta.getCodigo(), headerWS, idCanal);

                RespuestaUtils.respuestaExito(respuesta);
            } else {
                mensaje = respuesta.getMensaje() +
                        String.format("|Intento de generacion de token con datos invalidos: TEL: %s, DISPOSITIVO_ID: %s",
                                telefono, dispositivoId);
                seguridadDAO.registraJwtTokenLog(null, tipoToken, tipoOp, servicio,
                        token, resultado, mensaje, respuesta.getCodigo(), headerWS, idCanal);
            }
        }
        return respuesta;
    }

    private boolean telefonoEstaBloqueado(String telefono, String tipoOp, String servicio, HeaderWS
            headerWS, Integer idCanal, String token) {
        if (seguridadDAO.validaTelefonoEstaBloqueado(telefono)) {
            String tipoToken = tipoOp == "LOGIN" ? "ACCESS" : "REFRESH";
            String mensaje = "Intento de generación de token con teléfono bloqueado: " + telefono;

            seguridadDAO.registraJwtTokenLog(null, tipoToken, tipoOp, servicio,
                    token, "BLOQUEADO", mensaje, 403, headerWS, idCanal);

            return true;
        }
        return false;
    }

    private RespuestaDTO construirRespuestaExitosa(String accessToken, String refreshToken, long expiresIn, Integer
            idCanal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        JwtTokenResp response = new JwtTokenResp();
        response.setAccess_token(accessToken);
        response.setRefresh_token(refreshToken);
        response.setExpires_in(String.valueOf(expiresIn));
        RespuestaUtils.respuestaExito(respuesta, gson.toJson(response));
        return respuesta;
    }
}
