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
import mx.net.asp.asp_pago_payments.request.ProcesamientoSpeiSimpleReq;
import mx.net.asp.asp_pago_payments.response.ProcesamientoSpeiSimpleResponse;
import mx.net.asp.asp_pago_payments.service.ProcesamientoSpeiSimpleService;
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
@Tag(name = "Envio de dinero mediante ASP Pago")
@RequiredArgsConstructor
public class ProcesamientoSpeiSimpleController {
    private final ProcesamientoSpeiSimpleService procesamientoSpeiSimpleService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Servicio para realizar envios de dinero (Traspasos, SPEI y pago de Crédito)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = ProcesamientoSpeiSimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/procesamientoSpeiSimpleV3", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> procesamientoSpeiSimple(@Valid @RequestBody ProcesamientoSpeiSimpleReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        ProcesamientoSpeiSimpleResponse response;
        Gson gson = new Gson();
        try {
            respuesta = procesamientoSpeiSimpleService.procesamientoSpeiSimpleInicial(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), ProcesamientoSpeiSimpleResponse.class);
                log.info("respuesta /procesamientoSpeiSimpleV3 {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /procesamientoSpeiSimpleV3 {}", gson.toJson(respuesta));
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