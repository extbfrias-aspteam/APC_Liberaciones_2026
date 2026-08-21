package mx.net.asp.asp_pago_access.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.PropertiesInicialesOBJ;
import mx.net.asp.asp_pago_access.service.PropertiesInicialesService;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Datos Iniciales")
@RequiredArgsConstructor
public class PropertiesInicialesController {
    private final PropertiesInicialesService propertiesInicialesService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Obtener datos iniciales ASP Pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PropertiesInicialesOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerDatosInicialesV", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerDatosInicialesV() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<PropertiesInicialesOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = propertiesInicialesService.obtenerListadoPropertiesIniciales();
            if (respuesta.getCodigo() == 0) {
                Type propertiesListType = new TypeToken<List<PropertiesInicialesOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), propertiesListType);
                log.info("respuesta /obtenerDatosInicialesV: codigo: {}", respuesta.getCodigo());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /obtenerDatosInicialesV {}", gson.toJson(respuesta));
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