package mx.net.asp.asp_pago_management.controller;

import com.google.gson.Gson;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.response.ObtenerSaldoAhorroSAV2Response;
import mx.net.asp.asp_pago_management.service.CosultaSaldoAhorroService;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;

@Log4j2
@Validated
@RestController
@Tag(name = "Consulta de saldo ahorro")
@RequiredArgsConstructor
public class ObtenerSaldoAhorroV2Controller {

	private final CosultaSaldoAhorroService cosultaSaldoAhorroService;
    private final ErrorHandler errorHandler;

	@Operation(summary = "Servicio para cconsultar el saldo de la cuenta")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = ObtenerSaldoAhorroSAV2Response.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@GetMapping(value = "/obtenerSaldoAhorroSAV2", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> obtenerSaldoAhorroSAV2(
			@RequestParam("cuenta")
			@NotBlank(message = "El campo 'cuenta' es obligatorio.") String cuenta) {
		// Declaracion de variable para la respuesta
		RespuestaDTO respuesta = new RespuestaDTO();
		// Declaracion de variable para la respuesta 200
		ObtenerSaldoAhorroSAV2Response response;
		Gson gson = new Gson();
		try {
			respuesta = cosultaSaldoAhorroService.obtenerSaldoAhorro(cuenta);
			if (respuesta.getCodigo() == 0) {
				response = gson.fromJson(respuesta.getData(), ObtenerSaldoAhorroSAV2Response.class);
				log.info("respuesta /obtenerSaldoAhorroSAV2 {}", gson.toJson(response));
				return ResponseEntity.ok(response);
			} else {
				log.info("respuesta /obtenerSaldoAhorroSAV2 {}", gson.toJson(respuesta));
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
