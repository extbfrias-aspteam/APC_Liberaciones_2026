package mx.net.asp.asp_pago_documents.controller;

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
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AttachmentOBJ;
import mx.net.asp.asp_pago_documents.request.GeneraAnexoDetalleBeneficiarioReq;
import mx.net.asp.asp_pago_documents.service.AnexoDetalleBeneficiarioService;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@Tag(name = "Genera Reporte Estado Movimiento")
@RequiredArgsConstructor
public class AnexoDetalleBeneficiarioController {
    private final AnexoDetalleBeneficiarioService beneficiarioService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Generar Reporte Estado Movimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = AttachmentOBJ.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/generaAnexoDetalleBeneficiario", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generaAnexoDetalleBeneficiario(@Valid @RequestBody GeneraAnexoDetalleBeneficiarioReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        Gson gson = new Gson();
        AttachmentOBJ response;
        try {
            respuesta = beneficiarioService.generaAnexoDetalleBeneficiario(req);
            if (respuesta.getCodigo() == 0) {
                response = gson.fromJson(respuesta.getData(), AttachmentOBJ.class);
                log.info("respuesta /generaAnexoDetalleBeneficiario codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /generaAnexoDetalleBeneficiario {}", gson.toJson(respuesta));
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