package mx.net.asp.vf_core_api.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.request.ObtenerTokenReq;
import mx.net.asp.vf_core_api.request.ProcesaReq;
import mx.net.asp.vf_core_api.service.VFCoreVentanillaService;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class VFCoreVentanillaController {

    private final VFCoreVentanillaService vfCoreVentanillaService;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Operation(summary = "Servicio multifuncion para procesos de Ventanilla Facil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/procesa", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> procesa(@Valid @RequestBody ProcesaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.procesa(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/obtenerToken", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> obtenerToken(@Valid @RequestBody ObtenerTokenReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.obtenerToken(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/bloqueaComisionistaEmpleado", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> bloqueaComisionistaEmpleado(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.bloqueaComisionistaEmpleado(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/buscarComisionistaReferencia", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> buscarComisionistaReferencia(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.buscarComisionistaReferencia(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/consultarRetenciones", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> consultarRetenciones(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.consultarRetenciones(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/consultaSaldoAhorro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> consultaSaldoAhorro(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.consultaSaldoAhorro(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/consultaSaldoCredito", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> consultaSaldoCredito(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.consultaSaldoCredito(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/depositoAhorro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> depositoAhorro(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.depositoAhorro(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para obtner token para servicio /procesa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/enviarNotificaciones", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> enviarNotificaciones(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.enviarNotificaciones(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "/retiroAhorro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/retiroAhorro", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> retiroAhorro(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.retiroAhorro(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "/pagoCredito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/pagoCredito", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> pagoCredito(@RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = vfCoreVentanillaService.pagoCredito(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }
}
