package mx.net.asp.asp_pago_management.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.DetalleCuentaDestinoOBJ;
import mx.net.asp.asp_pago_management.service.ObtenerDetalleCuentaDestinoService;
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
@Tag(name = "Favoritos ASP Pago")
@RequiredArgsConstructor
public class ObtenerDetalleCuentaDestinoController {
    private final ObtenerDetalleCuentaDestinoService obtenerDetalleCuentaDestinoService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Consultar catálogo de favoritos asociados a una cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = DetalleCuentaDestinoOBJ.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerDetalleCuentaDestino", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerDetalleCuentaDestino(
            @RequestParam("identificadorCuenta")
            @NotBlank(message = "El campo 'cuenta' es obligatorio.") String identificadorCuenta) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        DetalleCuentaDestinoOBJ response;
        Gson gson = new Gson();
        try {
            respuesta = obtenerDetalleCuentaDestinoService.obtenerDetalle(identificadorCuenta);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), DetalleCuentaDestinoOBJ.class);
                log.info("respuesta /obtenerDetalleCuentaDestino codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /obtenerDetalleCuentaDestino {}", gson.toJson(respuesta));
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

