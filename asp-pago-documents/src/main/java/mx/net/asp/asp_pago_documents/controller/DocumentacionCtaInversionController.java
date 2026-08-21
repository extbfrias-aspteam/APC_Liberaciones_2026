package mx.net.asp.asp_pago_documents.controller;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AttachmentOBJ;
import mx.net.asp.asp_pago_documents.request.GeneraDocCtaInversionReq;
import mx.net.asp.asp_pago_documents.service.DocumentacionCtaInversionService;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Documentacion Cuenta Inversión")
@RequiredArgsConstructor
public class DocumentacionCtaInversionController {
    private final DocumentacionCtaInversionService documentacionCtaInversionService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Generación documentacion cuanta de inversión ASP Pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/generaDocumentacionCtaInversion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generaDocumentacionCtaSimplificada(@Valid @RequestBody GeneraDocCtaInversionReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<AttachmentOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = documentacionCtaInversionService.generaDocumentacionCtaInversionInicial(req);
            if (respuesta.getCodigo() == 0) {
                Type attachmentListType = new TypeToken<List<AttachmentOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), attachmentListType);
                log.info("respuesta /generaDocumentacionCtaInversion codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /generaDocumentacionCtaInversion {}", gson.toJson(respuesta));
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