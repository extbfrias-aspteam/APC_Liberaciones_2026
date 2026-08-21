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
import mx.net.asp.asp_pago_documents.request.GeneraDocCtaSimplificadaReq;
import mx.net.asp.asp_pago_documents.request.RegistroImagenesCtaSimplificadaReq;
import mx.net.asp.asp_pago_documents.service.DocumentacionCtaSimplificadaService;
import mx.net.asp.asp_pago_documents.service.RegistroImagenesCtaSimplificadaService;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "Documentacion Cuenta Simplificada")
@RequiredArgsConstructor
public class DocumentacionCtaSimplificadaController {
    private final DocumentacionCtaSimplificadaService documentacionCtaSimplificadaService;
    private final RegistroImagenesCtaSimplificadaService registroImagenesCtaSimplificadaService;
    private final ErrorHandler errorHandler;

    @Operation(summary = "Generación documentacion cuanta de ahorro simplificada ASP Pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentOBJ.class)))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/generaDocumentacionCtaSimplificada", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> generaDocumentacionCtaSimplificada(@Valid @RequestBody GeneraDocCtaSimplificadaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<AttachmentOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = documentacionCtaSimplificadaService.generaDocumentacionCtaSimplificada(req);
            if (respuesta.getCodigo() == 0) {
                Type attachmentListType = new TypeToken<List<AttachmentOBJ>>() {
                }.getType();
                response = gson.fromJson(respuesta.getData(), attachmentListType);
                log.info("respuesta /generaDocumentacionCtaSimplificada codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /generaDocumentacionCtaSimplificada {}", gson.toJson(respuesta));
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

    @Operation(summary = "Registro de imagenes INE ASP Pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content(schema = @Schema(implementation = RespuestaDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @Validated
    @PostMapping(value = "/registroImagenesCuentaAhorroSimplificada", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registroImagenesCuentaAhorroSimplificada(@Valid @RequestBody RegistroImagenesCtaSimplificadaReq req) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        List<AttachmentOBJ> response;
        Gson gson = new Gson();
        try {
            respuesta = registroImagenesCtaSimplificadaService.registaImagenesCtaSimplificada(req);
            if (respuesta.getCodigo() == 0) {
                log.info("respuesta /registroImagenesCuentaAhorroSimplificada codigo:: {} mensaje:: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return ResponseEntity.ok(respuesta);
            } else {
                log.info("respuesta /registroImagenesCuentaAhorroSimplificada {}", gson.toJson(respuesta));
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