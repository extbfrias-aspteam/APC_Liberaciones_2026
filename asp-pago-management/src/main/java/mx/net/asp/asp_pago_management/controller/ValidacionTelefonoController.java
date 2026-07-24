package mx.net.asp.asp_pago_management.controller;

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
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.EnviarCodigoReq;
import mx.net.asp.asp_pago_management.request.ValidaTelefonoReq;
import mx.net.asp.asp_pago_management.service.ValidacionTelefonoService;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@RestController
@Tag(name = "Servicios para la validación del teléfono")
@RequiredArgsConstructor
public class ValidacionTelefonoController {

    private final ValidacionTelefonoService validacionTelefonoService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Servicio para enviar codigo por SMS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/enviarCodigo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> enviarCodigo(@Valid @RequestBody EnviarCodigoReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = validacionTelefonoService.enviarCodigoTelefono(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /enviarCodigo {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /enviarCodigo {}", gson.toJson(respuesta));
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

    @Operation(summary = "Servicio para reenviar codigo por correo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/reenviarCodigoPorCorreo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reenviarCodigoPorCorreo(@Valid @RequestBody EnviarCodigoReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = validacionTelefonoService.reenviarCodigoCorreo(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /enviarCodigo {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /enviarCodigo {}", gson.toJson(respuesta));
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

    @Operation(summary = "Servicio para validar telefono mediante token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/validaTelefono", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validaTelefono(@Valid @RequestBody ValidaTelefonoReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = validacionTelefonoService.validarTelefono(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /validaTelefono {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /validaTelefono {}", gson.toJson(respuesta));
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