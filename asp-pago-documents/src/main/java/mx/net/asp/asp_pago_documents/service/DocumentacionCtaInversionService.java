package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.*;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.request.GeneraDocCtaInversionReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresDocCtaInversion;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DocumentacionCtaInversionService {

    private final GeneraReportesService generaReportesService;
    private final ProcreaDAO procreaDAO;
    private final InversionesDAO inversionesDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    public RespuestaDTO generaDocumentacionCtaInversionInicial(GeneraDocCtaInversionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Boolean existeCuenta = inversionesDAO.existeInversionByCuenta(req.getCuentaInversion());
            if (!Boolean.TRUE.equals(existeCuenta)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaInversion.INVERSION_NOT_FOUND,
                        ErroresDocCtaInversion.descError.get(ErroresDocCtaInversion.INVERSION_NOT_FOUND));
            }

            AhorroContratoOBJ ahorroContratoOBJ = procreaDAO.obtenerDatosAhorroContratoByCuenta(req.getCuentaInversion());
            if (ahorroContratoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaInversion.AHORRO_CONTRATO_NOT_FOUND,
                        ErroresDocCtaInversion.descError.get(ErroresDocCtaInversion.AHORRO_CONTRATO_NOT_FOUND));
            }

            ReportesParamOBJ reportesParamOBJ = procreaDAO.obtenerDatosReportesParamByLlave(req.getCuentaInversion());
            if (reportesParamOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaInversion.DATOS_DOC_NOT_FOUND,
                        ErroresDocCtaInversion.descError.get(ErroresDocCtaInversion.DATOS_DOC_NOT_FOUND));
            }

            List<AttachmentOBJ> attachmentsList = generaDocumentacionCuentaInversion(req.getCuentaInversion(), reportesParamOBJ);
            if (attachmentsList != null) {
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(attachmentsList));
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaInversion.GENERA_DOC,
                        ErroresDocCtaInversion.descError.get(ErroresDocCtaInversion.GENERA_DOC));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public List<AttachmentOBJ> generaDocumentacionCuentaInversion(String cuentaInversion, ReportesParamOBJ reportesParamOBJ) {
        List<AttachmentOBJ> attachmentsList = new ArrayList<>();
        AttachmentOBJ attachment;
        byte[] pdfBytes;
        try {
            pdfBytes = generaReportesService.generaConstanciaCtaInversion(cuentaInversion);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename(cuentaInversion + "_constancia.pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            pdfBytes = generaReportesService.generaCaratulaCtaInversion(reportesParamOBJ);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename(cuentaInversion + "_caratula.pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);
        } catch (Exception e) {
            errorHandler.handleException(e);
            attachmentsList = null;
        }
        return attachmentsList;
    }
}
