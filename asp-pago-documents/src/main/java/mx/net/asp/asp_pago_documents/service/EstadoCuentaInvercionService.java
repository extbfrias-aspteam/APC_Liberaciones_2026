package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.InversionesDAO;
import mx.net.asp.asp_pago_documents.dao.ProcreaDAO;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AhorroContratoOBJ;
import mx.net.asp.asp_pago_documents.model.AttachmentOBJ;
import mx.net.asp.asp_pago_documents.request.GeneraEdoCtaInversionReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresDocCtaInversion;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresEstadoCuentaInversion;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class EstadoCuentaInvercionService {

    private final GeneraReportesService generaReportesService;
    private final InversionesDAO inversionesDAO;
    private final ProcreaDAO procreaDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    public RespuestaDTO generaEdoCuentaInicial(GeneraEdoCtaInversionReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Boolean existeCuenta = inversionesDAO.existeInversionByCuenta(req.getCuentaInversion());
            if (!Boolean.TRUE.equals(existeCuenta)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoCuentaInversion.CUENTA_NO_EXISTE,
                        ErroresEstadoCuentaInversion.descError.get(ErroresEstadoCuentaInversion.CUENTA_NO_EXISTE));
            }

            Integer res = inversionesDAO.validaExisteInformacionEdoCuenta(req.getAnio(), req.getNumMes(), req.getCuentaInversion());
            if (res == -1) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoCuentaInversion.NO_HAY_DATOS_EDO_CUENTA,
                        ErroresEstadoCuentaInversion.descError.get(ErroresEstadoCuentaInversion.NO_HAY_DATOS_EDO_CUENTA));
            }

            AhorroContratoOBJ ahorroContratoOBJ = procreaDAO.obtenerDatosAhorroContratoByCuenta(req.getCuentaInversion());
            if (ahorroContratoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoCuentaInversion.AHORRO_CONTRATO_NOT_FOUND,
                        ErroresEstadoCuentaInversion.descError.get(ErroresEstadoCuentaInversion.AHORRO_CONTRATO_NOT_FOUND));
            }

            AttachmentOBJ attachmentOBJ = generaEdoCuenta(req);
            if (attachmentOBJ != null) {
                log.info("Se genero documento del Año: " + req.getAnio() + ", Mes: " + req.getNumMes() + " de la cuenta: " + req.getCuentaInversion());
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(gson.toJson(attachmentOBJ));
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

    public AttachmentOBJ generaEdoCuenta(GeneraEdoCtaInversionReq req) {
        AttachmentOBJ attachment;
        byte[] pdfBytes;
        try {
            pdfBytes = generaReportesService.generaEdoCuentaCtaInversion(req.getCuentaInversion(), req.getAnio(), req.getNumMes());
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename(req.getCuentaInversion() + "_" + req.getAnio() + "_" + req.getNumMes() + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
        } catch (Exception e) {
            errorHandler.handleException(e);
            attachment = null;
        }
        return attachment;
    }
}
