package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.request.AltaBeneficiarioReq;
import mx.net.asp.asp_pago_management.response.BeneficiarioDetalleResponse;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresBeneficiario;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresCuenta;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class BeneficiarioService {

    private final AhorroDAO ahorroDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final Gson gson;
    private final AnexoDetalleBeneficiarioService anexoDetalleBeneficiarioService;
    private final ProcreaDAO procreaDAO;
    private final ErrorHandler errorHandler;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO altaBeneficiarioDetalle(AltaBeneficiarioReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(req.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCuenta.CUENTA_NOT_FOUND,
                        ErroresCuenta.descError.get(ErroresCuenta.CUENTA_NOT_FOUND));
            }

            BeneficiarioOBJ beneficiarioOBJ = ahorroDAO.obtenerBeneficiarioCuentaAhByCuentaAh(req.getCuenta());

            if (beneficiarioOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.BENEFICIARIO_NOT_FOUND,
                        ErroresBeneficiario.descError.get(ErroresBeneficiario.BENEFICIARIO_NOT_FOUND));
            }

            req.setId(beneficiarioOBJ.getId());
            req.setUsuario_creacion(beneficiarioOBJ.getCuentaId());

            if (Boolean.TRUE.equals(beneficiarioOBJ.getDetalleEstatus())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.DETALLE_BENEFICIARIO_EXISTS,
                        ErroresBeneficiario.getMensajeError(ErroresBeneficiario.DETALLE_BENEFICIARIO_EXISTS));
            }

            respuesta = ahorroDAO.verificarBeneficiarioAsociadoCuenta(req.getId());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.DETALLE_BENEFICIARIO_COMPLETE,
                        ErroresBeneficiario.getMensajeError(ErroresBeneficiario.DETALLE_BENEFICIARIO_COMPLETE));
            }

            respuesta = ahorroDAO.insertarDetalleBeneficiario(req);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.REGISTRAR_DETALLE_BENEFICIARIO,
                        ErroresBeneficiario.getMensajeError(ErroresBeneficiario.REGISTRAR_DETALLE_BENEFICIARIO));
            }

            respuesta = ahorroDAO.actualizarDetalleEstatusBeneficiario(beneficiarioOBJ.getCuentaId());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.ACTUALIZAR_DETALLE_BENEFICIARIO,
                        ErroresBeneficiario.getMensajeError(ErroresBeneficiario.ACTUALIZAR_DETALLE_BENEFICIARIO));
            }

            CompletableFuture<RespuestaDTO> docGeneradaFuture = anexoDetalleBeneficiarioService.generaAnexoDetalleBeneficiario(req.getCuenta(), true);
            docGeneradaFuture.thenAccept(response -> {
                try {
                    if (response.getCodigo() == 0) {
                        log.info("*** Reporte de estado de movimiento generado correctamente ***");
                        AttachmentOBJ attachmentOBJ = gson.fromJson(response.getData(), AttachmentOBJ.class);
                        RespuestaDTO resp = solicitanteDAO.obtenerInfoGeneralSolicitanteById(posGlobCuentaObj.getPersonaId());
                        if (resp.getCodigo() == 0) {
                            SolicitanteOBJ solicitanteOBJ = gson.fromJson(resp.getData(), SolicitanteOBJ.class);
                            anexoDetalleBeneficiarioService.enviaDocumentoDetalleBeneficiario(attachmentOBJ, req.getCuenta(), solicitanteOBJ.getCorreo(), solicitanteOBJ.getNombre());
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
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validaBeneficiarioDetalle(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            BeneficiarioOBJ beneficiarioOBJ = ahorroDAO.obtenerBeneficiarioCuentaAhByCuentaAh(cuenta);

            if (beneficiarioOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresBeneficiario.BENEFICIARIO_NOT_FOUND,
                        ErroresBeneficiario.descError.get(ErroresBeneficiario.BENEFICIARIO_NOT_FOUND));
            }

            if (Boolean.TRUE.equals(beneficiarioOBJ.getDetalleEstatus())) {
                return RespuestaUtils.respuestaExito(respuesta, 0, "El beneficiario tiene los datos completos.");
            } else {
                BeneficiarioDetalleResponse benDetalleResponse = new BeneficiarioDetalleResponse();
                benDetalleResponse.setNombre(beneficiarioOBJ.getNombreBeneficiario());
                respuesta = procreaDAO.obtenerParentescoById(beneficiarioOBJ.getRelacionId());
                if (respuesta.getCodigo() == 0) {
                    benDetalleResponse.setParentesco(respuesta.getData());
                }
                return RespuestaUtils.respuestaExito(respuesta, 2, "No se ha llenado el detallado del beneficiario.", gson.toJson(benDetalleResponse));
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}