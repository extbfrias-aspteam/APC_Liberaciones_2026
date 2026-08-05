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
import mx.net.asp.vf_core_api.service.AspPagoDocumentsService;
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
public class AspPagoDocumentsController {

    private final AspPagoDocumentsService aspPagoDocumentsService;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Operation(summary = "Servicio para generar jwt de autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/generarReporteCred", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generarReporteCred(@Valid @RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = aspPagoDocumentsService.generarReporteCred(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para generar jwt de autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/generarCartaAutorizacion", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generarCartaAutorizacion(@Valid @RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = aspPagoDocumentsService.generarCartaAutorizacion(req);
            return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getBody());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(gson.toJson(respuesta));
        }
    }

    @Operation(summary = "Servicio para generar jwt de autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/generarCaratulaCred", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> generarCaratulaCred(@Valid @RequestBody String req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            ServiceResponse serviceResponse = aspPagoDocumentsService.generarCaratulaCred(req);
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
