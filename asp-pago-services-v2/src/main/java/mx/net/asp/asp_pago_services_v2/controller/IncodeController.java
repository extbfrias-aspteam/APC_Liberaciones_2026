package mx.net.asp.asp_pago_services_v2.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.SesionIncodeOBJ;
import mx.net.asp.asp_pago_services_v2.requests.ActualizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeManualReq;
import mx.net.asp.asp_pago_services_v2.requests.FinalizarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.requests.GuardarSesionIncodeReq;
import mx.net.asp.asp_pago_services_v2.service.IncodeService;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Log4j2
@RestController
@Tag(name = "Incode")
@RequiredArgsConstructor
public class IncodeController {

    private final IncodeService incodeService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Consultar sesión Incode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = SesionIncodeOBJ.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @GetMapping(value = "/consultarSesionIncode", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultarSesionIncode(@Valid @RequestParam @NotBlank(message = "El campo 'cuentaah' es obligatorio") String cuentaah) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        SesionIncodeOBJ response = new SesionIncodeOBJ();
        Gson gson = new Gson();
        try {
            respuesta = incodeService.obtenerSesionIncode(cuentaah);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), SesionIncodeOBJ.class);
                log.info("respuesta /consultarSesionIncode {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarSesionIncode {}", gson.toJson(respuesta));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @Validated
    @Operation(summary = "Guarda la sesión de incode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación realizada con éxito, no se devuelve contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/guardarSesionIncode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> guardarSesionIncode(@Valid @RequestBody GuardarSesionIncodeReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = incodeService.guardarSesionIncode(req);
            log.info("respuesta /guardarSesionIncode {}", gson.toJson(respuesta));
            if (respuesta.getCodigo() == 0) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @Validated
    @Operation(summary = "Actualiza la sesión de incode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación realizada con éxito, no se devuelve contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(value = "/actualizarSesionIncode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> actualizarSesionIncode(@Valid @RequestBody ActualizarSesionIncodeReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = incodeService.actualizarSesionIncode(req);
            log.info("respuesta /actualizarSesionIncode {}", gson.toJson(respuesta));
            if (respuesta.getCodigo() == 0) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @Validated
    @Operation(summary = "Finaliza la sesión de incode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación realizada con éxito, no se devuelve contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(value = "/finalizarSesionIncode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> finalizarSesionIncode(@Valid @RequestBody FinalizarSesionIncodeReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = incodeService.finalizarSesionIncode(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /finalizarSesionIncode {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /finalizarSesionIncode {}", gson.toJson(respuesta));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @Validated
    @Operation(summary = "Finaliza la sesión de incode manual")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(value = "/finalizarSesionIncodeManual", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> finalizarSesionIncodeManual(@Valid @RequestBody FinalizarSesionIncodeManualReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = incodeService.finalizarSesionIncodeManual(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /finalizarSesionIncodeManual {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /finalizarSesionIncodeManual {}", gson.toJson(respuesta));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
}