package mx.net.asp.asp_pago_management.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.response.ValidaCurpNubResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.service.ValidacionNubariumService;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;

@Log4j2
@Validated
@RestController
@Tag(name = "Servicios que realiza validaciones a nubarium")
@RequiredArgsConstructor
public class ValidacionNubariumController {

	private final ValidacionNubariumService validacionNubariumService;
	private final ErrorHandler errorHandler;

	@Operation(summary = "Validacion curp Nubarium")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = ValidaCurpNubResponse.class))),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor") })
	@GetMapping(value = "/validaCurpNub", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> validaCurpNub(
			@RequestParam("curp")
			@NotBlank(message = "El campo 'curp' es obligatorio.")
			@Size(min = 18, max = 18, message = "El campo 'curp' debe tener 18 caracteres.") String curp) {
		RespuestaDTO respuesta = new RespuestaDTO();
		try {
			respuesta = validacionNubariumService.validaCurpNub(curp);
			Gson gson = new Gson();
			if (respuesta.getCodigo() == 0) {
				ValidaCurpNubResponse response = gson.fromJson(respuesta.getData(), ValidaCurpNubResponse.class);
				log.info("respuesta /validaCurpNub {}", gson.toJson(response));
				return ResponseEntity.ok(response);
			} else {
				log.info("respuesta /validaCurpNub {}", gson.toJson(respuesta));
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
