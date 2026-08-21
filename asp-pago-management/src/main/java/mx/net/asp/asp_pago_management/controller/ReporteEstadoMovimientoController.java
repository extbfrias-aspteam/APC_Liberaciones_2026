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
import mx.net.asp.asp_pago_management.request.ReporteEstadoMovimientoReq;
import mx.net.asp.asp_pago_management.service.ReporteEstadoMovimientoService;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Estado Movimientos")
@RequiredArgsConstructor
public class ReporteEstadoMovimientoController {

    private final ReporteEstadoMovimientoService reporteEstadoMovimientoService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Reporte Estado Movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/generaReporteEstadoMovimientosV2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generaReporteEstadoMovimientosV2(@Valid @RequestBody ReporteEstadoMovimientoReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = reporteEstadoMovimientoService.generaReporteEstadoMovimiento(req);

            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /generaReporteEstadoMovimientosV2 {}", gson.toJson(respuesta));
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /generaReporteEstadoMovimientosV2 {}", gson.toJson(respuesta));
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