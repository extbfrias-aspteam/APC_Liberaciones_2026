package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.ws.asp.MSDocCtaSimplifacada;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.AttachmentOBJ;
import mx.net.asp.asp_pago_onb.model.AttachmentStringOBJ;
import mx.net.asp.asp_pago_onb.ws.asp.MSEnviarEmail;
import mx.net.asp.asp_pago_onb.utilerias.CorreoUtils;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@AllArgsConstructor
public class DocumentacionCtaSimplificadaService {

    private final MSDocCtaSimplifacada msDocCtaSimplifacada;
    private final MSEnviarEmail msEnviarEmail;
    private final Gson gson;
    private final Environment env;
    private final CorreoUtils correoUtils;
    private final ErrorHandler errorHandler;

    @Async
    public CompletableFuture<RespuestaDTO> enviaDocumentacionCtaSimplificada(List<AttachmentOBJ> attachmentsList, String cuentaah, String correo, String nombreSolicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String bodyCorreo = correoUtils.generaCorreoDocumentacionCuentaSimplificada(cuentaah, nombreSolicitante);
            log.info("Se enviara correo Documentación de cuenta de ahorro a {}", correo);
            msEnviarEmail.enviarCorreoWithAttachment(correo, "Documentación de cuenta de ahorro", bodyCorreo, attachmentsList);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> generaDocumentacionCtaSimplificada(String cuentaah, boolean subirDoc) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = msDocCtaSimplifacada.generarDocumentacionCuentaSimplificada(cuentaah, subirDoc);
            if (respuesta.getCodigo() == 0) {
                Type attachmentListType = new TypeToken<List<AttachmentStringOBJ>>() {
                }.getType();
                List<AttachmentStringOBJ> attachmentsResponseList = gson.fromJson(respuesta.getData(), attachmentListType);
                List<AttachmentOBJ> attachmentsList = new ArrayList<>();
                for (AttachmentStringOBJ attachmentStringOBJ : attachmentsResponseList) {
                    // Decodificar el campo theAttachmentBytes en base64
                    byte[] decodedBytes = Base64.getDecoder().decode(attachmentStringOBJ.getTheAttachmentBytes());
                    // Se declara el objeto AttachmentOBJ para mapear el arreglo de bytes decodificados
                    AttachmentOBJ attachmentOBJ = new AttachmentOBJ();
                    attachmentOBJ.setTheAttachmentBytes(decodedBytes);
                    attachmentOBJ.setTheFilename(attachmentStringOBJ.getTheFilename());
                    attachmentOBJ.setTheContentType(attachmentStringOBJ.getTheContentType());
                    attachmentOBJ.setSend(attachmentStringOBJ.getSend());
                    attachmentsList.add(attachmentOBJ);
                }
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(attachmentsList));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }
}