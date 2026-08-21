package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.*;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.request.AltaBeneficiarioReq;
import mx.net.asp.asp_pago_onb.request.BeneficiarioReqOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresAltaBeneficiario;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class BeneficiarioService {

    private final CoDiDAO codiDAO;
    private final AhorroDAO ahorroDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final Gson gson;
    private final StringFormatUtils stringFormatUtils;
    private final DocumentacionCtaSimplificadaService documentacionCtaSimplificadaService;
    private final ErrorHandler errorHandler;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO altaBeneficiarioAspPago(AltaBeneficiarioReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            CuentaAspPagoOBJ cuentaAspPagoObj = ahorroDAO.obtenerDatosGeneralesCuentaAhByCuentaAh(req.getCuenta());
            if (cuentaAspPagoObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAltaBeneficiario.CUENTA_NOT_FOUND,
                        ErroresAltaBeneficiario.descError.get(ErroresAltaBeneficiario.CUENTA_NOT_FOUND));
            }

            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(cuentaAspPagoObj.getPersonaId());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if(respuesta.getCodigo() != 0)
                return respuesta;

            Boolean beneficiario = ahorroDAO.validaCuentaTieneBeneficiarioByCuentaAh(req.getCuenta());
            if (Boolean.TRUE.equals(beneficiario)) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAltaBeneficiario.BENEFICIARIO_REGISTRADO,
                        ErroresAltaBeneficiario.descError.get(ErroresAltaBeneficiario.BENEFICIARIO_REGISTRADO));
            }

            for (BeneficiarioReqOBJ benOBJ : req.getBeneficiarios()) {
                //Se procede a registrar al beneficiario
                respuesta = registrarBeneficiario(cuentaAspPagoObj, benOBJ);
                if (respuesta.getCodigo() != 0) {
                    return respuesta;
                }

                procesaDocumentacion(regCoDiOBJ.getCuenta(), regCoDiOBJ.getSolicitanteId());
                RespuestaUtils.respuestaExito(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO registrarBeneficiario(CuentaAspPagoOBJ cuentaAspPagoObj, BeneficiarioReqOBJ benOBJ) {
        BeneficiarioOBJ beneficiarioOBJ = new BeneficiarioOBJ();
        beneficiarioOBJ.setNombreBeneficiario(concatenarNombre(benOBJ));
        beneficiarioOBJ.setPorcentaje(100);
        beneficiarioOBJ.setCuentaId(cuentaAspPagoObj.getCuentaId());
        beneficiarioOBJ.setRelacionId(benOBJ.getParentesco());

        RespuestaDTO respuesta = ahorroDAO.registrarBeneficiarioCuentaAh(beneficiarioOBJ);
        if (respuesta.getCodigo() != 0) {
            return RespuestaUtils.asignarError(new RespuestaDTO(),
                    ErroresAltaBeneficiario.ERROR_REGISTRA_BENEFICIARIO,
                    ErroresAltaBeneficiario.descError.get(ErroresAltaBeneficiario.ERROR_REGISTRA_BENEFICIARIO));
        }
        return respuesta;
    }

    private String concatenarNombre(BeneficiarioReqOBJ benOBJ) {
        return String.format("%s %s %s",
                stringFormatUtils.limpiarEspacios(benOBJ.getNombre()),
                stringFormatUtils.limpiarEspacios(benOBJ.getPrimer_apellido()),
                stringFormatUtils.limpiarEspacios(benOBJ.getSegundo_apellido()));
    }

    private void procesaDocumentacion(String cuenta, String solicitanteId) {
        CompletableFuture<RespuestaDTO> docGeneradaFuture = documentacionCtaSimplificadaService.generaDocumentacionCtaSimplificada(cuenta, true);
        docGeneradaFuture.thenAccept(response -> {
            try {
                if (response.getCodigo() == 0) {
                    log.info("*** Documentación de Cuenta Simplificada generada correctamente ***");
                    enviarDocumentacion(response, cuenta, solicitanteId);
                } else {
                    log.error("Ocurrió un error al generar la Documentación");
                }
            } catch (Exception e) {
                log.error("Error procesando la documentación generada:");
                errorHandler.handleException(e);
            }
        });
    }

    private void enviarDocumentacion(RespuestaDTO response, String cuenta, String solicitanteId) {
        Type attachmentListType = new TypeToken<List<AttachmentOBJ>>() {
        }.getType();
        List<AttachmentOBJ> attachmentsList = gson.fromJson(response.getData(), attachmentListType);

        List<AttachmentOBJ> attachmentsToSendList = attachmentsList.stream()
                .filter(AttachmentOBJ::getSend)
                .toList();

        SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(solicitanteId);
        if (solicitanteOBJ != null) {
            documentacionCtaSimplificadaService.enviaDocumentacionCtaSimplificada(
                    attachmentsToSendList, cuenta, solicitanteOBJ.getCorreo(), solicitanteOBJ.getNombre());
        }
    }

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresAltaBeneficiario.REGISTRO_CODI_NOT_FOUND,
                    ErroresAltaBeneficiario.descError.get(ErroresAltaBeneficiario.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresAltaBeneficiario.VERSION_APP,
                    ErroresAltaBeneficiario.descError.get(ErroresAltaBeneficiario.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }
}