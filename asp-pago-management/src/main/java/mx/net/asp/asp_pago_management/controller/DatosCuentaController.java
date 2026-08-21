package mx.net.asp.asp_pago_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.CreditoOBJ;
import mx.net.asp.asp_pago_management.model.CuentaAspPagoOBJ;
import mx.net.asp.asp_pago_management.model.TarjetaOBJ;
import mx.net.asp.asp_pago_management.service.DatosCuentaService;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;

import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Log4j2
@Validated
@RestController
@Tag(name = "Cuenta ASP Pago")
@RequiredArgsConstructor
public class DatosCuentaController {
    private final DatosCuentaService cuentaService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Servicio para obtener el detalle de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = CuentaAspPagoOBJ.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerDetalleCuenta", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerDetalleCuenta(
            @RequestParam("cuenta")
            @NotBlank(message = "El campo 'cuenta' es obligatorio.") String cuenta) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        CuentaAspPagoOBJ response;
        Gson gson = new Gson();
        try {
            respuesta = cuentaService.obtenerDetalleCuenta(cuenta);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), CuentaAspPagoOBJ.class);
				log.info("respuesta /obtenerDetalleCuenta {}", gson.toJson(response));
				return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /obtenerDetalleCuenta codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
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
    
    @Operation(summary = "Servicio para obtener la lista de creditos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreditoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerListaCreditos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerListaCreditos(
            @RequestParam("cuenta")
            @NotBlank(message = "El campo 'cuenta' es obligatorio.") String cuenta) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        List<CreditoOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = cuentaService.obtenerListadoCreditos(cuenta);
            if (respuesta.getCodigo() == 0) {
                Type creditosListType = new TypeToken<List<CreditoOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), creditosListType);

                log.info("respuesta /obtenerListaCreditos {}", gson.toJson(response));
				return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /obtenerListaCreditos codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
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

    @Operation(summary = "Servicio para obtener las tarjetas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TarjetaOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerTarjetas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerTarjetas(
            @RequestParam("cuenta")
            @NotBlank(message = "El campo 'cuenta' es obligatorio.") String cuenta) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        List<TarjetaOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = cuentaService.obtenerTarjetas(cuenta);
            if (respuesta.getCodigo() == 0) {
                Type tarjetasListType = new TypeToken<List<TarjetaOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), tarjetasListType);

                log.info("respuesta /obtenerTarjetas {}", gson.toJson(response));
				return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /obtenerTarjetas codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
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