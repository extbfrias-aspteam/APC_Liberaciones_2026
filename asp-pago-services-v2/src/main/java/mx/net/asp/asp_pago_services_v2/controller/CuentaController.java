package mx.net.asp.asp_pago_services_v2.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
import mx.net.asp.asp_pago_services_v2.requests.ValidarCurpCuentaReq;
import mx.net.asp.asp_pago_services_v2.service.CuentaService;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Servicios para la Cuenta Facil")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final ErrorHandler errorHandler;

    @Validated
    @Operation(summary = "Servicio para subir la CUENTA FACIL a N4 (CUENTA REMOTA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación realizada con éxito, no se devuelve contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/subirNivelCuentaRemota", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> subirNivelCuentaRemota(@Valid @RequestBody SubirNivelCuentaRemotaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = cuentaService.subirNivelCuentaRemota(req);
            log.info("respuesta /subirNivelCuentaRemota {}", gson.toJson(respuesta));
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
    @Operation(summary = "Servicio para validar si el curp enviado es el curp que tiene asociado la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RespuestaDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/validaCurpCuenta", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validaCurpCuenta(@Valid @RequestBody ValidarCurpCuentaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = cuentaService.validarCurpCuenta(req);
            log.info("respuesta /validaCurpCuenta {}", gson.toJson(respuesta));
            if (respuesta.getCodigo() == 0) {
                return ResponseEntity.ok(respuesta);
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
}
