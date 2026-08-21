package mx.net.asp.asp_pago_management.service;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.CoDiDAO;
import mx.net.asp.asp_pago_management.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.AttachmentOBJ;
import mx.net.asp.asp_pago_management.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_management.model.ReporteEstadoMovimientoOBJ;
import mx.net.asp.asp_pago_management.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_management.request.ReporteEstadoMovimientoReq;
import mx.net.asp.asp_pago_management.utilerias.CifradoUtil;
import mx.net.asp.asp_pago_management.utilerias.CorreoUtils;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresEstadoMov;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.MSEnviarEmail;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReporteEstadoMovimientoService {
    private final CoDiDAO coDiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final ReporteEstadoMovimientoAsync reporteEstadoMovimientoAsync;

    private final ErrorHandler errorHandler;
    private final MSEnviarEmail msEnviarEmail;
    private final CorreoUtils correoUtils;
    private final Gson gson;
    private final CifradoUtil cifradoUtil;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO generaReporteEstadoMovimiento(ReporteEstadoMovimientoReq req) {

        RespuestaDTO respuesta = new RespuestaDTO();
        String dataCif = req.getData();
        String keySourceASP = "";

        try {
            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = coDiDAO.obtenerRegistroCoDiByCuentaAh(req.getCuenta());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if(respuesta.getCodigo() != 0)
                return respuesta;
            
            if (req.getData().isEmpty() || req.getData() == null) {
                log.info("El campo data no contiene informacion");

                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.DATA_WITHOUT_INFORMATION,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.DATA_WITHOUT_INFORMATION));
            }

            keySourceASP = cifradoUtil.generaKeySource(regCoDiOBJ.getIdHardware(), regCoDiOBJ.getPassword());

            log.info("keySourceASP Edo Movimientos :: " + keySourceASP);
            String dataDecripted = "";
            dataDecripted = cifradoUtil.desencriptaInformacionB64(keySourceASP, dataCif);
            log.info("dataDecripted :: " + dataDecripted);
            ReporteEstadoMovimientoOBJ reporteEstadoMovimientoOBJ = gson.fromJson(dataDecripted, ReporteEstadoMovimientoOBJ.class);

            CompletableFuture<RespuestaDTO> docGeneradaFuture = reporteEstadoMovimientoAsync.generarReporteEstadoMovimiento(reporteEstadoMovimientoOBJ);
            docGeneradaFuture.thenAccept(response -> {
                try {
                    if (response.getCodigo() == 0) {
                        log.info("*** Reporte de Estado de Movimientos generado correctamente ***");
                        AttachmentOBJ attachmentOBJ = gson.fromJson(response.getData(), AttachmentOBJ.class);
                        RespuestaDTO resp = solicitanteDAO.obtenerInfoGeneralSolicitanteById(regCoDiOBJ.getSolicitanteId());
                        if (resp.getCodigo() == 0) {
                            SolicitanteOBJ solicitanteOBJ = gson.fromJson(resp.getData(), SolicitanteOBJ.class);
                            enviaRteEdoMovimientos(attachmentOBJ, regCoDiOBJ.getCuenta(), solicitanteOBJ.getCorreo(), solicitanteOBJ.getNombre());
                        }
                    } else {
                        log.error("Ocurrio un error al generar la Documentacion");
                    }
                } catch (Exception e) {
                    log.error("Error procesando la documentacion generada:");
                    errorHandler.handleException(e);
                }
            });

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresEstadoMov.REGISTRO_CODI_NOT_FOUND,
                    ErroresEstadoMov.descError.get(ErroresEstadoMov.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresEstadoMov.VERSION_APP,
                    ErroresEstadoMov.descError.get(ErroresEstadoMov.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> enviaRteEdoMovimientos(AttachmentOBJ attachmentOBJ, String cuentaah, String correo, String nombreSolicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String bodyCorreo = correoUtils.generaCorreoReporteEstadoMovimiento(nombreSolicitante);
            log.info("Se enviara correo Reporte Estado de Movimientos a {}", correo);
            msEnviarEmail.enviarCorreoWithOneAttachment(correo, "Reporte Estado de Movimientos", bodyCorreo, attachmentOBJ);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

}
