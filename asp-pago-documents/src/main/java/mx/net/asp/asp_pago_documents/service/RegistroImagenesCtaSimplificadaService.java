package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.*;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.request.RegistroImagenesCtaSimplificadaReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresImgCtaSimplificada;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class RegistroImagenesCtaSimplificadaService {

    private static final String MODULO_AHORRO = "AHORRO";
    private static final String FOTO_SELFIE = "FOTO SELFIE.png";
    private static final String VALOR_OK = "OK";
    private static final int TIPO_DOCUMENTO = 9;

    private final SubeDocumentacionService subeDocumentacionService;
    private final CoDiDAO codiDAO;
    private final PublicProcreaDAO publicProcreaDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final ErrorHandler errorHandler;

    @Value("${clave.ine}")
    private String claveINE;
    @Value("${id.doc.ine.front}")
    private Integer idIneFront;
    @Value("${id.doc.ine.reverso}")
    private Integer idIneReverso;
    @Value("${id.doc.foto.selfie}")
    private Integer idFotoSelfie;


    public RespuestaDTO registaImagenesCtaSimplificada(RegistroImagenesCtaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<AttachmentOBJ> attachmentsList = new ArrayList<>();
        AttachmentOBJ attachment;

        try {
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.obtenerRegistroCoDiByCuentaAh(req.getNumeroCuenta());
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresImgCtaSimplificada.CUENTA_NOT_FOUND,
                        ErroresImgCtaSimplificada.descError.get(ErroresImgCtaSimplificada.CUENTA_NOT_FOUND));
            }

            log.info("### Se subirán {} imagenes INE de cuenta ahorro", req.getRutasIne().size());

            byte[] fotoSelfie = Base64.getDecoder().decode(req.getFotoSelfie());
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(fotoSelfie);
            attachment.setTheFilename("FOTO SELFIE" + ".png");
            attachment.setTheContentType(MediaType.IMAGE_PNG_VALUE);
            attachment.setSend(false);
            attachment.setId(idFotoSelfie);
            attachmentsList.add(attachment);
            log.info("### Se subira imagenes INE de cuenta ahorro");
            subeDocumentacionService.subeImagenes(attachmentsList, req.getNumeroCuenta(), regCoDiOBJ.getSolicitanteId());

            List<Map<String, Object>> archivos = new ArrayList<>();
            String valor;
            // Procesar cada ruta de INE
            for (int i = 0; i < req.getRutasIne().size(); i++) {
                String rutaIne = req.getRutasIne().get(i);
                ArchivoOBJ archivoIne = obtenerRutaYNombre(rutaIne);
                Map<String, Object> archivoInfo = new HashMap<>();
                archivoInfo.put("nombre", archivoIne.getNombre());
                archivoInfo.put("id", (i == 0) ? idIneFront : idIneReverso); // Asignar IDs según posición
                archivoInfo.put("ruta", archivoIne.getRuta());
                archivos.add(archivoInfo);
            }

            String modulo = "AHORRO";
            String ruta = regCoDiOBJ.getSolicitanteId() + "/" + modulo + "/" + req.getNumeroCuenta();
            Map<String, Object> archivoInfo = new HashMap<>();
            archivoInfo.put("nombre", "FOTO SELFIE.png");
            archivoInfo.put("id", idFotoSelfie); // Asignar IDs según posición
            archivoInfo.put("ruta", ruta);
            archivos.add(archivoInfo);

            respuesta = actualizarInfo(archivos, req.getNumeroCuenta(), regCoDiOBJ.getSolicitanteId());

            RespuestaUtils.respuestaExito(respuesta, 0, "Información de INE registrada exitosamente.");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO actualizarInfo(List<Map<String, Object>> archivos, String cuenta, String solicitanteId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        RespuestaDTO resp = new RespuestaDTO();
        String valor;
        for (Map<String, Object> archivo : archivos) {
            String nombreArchivo = (String) archivo.get("nombre");
            String rutaArchivo = (String) archivo.get("ruta");
            Integer id = (Integer) archivo.get("id");

            // Realizar el insert para cada archivo
            respuesta = publicProcreaDAO.insertarInfoArch(
                    cuenta,
                    rutaArchivo,
                    nombreArchivo,
                    id,
                    9
            );

            if(respuesta.getCodigo() != 0){
                if (id == idIneFront) { // INE Frontal
                    log.warn("No se pudo registrar la informacion del INE Frontal. Proceso detenido.");
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresImgCtaSimplificada.ERROR_INSERT_INF_INE_FRONTAL,
                            ErroresImgCtaSimplificada.descError.get(ErroresImgCtaSimplificada.ERROR_INSERT_INF_INE_FRONTAL));
                } else if (id == idIneReverso) { // INE Reverso
                    log.warn("No se pudo registrar la informacion del INE Reverso.");
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresImgCtaSimplificada.ERROR_INSERT_INF_INE_REVERSO,
                            ErroresImgCtaSimplificada.descError.get(ErroresImgCtaSimplificada.ERROR_INSERT_INF_INE_REVERSO));
                }
            }
        }

        PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
        valor = "OK";
        if (posGlobCuentaObj != null) {
            if (posGlobCuentaObj.getTipoCuenta().equals("AHORRO")) {
                //Se obtiene el concepto del codigo de autorización en PROCREA
                ConceptoCuentaOBJ conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaProcreaByClave(claveINE, solicitanteId, cuenta);
                if (conceptoCuentaOBJ != null) {
                    //Ya cuenta con el concepto de la INE, hay que actualizarlo
                    conceptosCuentaDAO.actualizarConceptoCuentaProcreaById(conceptoCuentaOBJ.getId(), valor);
                } else if (resp.getCodigo() == -100) {
                    //No cuenta con concepto INE, hay que registrarlo
                    conceptosCuentaDAO.guardarConceptoCuentaProcrea(claveINE, valor, cuenta);
                }
            } else {
                //Se obtiene el concepto del codigo de autorización en CERO
                ConceptoCuentaOBJ conceptoCuentaOBJ = conceptosCuentaDAO.obtenerConceptoCuentaCeroByClave(claveINE, solicitanteId, cuenta);
                if (conceptoCuentaOBJ != null) {
                    //Ya cuenta con el concepto de la INE, hay que actualizarlo
                    conceptosCuentaDAO.actualizarConceptoCuentaCeroById(conceptoCuentaOBJ.getId(), valor);
                } else if (resp.getCodigo() == -100) {
                    //No cuenta con concepto INE, hay que registrarlo
                    conceptosCuentaDAO.guardarConceptoCuentaCero(claveINE, valor, cuenta);
                }
            }
        }
        return respuesta;
    }

    public ArchivoOBJ obtenerRutaYNombre(String rutaCompleta) {
		ArchivoOBJ archivoOBJ = new ArchivoOBJ();
		if (rutaCompleta == null || !rutaCompleta.contains("/")) {
			archivoOBJ.setRuta(rutaCompleta);
			archivoOBJ.setNombre("");
			return archivoOBJ;
		}
	
		int lastSlashIndex = rutaCompleta.lastIndexOf('/');
		archivoOBJ.setRuta(rutaCompleta.substring(0, lastSlashIndex + 1));
		archivoOBJ.setNombre(rutaCompleta.substring(lastSlashIndex + 1));

		log.info("nombre: " + archivoOBJ.getNombre());
		return archivoOBJ;
	}
}
