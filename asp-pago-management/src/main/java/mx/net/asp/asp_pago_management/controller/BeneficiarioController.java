package mx.net.asp.asp_pago_management.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.AltaBeneficiarioReq;
import mx.net.asp.asp_pago_management.service.BeneficiarioService;
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
@Tag(name = "Beneficiarios ASP Pago")
@RequiredArgsConstructor
public class BeneficiarioController {
    private final BeneficiarioService beneficiarioService;
    private final ErrorHandler errorHandler;
    private final String logInfoMessage = "respuesta /altaBeneficiarioDetalle {}";

    @Operation(summary = "Servicio para registrar el detalle del beneficiario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/altaBeneficiarioDetalle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> altaBeneficiarioDetalle(@Valid @RequestBody AltaBeneficiarioReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = beneficiarioService.altaBeneficiarioDetalle(req);
            if (respuesta.getCodigo() == 0) {
                log.info(logInfoMessage, gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info(logInfoMessage, gson.toJson(respuesta));
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

    @Operation(summary = "Servicio para validar si se lleno el detalle del beneficiario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/validaBeneficiarioDetalle", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validaBeneficiarioDetalle(
            @RequestParam("cuenta")
            @NotBlank(message = "El campo 'cuenta' es obligatorio.") String cuenta) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = beneficiarioService.validaBeneficiarioDetalle(cuenta);
            if (respuesta.getCodigo() == 0) {
                log.info(logInfoMessage, gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info(logInfoMessage, gson.toJson(respuesta));
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