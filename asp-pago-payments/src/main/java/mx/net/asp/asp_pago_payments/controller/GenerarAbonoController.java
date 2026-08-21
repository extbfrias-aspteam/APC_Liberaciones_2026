package mx.net.asp.asp_pago_payments.controller;

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
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.request.GenerarAbonoReq;
import mx.net.asp.asp_pago_payments.response.GenerarAbonoResponse;
import mx.net.asp.asp_pago_payments.service.GenerarAbonoService;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Abonos desde EiYu")
@RequiredArgsConstructor
public class GenerarAbonoController {
    private final GenerarAbonoService generarAbonoService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Servicio para recibir abonos desde EiYu)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = GenerarAbonoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = GenerarAbonoResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/generarAbono", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generarAbono(@Valid @RequestBody GenerarAbonoReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        GenerarAbonoResponse response;
        Gson gson = new Gson();
        try {
            respuesta = generarAbonoService.generarAbonoInicial(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), GenerarAbonoResponse.class);
                log.info("respuesta /generarAbono {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                response = gson.fromJson(respuesta.getData(), GenerarAbonoResponse.class);
                log.info("respuesta /generarAbono {}", gson.toJson(response));
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