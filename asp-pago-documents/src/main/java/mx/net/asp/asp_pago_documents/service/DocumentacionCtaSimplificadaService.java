package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.*;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.request.GeneraDocCtaSimplificadaReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresDocCtaSimplificada;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DocumentacionCtaSimplificadaService {

    private final PublicCeroDAO publicCeroDAO;
    private final GeneraReportesService generaReportesService;
    private final SubeDocumentacionService subeDocumentacionService;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    @Value("${id.sucursal.virtual}")
    private Integer sucVitualCoDi;
    @Value("${producto.id.cuenta.facil}")
    private Integer prodcutoIdCtaFacil;
    @Value("${concepto.colonia.cuenta.facil}")
    private String conceptoColoniaCtaFacil;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO generaDocumentacionCtaSimplificada(GeneraDocCtaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        BeneficiarioOBJ beneficiarioOBJ;
        try {
            CuentaAspPagoOBJ cuentaAspPagoObj = ahorroDAO.obtenerDatosGeneralesCuentaAhByCuentaAh(req.getCuentaah());
            if (cuentaAspPagoObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaSimplificada.CUENTA_NOT_FOUND,
                        ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.CUENTA_NOT_FOUND));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(cuentaAspPagoObj.getPersonaId());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            beneficiarioOBJ = ahorroDAO.obtenerBeneficiarioCuentaAhByCuentaAh(req.getCuentaah());
            if (beneficiarioOBJ == null)
                log.info("### La cuenta no tiene beneficiario registrado");

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(regCoDiOBJ.getSolicitanteId());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaSimplificada.SOLICITANTE_NOT_FOUND,
                        ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.SOLICITANTE_NOT_FOUND));
            }

            respuesta = llenaDatosDocumentacionCuentaSimplificada(solicitanteOBJ, regCoDiOBJ, beneficiarioOBJ, cuentaAspPagoObj);
            if (respuesta.getCodigo() == 0) {
                DocumentacionCtaFacilOBJ datosContratoCtaFacilObj = gson.fromJson(respuesta.getData(), DocumentacionCtaFacilOBJ.class);
                log.info("### Datos Documentacion Cuenta Facil:: " + gson.toJson(datosContratoCtaFacilObj));
                Boolean band = beneficiarioOBJ != null;
                List<AttachmentOBJ> attachmentsList = generaDocumentacionCuentaSimplificada(datosContratoCtaFacilObj, band);
                if (attachmentsList != null) {
                    respuesta.setCodigo(0);
                    respuesta.setMensaje("OK");
                    respuesta.setData(gson.toJson(attachmentsList));
                    if (Boolean.TRUE.equals(req.getSubirDoc())) {
                        log.info("### Se subira documentacion de cuenta ahorro");
                        subeDocumentacionService.subeDocumentacionCtaSimplificadaGCS(attachmentsList, datosContratoCtaFacilObj.getCuenta(), datosContratoCtaFacilObj.getPersonaId(), band);
                    }
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresDocCtaSimplificada.GENERA_DOC,
                            ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.GENERA_DOC));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validarRegistroCoDi(RegistroCoDiOBJ regCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaSimplificada.REGISTRO_CODI_NOT_FOUND,
                        ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaSimplificada.VERSION_APP,
                        ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.VERSION_APP));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    public RespuestaDTO llenaDatosDocumentacionCuentaSimplificada(SolicitanteOBJ solicitante, RegistroCoDiOBJ registroCoDi, BeneficiarioOBJ beneficiario, CuentaAspPagoOBJ cuentaAspPagoObj) {
        DocumentacionCtaFacilOBJ datosContratoCtaFacilObj = new DocumentacionCtaFacilOBJ();
        RespuestaDTO respuesta = new RespuestaDTO();
        String recaFacil;
        String idColonia;
        try {

            if (beneficiario != null) {
                datosContratoCtaFacilObj.setNombreBeneficiario(beneficiario.getNombreBeneficiario());
                datosContratoCtaFacilObj.setPorcentajeBeneficiario(beneficiario.getPorcentaje().toString());
                respuesta = procreaDAO.obtenerParentescoById(beneficiario.getRelacionId());
                if (respuesta.getCodigo() == 0) {
                    datosContratoCtaFacilObj.setParentescoBeneficiario(respuesta.getData());
                }
            }

            datosContratoCtaFacilObj.setNombreSolicitanteCompleto(solicitante.getNombre());
            datosContratoCtaFacilObj.setNombreSolicitante(solicitante.getNombre_p());
            datosContratoCtaFacilObj.setApellidoPaterno(solicitante.getApellido_p());
            datosContratoCtaFacilObj.setApellidoMaterno(solicitante.getApellido_m());
            datosContratoCtaFacilObj.setFechaNaciemiento(solicitante.getFecha_nacimiento());
            datosContratoCtaFacilObj.setCorreo(solicitante.getCorreo());
            datosContratoCtaFacilObj.setTelefono(solicitante.getTelefonoCoDi());
            datosContratoCtaFacilObj.setRfc(solicitante.getRfc());
            datosContratoCtaFacilObj.setCurp(solicitante.getCurp());
            datosContratoCtaFacilObj.setCalle(solicitante.getDescripcionDomicilio1());
            datosContratoCtaFacilObj.setNoExterior(solicitante.getDescripcionDomicilio3());
            datosContratoCtaFacilObj.setNoInterior(solicitante.getDescripcionDomicilio5());
            datosContratoCtaFacilObj.setApellidoMaterno(solicitante.getApellido_m());
            datosContratoCtaFacilObj.setApellidoMaterno(solicitante.getApellido_m());
            datosContratoCtaFacilObj.setPersonaId(solicitante.getNumero());
            datosContratoCtaFacilObj.setCuenta(registroCoDi.getCuenta());
            datosContratoCtaFacilObj.setReferencia(registroCoDi.getCuenta());
            datosContratoCtaFacilObj.setCuentaClabe(cuentaAspPagoObj.getClabe());
            datosContratoCtaFacilObj.setSucursalId(sucVitualCoDi);
            datosContratoCtaFacilObj.setEmpresa("");

            //Se obtiene el reca para el prodcuto CUENTA FACIL
            respuesta = procreaDAO.obtenerRecaByProductoId(prodcutoIdCtaFacil);

            if (respuesta.getCodigo() == 0) {
                recaFacil = respuesta.getData();
                log.info("### RECA obtenido:: " + recaFacil);
                datosContratoCtaFacilObj.setRecaFacil(recaFacil);
                //Se obtiene el concepto de colonia del solicitante
                respuesta = solicitanteDAO.obtenerConceptoSolicitanteByClave(conceptoColoniaCtaFacil, solicitante.getNumero());
                if (respuesta.getCodigo() == 0) {
                    log.info("### idColonia obtenida:: " + respuesta.getData());
                    idColonia = respuesta.getData();

                    InfoDomicilioOBJ infoDomicilioOBJ = publicCeroDAO.obtenerInformacionDomicilioByColoniaId(Long.parseLong(idColonia));
                    if (infoDomicilioOBJ == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresDocCtaSimplificada.DATOS_DOC,
                                ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.DATOS_DOC));
                    }

                    log.info("Datos Domicilio:: " + gson.toJson(infoDomicilioOBJ));
                    datosContratoCtaFacilObj.setEstado(infoDomicilioOBJ.getEstado());
                    datosContratoCtaFacilObj.setMunicipio(infoDomicilioOBJ.getMunicipio());
                    datosContratoCtaFacilObj.setLocalidad(infoDomicilioOBJ.getLocalidad());
                    datosContratoCtaFacilObj.setColonia(infoDomicilioOBJ.getColonia());
                    datosContratoCtaFacilObj.setCp(infoDomicilioOBJ.getCp());

                    respuesta.setCodigo(0);
                    respuesta.setMensaje("OK");
                    respuesta.setData(gson.toJson(datosContratoCtaFacilObj));

                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresDocCtaSimplificada.DATOS_DOC,
                            ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.DATOS_DOC));
                }
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDocCtaSimplificada.DATOS_DOC,
                        ErroresDocCtaSimplificada.descError.get(ErroresDocCtaSimplificada.DATOS_DOC));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public List<AttachmentOBJ> generaDocumentacionCuentaSimplificada(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj, Boolean beneficiario) {
        List<AttachmentOBJ> attachmentsList = new ArrayList<>();
        AttachmentOBJ attachment;
        byte[] pdfBytes;
        try {
            pdfBytes = generaReportesService.generaTarjetaAhorro(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("TarjetaAhorro" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            pdfBytes = generaReportesService.generaContratoCuentaSimplificada(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("ContratoCuentaSimplificada" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            pdfBytes = generaReportesService.generaContratoServElecSF(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("ContratoServiciosElectronicos" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            //NO se envian por correo pero si se sube.
            pdfBytes = generaReportesService.generaRegistroContrato(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("Registro_Contrato" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(false);
            attachmentsList.add(attachment);

            pdfBytes = generaReportesService.generaDisposicionesLegales(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("disposiciones_legales" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            pdfBytes = generaReportesService.generaCaratula(datosContratoCtaFacilObj);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("caratula" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
            attachmentsList.add(attachment);

            if (Boolean.TRUE.equals(beneficiario)) {
                pdfBytes = generaReportesService.generaAnexo(datosContratoCtaFacilObj);
                attachment = new AttachmentOBJ();
                attachment.setTheAttachmentBytes(pdfBytes);
                attachment.setTheFilename("Anexo_a" + ".pdf");
                attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
                attachment.setSend(true);
                attachmentsList.add(attachment);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            attachmentsList = null;
        }
        return attachmentsList;
    }

}
