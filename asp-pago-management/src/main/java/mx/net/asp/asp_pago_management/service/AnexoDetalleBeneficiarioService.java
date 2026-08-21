package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.AttachmentOBJ;
import mx.net.asp.asp_pago_management.model.AttachmentStringOBJ;
import mx.net.asp.asp_pago_management.utilerias.CorreoUtils;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.ws.asp.MSAnexoDetalleBeneficiario;
import mx.net.asp.asp_pago_management.ws.asp.MSEnviarEmail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@AllArgsConstructor
public class AnexoDetalleBeneficiarioService {

    private final MSAnexoDetalleBeneficiario msAnexoDetalleBeneficiario;
    private final Gson gson;
    private final MSEnviarEmail msEnviarEmail;
    private final CorreoUtils correoUtils;
    private final ErrorHandler errorHandler;

    @Async
    public CompletableFuture<RespuestaDTO> enviaDocumentoDetalleBeneficiario(AttachmentOBJ attachmentOBJ, String cuentaah, String correo, String nombreSolicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String bodyCorreo = correoUtils.generaCorreoAnexoDetalleBeneficiario(cuentaah, nombreSolicitante);
            log.info("Se enviara correo Anexo detalle beneficiario a {}", correo);
            msEnviarEmail.enviarCorreoWithOneAttachment(correo, "Documentación de Anexo de detalle de beneficiario", bodyCorreo, attachmentOBJ);
            log.info("Se envio el correo");

        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> generaAnexoDetalleBeneficiario(String cuentaah, boolean subirDoc) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = msAnexoDetalleBeneficiario.generarAnexoDetalleBeneficiario(cuentaah, subirDoc);
            if (respuesta.getCodigo() == 0) {
                
                AttachmentStringOBJ attachmentStringOBJ = gson.fromJson(respuesta.getData(), AttachmentStringOBJ.class);
                
                byte[] decodedBytes = Base64.getDecoder().decode(attachmentStringOBJ.getTheAttachmentBytes());

                AttachmentOBJ attachmentOBJ = new AttachmentOBJ();
                attachmentOBJ.setTheAttachmentBytes(decodedBytes);
                attachmentOBJ.setTheFilename(attachmentStringOBJ.getTheFilename());
                attachmentOBJ.setTheContentType(attachmentStringOBJ.getTheContentType());
                attachmentOBJ.setSend(attachmentStringOBJ.getSend());
                
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(attachmentOBJ));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            respuesta.setCodigo(-200);
            respuesta.setMensaje("Error interno");
            respuesta.setData(null);
        }
        return CompletableFuture.completedFuture(respuesta);
    }

}