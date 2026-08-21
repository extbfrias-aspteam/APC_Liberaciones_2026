package mx.net.asp.asp_pago_management.service;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.AttachmentOBJ;
import mx.net.asp.asp_pago_management.model.AttachmentStringOBJ;
import mx.net.asp.asp_pago_management.model.ReporteEstadoMovimientoOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.MSReporteEstadoMovimiento;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReporteEstadoMovimientoAsync {
    private final MSReporteEstadoMovimiento msReporteEstadoMovimiento;
    private final ErrorHandler errorHandler;

    private final Gson gson;
    

    @Async
    public CompletableFuture<RespuestaDTO> generarReporteEstadoMovimiento(ReporteEstadoMovimientoOBJ req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = msReporteEstadoMovimiento.generarReporteEdoMovimientos(req, false);
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
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }
}
