package mx.net.asp.asp_pago_services_v2.controller;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.CatalogoOBJ;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
import mx.net.asp.asp_pago_services_v2.service.PLDService;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Catálogos PLD")
@RequiredArgsConstructor
public class PLDController {

    private final PLDService pldService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Consultar catálogo de ingresos mensuales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarIngresosMensuales")
    public ResponseEntity<Object> consultarIngresosMensuales() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerIngresosMensuales();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarIngresosMensuales {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarIngresosMensuales {}", gson.toJson(respuesta));
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
    
    @Operation(summary = "Consultar catálogo de ahorros mensuales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarAhorrosMensuales")
    public ResponseEntity<Object> consultarAhorrosMensuales() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerAhorrosMensuales();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarAhorrosMensuales {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarAhorrosMensuales {}", gson.toJson(respuesta));
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
    
    @Operation(summary = "Consultar catálogo de ocupaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarOcupaciones")
    public ResponseEntity<Object> consultarOcupaciones() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerOcupaciones();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarOcupaciones {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarOcupaciones {}", gson.toJson(respuesta));
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
    
    @Operation(summary = "Consultar catálogo frecuencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarFrecuencia")
    public ResponseEntity<Object> consultarFrecuencia() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerFrecuencia();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarFrecuencia {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarFrecuencia {}", gson.toJson(respuesta));
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

    @Operation(summary = "Consultar catálogo de objetivos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarObjetivos")
    public ResponseEntity<Object> consultarObjetivos() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerObjetivos();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarObjetivos {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarObjetivos {}", gson.toJson(respuesta));
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

    @Operation(summary = "Consultar catálogo de actividades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/consultarActividades")
    public ResponseEntity<Object> consultarActividades() {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerActividades();
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarActividades {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarActividades {}", gson.toJson(respuesta));
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

    @Operation(summary = "Consultar catálogo de giros por actividad id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CatalogoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @GetMapping(value = "/consultarGirosByActividadId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> consultarGirosByActividadId(@Valid @RequestParam("actividadId") @NotNull(message = "El campo 'actividadId' es obligatorio") @Min(value = 1, message = "El campo 'actividadId' no debe ser 0") Integer actividadId) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<CatalogoOBJ> response = new ArrayList<CatalogoOBJ>();
        Gson gson = new Gson();
        try {
            respuesta = pldService.obtenerGirosByActividadId(actividadId);
            if (respuesta.getCodigo() == 0) {
                Type catalogoListType = new TypeToken<List<CatalogoOBJ>>(){}.getType();
                response = gson.fromJson(respuesta.getData(), catalogoListType);
                log.info("respuesta /consultarGirosByActividadId {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /consultarGirosByActividadId {}", gson.toJson(respuesta));
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
    @Operation(summary = "Servicio para guardar el formulario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operación realizada con éxito, no se devuelve contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/guardarFormulario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> guardarFormulario(@Valid @RequestBody SubirNivelCuentaRemotaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        try {
            respuesta = pldService.guardarFormularioPLD(req);
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
}