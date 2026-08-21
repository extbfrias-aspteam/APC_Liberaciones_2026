package mx.net.asp.asp_pago_onb.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.TarjetaOBJ;
import mx.net.asp.asp_pago_onb.request.RegistroDeCuentaSimplificadaReq;
import mx.net.asp.asp_pago_onb.response.RegistroCuentaSimplificadaResponse;
import mx.net.asp.asp_pago_onb.service.RegistroDeCuentaSimplificadaService;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Registro de Cuenta Simplificada")
@RequiredArgsConstructor
public class RegistroDeCuentaSimplificadaController {
    private final RegistroDeCuentaSimplificadaService registroDeCuentaSimplificadaService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Servicio para dar de alta una cuenta simplificada producto 6")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistroCuentaSimplificadaResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/registroDeCuentaSimplificada", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registroDeCuentaSimplificada(@Valid @RequestBody RegistroDeCuentaSimplificadaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<RegistroCuentaSimplificadaResponse> response;
        Gson gson = new Gson();
        try {
            respuesta = registroDeCuentaSimplificadaService.registroDeCuentaSimplificadaInicial(req);
            if (respuesta.getCodigo() == 0) {
                Type registrosList = new TypeToken<List<RegistroCuentaSimplificadaResponse>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), registrosList);
                log.info("respuesta /registroDeCuentaSimplificada {}", gson.toJson(response));
                // response = gson.fromJson(respuesta.getData(), RegistroCuentaSimplificadaResponse.class);
                // log.info("respuesta /registroDeCuentaSimplificada {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /registroDeCuentaSimplificada {}", gson.toJson(respuesta));
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