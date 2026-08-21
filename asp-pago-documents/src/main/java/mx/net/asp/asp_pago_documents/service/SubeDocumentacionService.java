package mx.net.asp.asp_pago_documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.InMemoryMultipartFile;
import mx.net.asp.asp_pago_documents.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.ws.asp.SubirDocumentosFTP;
import mx.net.asp.asp_pago_documents.dao.*;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class SubeDocumentacionService {

    private final FileUploadStorageFLService fileUploadStorageFLService;
    private final SubirDocumentosFTP subirDocumentosFTP;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final ErrorHandler errorHandler;
    private final StringFormatUtils stringFormatUtils;

    @Value("${clave.ine}")
    private String claveINE;
    @Value("${id.doc.tarjeta.ahorro}")
    private Integer idTarjetaAhorro;
    @Value("${id.doc.contrato}")
    private Integer idContrato;
    @Value("${id.doc.contrato.serv.elect}")
    private Integer idContratoServElec;
    @Value("${id.doc.registro.contrato}")
    private Integer idRegistroContrato;
    @Value("${id.doc.anexo.disp.legales}")
    private Integer idAnexoDispLegales;
    @Value("${id.doc.anexo.a}")
    private Integer idAnexoA;
    @Value("${id.doc.caratula}")
    private Integer idCaratula;
    @Value("${id.doc.ine.front}")
    private Integer idIneFront;
    @Value("${id.doc.ine.reverso}")
    private Integer idIneReverso;
    @Value("${id.doc.foto.selfie}")
    private Integer idFotoSelfie;
    @Value("${id.doc.anexo.detalle.beneficiario}")
    private Integer idAnexoDetalleBeneficiario;

    @Async
    public CompletableFuture<RespuestaDTO> subeDocumentacionCtaSimplificada(List<AttachmentOBJ> attachmentsList, String cuenta, String solicitanteId, Boolean beneficiario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Map<String, Object> archivosNombres = new HashMap<>();
            List<Map<String, Object>> archivos = new ArrayList<>();
            archivosNombres.put("nombre", "TARJETA DE AHORRO.pdf");
            archivosNombres.put("id", idTarjetaAhorro);
            archivos.add(archivosNombres);
            archivosNombres = new HashMap<>();
            archivosNombres.put("nombre", "CONTRATO.pdf");
            archivosNombres.put("id", idContrato);
            archivos.add(archivosNombres);
            archivosNombres = new HashMap<>();
            archivosNombres.put("nombre", "CONTRATO DE SERVICIOS ELECTRONICOS.pdf");
            archivosNombres.put("id", idContratoServElec);
            archivos.add(archivosNombres);
            archivosNombres = new HashMap<>();
            archivosNombres.put("nombre", "Registro_Contrato.pdf");
            archivosNombres.put("id", idRegistroContrato);
            archivos.add(archivosNombres);
            archivosNombres = new HashMap<>();
            archivosNombres.put("nombre", "ANEXO A_DISPOSICIONES LEGALES.pdf");
            archivosNombres.put("id", idAnexoDispLegales);
            archivos.add(archivosNombres);
            archivosNombres = new HashMap<>();
            archivosNombres.put("nombre", "CARATULA.pdf");
            archivosNombres.put("id", idCaratula);
            archivos.add(archivosNombres);
            if (Boolean.TRUE.equals(beneficiario)) {
                archivosNombres = new HashMap<>();
                archivosNombres.put("nombre", "ANEXO_A.pdf");
                archivosNombres.put("id", idAnexoA);
                archivos.add(archivosNombres);
            }
            HeaderWS headerWS = new HeaderWS();
            subirDocumentosFTP.subirDocumentosFTP(cuenta, solicitanteId, attachmentsList, archivos, headerWS);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> subeDocumentacionCtaSimplificadaGCS(
            List<AttachmentOBJ> attachmentsList,
            String cuenta,
            String solicitanteId,
            Boolean beneficiario) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<MultipartFile> multipartFiles = convertirAttachmentsAMultipart(attachmentsList, beneficiario);

            List<UploadFileGCSOBJ> response = fileUploadStorageFLService
                    .uploadFileListAspPagoLocalFirst("contratos", solicitanteId + "/" + cuenta, multipartFiles, "ONBOARDING");

            log.info("Resultado subida documentacion cta simplificada GCS: {}",
                    stringFormatUtils.toJsonSafe(response));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    private List<MultipartFile> convertirAttachmentsAMultipart(
            List<AttachmentOBJ> attachmentsList,
            Boolean beneficiario
    ) {
        List<String> nombresEsperados = obtenerNombresDocumentos(beneficiario);
        List<MultipartFile> multipartFiles = new ArrayList<>();

        if (attachmentsList == null || attachmentsList.isEmpty()) {
            return multipartFiles;
        }

        int limite = Math.min(attachmentsList.size(), nombresEsperados.size());

        for (int i = 0; i < limite; i++) {
            AttachmentOBJ attachment = attachmentsList.get(i);

            if (attachment == null || Boolean.FALSE.equals(attachment.getSend())) {
                continue;
            }

            String nombreArchivo = nombresEsperados.get(i);
            String contentType = attachment.getTheContentType();

            MultipartFile multipartFile = new InMemoryMultipartFile(
                    "files",
                    nombreArchivo,
                    contentType,
                    attachment.getTheAttachmentBytes()
            );

            multipartFiles.add(multipartFile);
        }

        return multipartFiles;
    }

    private List<String> obtenerNombresDocumentos(Boolean beneficiario) {
        List<String> nombres = new ArrayList<>();
        nombres.add("TARJETA DE AHORRO.pdf");
        nombres.add("CONTRATO.pdf");
        nombres.add("CONTRATO DE SERVICIOS ELECTRONICOS.pdf");
        nombres.add("Registro_Contrato.pdf");
        nombres.add("ANEXO A_DISPOSICIONES LEGALES.pdf");
        nombres.add("CARATULA.pdf");

        if (Boolean.TRUE.equals(beneficiario)) {
            nombres.add("ANEXO_A.pdf");
        }

        return nombres;
    }

    @Async
    public CompletableFuture<RespuestaDTO> subeImagenes(List<AttachmentOBJ> attachmentsList, String cuenta, String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<Map<String, Object>> archivos = new ArrayList<>();
        try {
            for (AttachmentOBJ attachment : attachmentsList) {
                Map<String, Object> archivosNombres = new HashMap<>();
                archivosNombres.put("nombre", attachment.getTheFilename());
                archivosNombres.put("id", attachment.getId());
                archivos.add(archivosNombres);
            }

            procesaSubirImagenes(attachmentsList, archivos, cuenta, solicitanteId);
            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    private void procesaSubirImagenes(List<AttachmentOBJ> attachmentsList, List<Map<String, Object>> archivos, String cuenta, String solicitanteId) {
        HeaderWS headerWS = new HeaderWS();
        CompletableFuture<RespuestaDTO> imgSubidasFuture = subirDocumentosFTP.subirDocumentosFTP(cuenta, solicitanteId, attachmentsList, archivos, headerWS);
        imgSubidasFuture.thenAccept(response -> {
            try {
                log.info("response:: {}", response);
                String valor;
                if (response.getCodigo() == 0) {
                    log.info("*** Imagenes INE subidas correctamente ***");
                    valor = "OK";
                } else {
                    log.info("*** Error al subir Imagenes INE ***");
                    valor = "ERROR";
                }

                PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
                if (posGlobCuentaObj != null) {
                    if (posGlobCuentaObj.getTipoCuenta().equals("AHORRO")) {
                        //Se obtiene el concepto del codigo de autorización en PROCREA
                        ConceptoCuentaOBJ conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaProcreaByClave(claveINE, solicitanteId, cuenta);
                        if (conceptoCuentaOBJ != null) {
                            //Ya cuenta con el concepto de la INE, hay que actualizarlo
                            conceptosCuentaDAO.actualizarConceptoCuentaProcreaById(conceptoCuentaOBJ.getId(), valor);
                        } else {
                            //No cuenta con concepto INE, hay que registrarlo
                            conceptosCuentaDAO.guardarConceptoCuentaProcrea(claveINE, valor, cuenta);
                        }
                    } else {
                        //Se obtiene el concepto del codigo de autorización en CERO
                        ConceptoCuentaOBJ conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaCeroByClave(claveINE, solicitanteId, cuenta);
                        if (conceptoCuentaOBJ != null) {
                            //Ya cuenta con el concepto de la INE, hay que actualizarlo
                            conceptosCuentaDAO.actualizarConceptoCuentaCeroById(conceptoCuentaOBJ.getId(), valor);
                        } else {
                            //No cuenta con concepto INE, hay que registrarlo
                            conceptosCuentaDAO.guardarConceptoCuentaCero(claveINE, valor, cuenta);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error procesando el registro en BD de imagenes INE:");
                errorHandler.handleException(e);
            }
        });
    }

    @Async
    public CompletableFuture<RespuestaDTO> subeAnexoDetalleBeneficiario(AttachmentOBJ attachmentOBJ, String cuenta, String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            Map<String, Object> archivosNombres = new HashMap<>();
            List<Map<String, Object>> archivos = new ArrayList<>();
            archivosNombres.put("nombre", "Anexo_a_detalle_beneficiario.pdf");
            archivosNombres.put("id", idAnexoDetalleBeneficiario);
            archivos.add(archivosNombres);

            // Crear una lista de AttachmentOBJ y agregar attachmentOBJ
            List<AttachmentOBJ> attachmentsList = new ArrayList<>();
            attachmentsList.add(attachmentOBJ);

            HeaderWS headerWS = new HeaderWS();
            subirDocumentosFTP.subirDocumentosFTP(cuenta, solicitanteId, attachmentsList, archivos, headerWS);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }
}
