package mx.net.asp.asp_pago_jwt.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.reponse.JwtTokenResp;
import mx.net.asp.asp_pago_jwt.request.LoginAuthReq;
import mx.net.asp.asp_pago_jwt.request.RefreshTokenReq;
import mx.net.asp.asp_pago_jwt.service.JwtTokenService;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "JWT Token")
@RequiredArgsConstructor
public class JwtTokenController {

    private final JwtTokenService jwtTokenService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Generación access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = JwtTokenResp.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/login-auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginAuth(@Valid @RequestBody LoginAuthReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        JwtTokenResp response;
        Gson gson = new Gson();
        try {
            respuesta = jwtTokenService.loginAuthInicial(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), JwtTokenResp.class);
                log.info("respuesta /login-auth codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /login-auth codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @Operation(summary = "Renovacion de JTW Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = JwtTokenResp.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        JwtTokenResp response;
        Gson gson = new Gson();
        try {
            respuesta = jwtTokenService.refreshTokenInicial(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), JwtTokenResp.class);
                log.info("respuesta /refresh-token:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /refresh-token:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
}