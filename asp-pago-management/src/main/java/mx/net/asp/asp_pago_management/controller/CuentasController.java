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
import mx.net.asp.asp_pago_management.dto.CuentaDTO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.CreditoOBJ;
import mx.net.asp.asp_pago_management.service.CuentasService;
import mx.net.asp.asp_pago_management.utilerias.StringFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@Validated
@RestController
@Tag(name = "Cuenta ASP Pago")
@RequiredArgsConstructor
public class CuentasController {
    private final CuentasService cuentasService;
    private final StringFormatUtils stringFormatUtils;

    @Operation(summary = "Servicio para obtener la lista de creditos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreditoOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "/obtenerCuentasVigentesCliente", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> obtenerCuentasVigentesCliente(
            @RequestParam("personaId")
            @NotBlank(message = "El campo 'personaId' es obligatorio.") String personaId) {
        List<CuentaDTO>response = cuentasService.obtenerCuentasCliente(personaId);
        //mapeamos la respuesta final
        log.info("response /obtenerCuentasVigentesCliente: {}", stringFormatUtils.toJsonSafe(response));
        return ResponseEntity.ok(response);
    }
}