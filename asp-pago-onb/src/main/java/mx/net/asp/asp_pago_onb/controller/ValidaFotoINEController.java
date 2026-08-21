package mx.net.asp.asp_pago_onb.controller;

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
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.RespuestaReconocimientoFacialServiceReqOBJ;
import mx.net.asp.asp_pago_onb.request.ValidaFotoIneReq;
import mx.net.asp.asp_pago_onb.service.ValidaFotoIneService;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
@Tag(name = "Beneficiarios Cuenta ASP Pago")
@RequiredArgsConstructor
public class ValidaFotoINEController {
    private final ValidaFotoIneService validaFotoIneService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Valida foto INE ASP Pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaReconocimientoFacialServiceReqOBJ.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/validaFotoIne", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> validaFotoIne(@Valid @RequestBody ValidaFotoIneReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        Gson gson = new Gson();
        RespuestaReconocimientoFacialServiceReqOBJ response;
        try {
            respuesta = validaFotoIneService.validaFotoIne(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), RespuestaReconocimientoFacialServiceReqOBJ.class);
                log.info("respuesta /validaFotoIne {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /validaFotoIne {}", gson.toJson(respuesta));
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