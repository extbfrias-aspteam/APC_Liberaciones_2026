package mx.net.asp.asp_pago_management.controller;

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
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ServiciosTercerosOBJ;
import mx.net.asp.asp_pago_management.service.ServiciosTercerosService;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Servicios de Terceros y Recargas Telefónicas")
@RequiredArgsConstructor
public class ServiciosTercerosController {

    private final ServiciosTercerosService serviciosTercerosService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Consultar catálogo Servicios de Terceros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiciosTercerosOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/listaServicios", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultarListaServicios() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<ServiciosTercerosOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = serviciosTercerosService.obtenerListadoServiciosTerceros();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<ServiciosTercerosOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /listaServicios codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /listaServicios {}", gson.toJson(respuesta));
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
    @Operation(summary = "Consultar catálogo Servicios de Recargas Telefónicas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiciosTercerosOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/listaRecargas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultarListaRecargas() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<ServiciosTercerosOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = serviciosTercerosService.obtenerListadoRecargasTelefonicas();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<ServiciosTercerosOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /listaRecargas codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /listaRecargas {}", gson.toJson(respuesta));
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