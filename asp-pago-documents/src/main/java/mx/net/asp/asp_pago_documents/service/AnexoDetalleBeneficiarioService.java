package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.*;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.request.GeneraAnexoDetalleBeneficiarioReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresAnexoDetalleBeneficiario;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AnexoDetalleBeneficiarioService {

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

    public RespuestaDTO generaAnexoDetalleBeneficiario(GeneraAnexoDetalleBeneficiarioReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            CuentaAspPagoOBJ cuentaAspPagoOBJ = ahorroDAO.obtenerDatosGeneralesCuentaAhByCuentaAh(req.getCuentaah());
            if (cuentaAspPagoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.CUENTA_NOT_FOUND,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.CUENTA_NOT_FOUND));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(cuentaAspPagoOBJ.getPersonaId());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            BeneficiarioDetalleOBJ beneficiarioDetalleOBJ = ahorroDAO.obtenerDetalleBeneficiarioByCuentaAh(req.getCuentaah());
            if (beneficiarioDetalleOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.BENEFICIARIO_NOT_FOUND,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.BENEFICIARIO_NOT_FOUND));
            }else if (!beneficiarioDetalleOBJ.getDetalleEstatus()){
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.BENEFICIARIO_DETALLE_NOT_FOUND,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.BENEFICIARIO_DETALLE_NOT_FOUND));
            }

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(regCoDiOBJ.getSolicitanteId());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.SOLICITANTE_NOT_FOUND,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.SOLICITANTE_NOT_FOUND));
            }

            respuesta = solicitanteDAO.obtenerConceptoSolicitanteByClave(conceptoColoniaCtaFacil, solicitanteOBJ.getNumero());
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.DATOS_DOC,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.DATOS_DOC));
            }

            String idColonia = respuesta.getData();
            InfoDomicilioOBJ infoDomicilioOBJ = publicCeroDAO.obtenerInformacionDomicilioByColoniaId(Long.parseLong(idColonia));

            respuesta = llenaDatosAnexoDetalleBeneficiario(solicitanteOBJ, regCoDiOBJ, beneficiarioDetalleOBJ, infoDomicilioOBJ);
            if (respuesta.getCodigo() == 0) {
                AnexoDetalleBeneficiarioOBJ anexoDetalleBeneficiarioOBJ = gson.fromJson(respuesta.getData(), AnexoDetalleBeneficiarioOBJ.class);
                log.info("### Datos Anexo Beneficiario:: " + gson.toJson(anexoDetalleBeneficiarioOBJ));
                AttachmentOBJ attachmentOBJ = generaDocumentoAnexoDetalleBeneficiario(anexoDetalleBeneficiarioOBJ);
                if (attachmentOBJ != null) {
                    RespuestaUtils.respuestaExito(respuesta, gson.toJson(attachmentOBJ));

                    if (Boolean.TRUE.equals(req.getSubirDoc())) {
                        log.info("### Se subira el anexo del detalle del beneficiario");
                        subeDocumentacionService.subeAnexoDetalleBeneficiario(attachmentOBJ, regCoDiOBJ.getCuenta(), solicitanteOBJ.getNumero());
                    }
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresAnexoDetalleBeneficiario.GENERA_DOC,
                            ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.GENERA_DOC));
                }
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.DATOS_DOC,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.DATOS_DOC));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
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
                        ErroresAnexoDetalleBeneficiario.REGISTRO_CODI_NOT_FOUND,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresAnexoDetalleBeneficiario.VERSION_APP,
                        ErroresAnexoDetalleBeneficiario.descError.get(ErroresAnexoDetalleBeneficiario.VERSION_APP));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    public RespuestaDTO llenaDatosAnexoDetalleBeneficiario(SolicitanteOBJ solicitante, RegistroCoDiOBJ registroCoDi, BeneficiarioDetalleOBJ beneficiarioDetalleOBJ, InfoDomicilioOBJ infoDomicilioOBJ) {
        AnexoDetalleBeneficiarioOBJ anexoDetalleBeneficiarioOBJ = new AnexoDetalleBeneficiarioOBJ();
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            /* Listas y otros datos generales */
            respuesta = procreaDAO.obtenerRecaByProductoId(prodcutoIdCtaFacil);
            if (respuesta.getCodigo() == 0) anexoDetalleBeneficiarioOBJ.setRecaFacil(respuesta.getData());
            anexoDetalleBeneficiarioOBJ.setCuenta(registroCoDi.getCuenta());
            anexoDetalleBeneficiarioOBJ.setPersonaId(solicitante.getNumero());

            /* Datos del solicitante */
            anexoDetalleBeneficiarioOBJ.setNombre(solicitante.getNombre_p());
            anexoDetalleBeneficiarioOBJ.setApellidoPaterno(solicitante.getApellido_p());
            anexoDetalleBeneficiarioOBJ.setApellidoMaterno(solicitante.getApellido_m());
            anexoDetalleBeneficiarioOBJ.setFechaNacimiento(solicitante.getFecha_nacimiento());
            anexoDetalleBeneficiarioOBJ.setPorcentaje("100%");
            anexoDetalleBeneficiarioOBJ.setRfc(solicitante.getRfc());
            anexoDetalleBeneficiarioOBJ.setCurp(solicitante.getCurp());
            anexoDetalleBeneficiarioOBJ.setCalle(solicitante.getDescripcionDomicilio1());
            anexoDetalleBeneficiarioOBJ.setNoExterior(solicitante.getDescripcionDomicilio3());
            anexoDetalleBeneficiarioOBJ.setNoInterior(solicitante.getDescripcionDomicilio5());
            anexoDetalleBeneficiarioOBJ.setMunicipio(infoDomicilioOBJ.getMunicipio());
            anexoDetalleBeneficiarioOBJ.setCp(infoDomicilioOBJ.getCp());
            anexoDetalleBeneficiarioOBJ.setColonia(infoDomicilioOBJ.getColonia());
            anexoDetalleBeneficiarioOBJ.setTelefono(solicitante.getTelefonoCoDi());
            anexoDetalleBeneficiarioOBJ.setEntidadFederativa(infoDomicilioOBJ.getEstado());
            anexoDetalleBeneficiarioOBJ.setCorreo(solicitante.getCorreo());

            /* Datos del beneficiario */
            anexoDetalleBeneficiarioOBJ.setPorcentajeBeneficiario(beneficiarioDetalleOBJ.getPorcentaje().toString());
            anexoDetalleBeneficiarioOBJ.setNombreBeneficiarioDetalle(beneficiarioDetalleOBJ.getNombreBeneficiario());
            anexoDetalleBeneficiarioOBJ.setPorcentajeBeneficiarioDetalle(beneficiarioDetalleOBJ.getPorcentaje().toString());
            respuesta = procreaDAO.obtenerParentescoById(beneficiarioDetalleOBJ.getRelacionId());
            if (respuesta.getCodigo() == 0)
                anexoDetalleBeneficiarioOBJ.setParentescoBeneficiarioDetalle(respuesta.getData());
            anexoDetalleBeneficiarioOBJ.setFechaNacimientoBeneficiarioDetalle(beneficiarioDetalleOBJ.getFechaNacimiento());
            anexoDetalleBeneficiarioOBJ.setCalleBeneficiarioDetalle(beneficiarioDetalleOBJ.getCalle());
            anexoDetalleBeneficiarioOBJ.setNoExtBeneficiarioDetalle(beneficiarioDetalleOBJ.getNumeroExterior());
            anexoDetalleBeneficiarioOBJ.setNoIntBeneficiarioDetalle(beneficiarioDetalleOBJ.getNumeroInterior());
            anexoDetalleBeneficiarioOBJ.setMunicipioBeneficiarioDetalle(beneficiarioDetalleOBJ.getDomicilio());
            anexoDetalleBeneficiarioOBJ.setCpBeneficiarioDetalle(beneficiarioDetalleOBJ.getCodigoPostal());
            anexoDetalleBeneficiarioOBJ.setColoniaBeneficiarioDetalle(beneficiarioDetalleOBJ.getColonia());
            anexoDetalleBeneficiarioOBJ.setCiudadBeneficiarioDetalle(beneficiarioDetalleOBJ.getCiudad());
            anexoDetalleBeneficiarioOBJ.setTelefonoBeneficiarioDetalle(beneficiarioDetalleOBJ.getNumeroCelular());

            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(gson.toJson(anexoDetalleBeneficiarioOBJ));

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public AttachmentOBJ generaDocumentoAnexoDetalleBeneficiario(AnexoDetalleBeneficiarioOBJ anexoDetalleBeneficiarioOBJ) {
        AttachmentOBJ attachment;
        byte[] pdfBytes;
        try {
            pdfBytes = generaReportesService.generaAnexoDetalleBeneficiario(anexoDetalleBeneficiarioOBJ);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("Anexo_a_detalle_beneficiario" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);
        } catch (Exception e) {
            errorHandler.handleException(e);
            attachment = null;
        }
        return attachment;
    }

}
