package mx.net.asp.asp_pago_management.controller;

import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.response.ConsultaDomicilioResponse;
import mx.net.asp.asp_pago_management.service.ConsultarCPService;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;

import java.lang.reflect.Type;
import java.util.List;

@Log4j2
@Validated
@RestController
@Tag(name = "Servicios de consulta datos direccion")
@RequiredArgsConstructor
public class ConsultarCPController {

    private final ConsultarCPService consultarCPService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Consultar codigo postal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConsultaCPOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarCP", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultarCP(
            @RequestParam("cp")
            @NotBlank(message = "El campo 'cp' es obligatorio.")
            @Pattern(regexp = "^\\d{5}$", message = "El campo 'cp' debe contener exactamente 5 dígitos numéricos") String cp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = consultarCPService.consultaCP(cp);
            List<ConsultaCPOBJ> response;
            Gson gson = new Gson();
            if (respuesta.getCodigo() == 0) {
                Type consultaCpListType = new TypeToken<List<ConsultaCPOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), consultaCpListType);
                log.info("respuesta /consultarCP codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarCP {}", gson.toJson(respuesta));
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

    @Operation(summary = "Consultar domicilio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConsultaDomicilioResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultaDomicilio", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultaDomicilio(
            @RequestParam("solicitante_id")
            @NotBlank(message = "El campo 'solicitante_id' es obligatorio.") String solicitante_id) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = consultarCPService.consultaDomicilio(solicitante_id);
            Gson gson = new Gson();
            if (respuesta.getCodigo() == 0) {
                ConsultaDomicilioResponse response = gson.fromJson(respuesta.getData(),
                        ConsultaDomicilioResponse.class);
                log.info("respuesta /consultaDomicilio {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultaDomicilio {}", gson.toJson(respuesta));
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