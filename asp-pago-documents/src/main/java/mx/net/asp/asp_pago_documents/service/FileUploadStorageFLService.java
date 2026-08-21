package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.ApiException;
import mx.net.asp.asp_pago_documents.config.FileStorageProperties;
import mx.net.asp.asp_pago_documents.dao.SeguridadDAO;
import mx.net.asp.asp_pago_documents.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.DocumentacionAspPagoOBJ;
import mx.net.asp.asp_pago_documents.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.EventoArchivo;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresStorage;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import mx.net.asp.asp_pago_documents.ws.asp.WSSubeArchivosBucketOCR;
import mx.net.asp.asp_pago_documents.ws.asp.response.UploadBucketOCRResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileUploadStorageFLService {

    private final FileStorageProperties fileStorageProperties;
    private final GCSAsyncUploadService gcsAsyncUploadService;
    private final WSSubeArchivosBucketOCR wsSubeArchivosBucket;
    private final SolicitanteDAO solicitanteDAO;
    private final SeguridadDAO seguridadDAO;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${local.storage.files.path}")
    private String localStorageFiles;
    @Value("${files.fallback.path}")
    private String filesFallback;

    @Value("${request.type.bucket.ocr}")
    private String requestType;
    @Value("${storage.option.bucket.ocr}")
    private String storageOption;

    public List<UploadFileGCSOBJ> procesaCargaDocBiometriaClienteExistente(
            String categoria,
            String identificador,
            MultipartFile ineFront,
            MultipartFile ineRev,
            MultipartFile fotoSelfie
    ) {
        //procesar buscar al cliente en caso de no existir, devoler error.
        SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(identificador);
        if (solicitanteOBJ == null) {
            throw new ApiException(
                    ErroresStorage.SOLICITANTE_NOT_FOUND,
                    ErroresStorage.descError.get(ErroresStorage.SOLICITANTE_NOT_FOUND)
            );
        }

        validarDocPendiente(solicitanteOBJ.getNumero());

        List<UploadFileGCSOBJ> respuesta = uploadFilesBiometriaAspPagoLocalFirst(categoria, solicitanteOBJ.getCurp(), ineFront, ineRev, fotoSelfie, "POST-LOGIN");

        //actualizamos la tabla de clientes pendientes de documentacion
        try {
            seguridadDAO.actualizarRegistroDocPendienteAspPago(solicitanteOBJ.getNumero());
        } catch (ApiException e) {
            if (e.getCodigo() == ErroresStorage.ERROR_ACTUALIZAR_DOCUMENTACION_PENDIENTE) {
                //Catch rollback local files
                for (UploadFileGCSOBJ file : respuesta) {
                    Path path = Paths.get(file.getObjectName());
                    rollbackArchivoLocal(path, file);
                }
            }
            throw e;
        }

        uploadFilesBiometriaAspPagoBucket(respuesta.get(0), ineFront.getContentType());
        uploadFilesBiometriaAspPagoBucket(respuesta.get(1), ineRev.getContentType());
        uploadFilesBiometriaAspPagoBucket(respuesta.get(2), fotoSelfie.getContentType());

        return respuesta;
    }

    public List<UploadFileGCSOBJ> procesaCargaDocBiometriaClienteNuevo(
            String categoria,
            String identificador,
            MultipartFile ineFront,
            MultipartFile ineRev,
            MultipartFile fotoSelfie,
            String origen
    ) {
        List<UploadFileGCSOBJ> respuesta = uploadFilesBiometriaAspPagoLocalFirst(categoria, identificador, ineFront, ineRev, fotoSelfie, origen);

        uploadFilesBiometriaAspPagoBucket(respuesta.get(0), ineFront.getContentType());
        uploadFilesBiometriaAspPagoBucket(respuesta.get(1), ineRev.getContentType());
        uploadFilesBiometriaAspPagoBucket(respuesta.get(2), fotoSelfie.getContentType());

        return respuesta;
    }

    public List<UploadFileGCSOBJ> uploadFilesBiometriaAspPagoLocalFirst(
            String categoria,
            String identificador,
            MultipartFile ineFront,
            MultipartFile ineRev,
            MultipartFile fotoSelfie,
            String origen
    ) {

        validarArchivoObligatorio(ineFront, "ineFront", categoria);
        validarArchivoObligatorio(ineRev, "ineRev", categoria);
        validarArchivoObligatorio(fotoSelfie, "fotoSelfie", categoria);

        //Implementacion bucket ASTORGA
        log.info("Se consume servicio OCR BUCKET");
        MultipartFile[] files = new MultipartFile[] { ineFront, ineRev };
        RespuestaDTO respuesta = wsSubeArchivosBucket.subeArchivosBucket(requestType, storageOption, files);
        if (respuesta.getCodigo() != 0 && respuesta.getCodigo() != 400) {
            throw new ApiException(
                    respuesta.getCodigo(),
                    respuesta.getMensaje()
            );
        }

        UploadBucketOCRResponse uploadBucketResponse = gson.fromJson(respuesta.getData(), UploadBucketOCRResponse.class);
        if (!"OK".equalsIgnoreCase(uploadBucketResponse.getMessage())) {
            throw new ApiException(
                    -100,
                    uploadBucketResponse.getMessage()
            );
        }

        List<UploadFileGCSOBJ> respuestas = new ArrayList<>();

        UploadFileGCSOBJ frontLocal = guardarArchivoEnContenedorLocalFirst(categoria, identificador, ineFront, origen);
        UploadFileGCSOBJ revLocal = guardarArchivoEnContenedorLocalFirst(categoria, identificador, ineRev, origen);
        UploadFileGCSOBJ selfieLocal = guardarArchivoEnContenedorLocalFirst(categoria, identificador, fotoSelfie, origen);

        respuestas.add(frontLocal);
        respuestas.add(revLocal);
        respuestas.add(selfieLocal);

        return respuestas;
    }

    public void uploadFilesBiometriaAspPagoBucket(
            UploadFileGCSOBJ fileLocal,
            String contentType
    ) {
        gcsAsyncUploadService.subirArchivoLocalGCSAsync(fileLocal, contentType);
    }

    public List<UploadFileGCSOBJ> uploadFileListAspPagoLocalFirst(
            String categoria,
            String identificador,
            List<MultipartFile> files,
            String origen
    ) {
        validarListaArchivos(files, categoria);

        List<UploadFileGCSOBJ> respuestas = new ArrayList<>();
        for (MultipartFile file : files) {
            validarArchivoObligatorio(file, "files", categoria);
            UploadFileGCSOBJ response = guardarArchivoEnContenedorLocalFirst(categoria, identificador, file, origen);
            respuestas.add(response);
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            UploadFileGCSOBJ response = respuestas.get(i);
            gcsAsyncUploadService.subirArchivoLocalGCSAsync(response, file.getContentType());
        }

        return respuestas;
    }

    private UploadFileGCSOBJ guardarArchivoEnContenedorLocalFirst(
            String categoria,
            String identificador,
            MultipartFile file,
            String origen
    ) {
        log.info("++++ INICIA CARGA DE ARCHIVO LOCAL ++++");

        String categoriaNormalizada = normalizarSegmento(categoria);
        String identificadorNormalizado = normalizarIdentificador(identificador, categoria);
        String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "archivo");
        String safeFileName = sanitizarNombreArchivo(originalFilename);

        try {

            Path directorio = Paths.get(
                    localStorageFiles + filesFallback,
                    categoriaNormalizada,
                    identificadorNormalizado
            );
            Files.createDirectories(directorio);

            String nombreArchivo = UUID.randomUUID() + "-" + safeFileName;
            Path rutaArchivo = directorio.resolve(nombreArchivo);

            file.transferTo(rutaArchivo.toFile());

            log.info("Archivo guardado localmente en {}", rutaArchivo);

            UploadFileGCSOBJ response = new UploadFileGCSOBJ();
            response.setMessage("Archivo guardado con éxito.");
            response.setObjectName(rutaArchivo.toString());
            response.setFileName(nombreArchivo);
            response.setCategoria(categoriaNormalizada);
            response.setIdentificador(identificadorNormalizado);
            response.setStorage("LOCAL");

            //registrar en tabla carga archivos
            DocumentacionAspPagoOBJ doc = crearDocAspPagoObj(
                    response.getCategoria(),
                    response.getIdentificador(),
                    response.getFileName(),
                    response.getObjectName(),
                    "LOCAL",
                    EventoArchivo.GUARDADO_LOCAL.name(),
                    origen
            );

            try {
                Long idStorage = seguridadDAO.guardarRegistroDocAspPago(doc);
                response.setId_storage(idStorage);
                //registrar en tabla de historico el guardado local del archivo
                seguridadDAO.registraLogDocumentacionAspPago(idStorage, response.getObjectName(), EventoArchivo.GUARDADO_LOCAL.name(), "OK");
            } catch (ApiException ex) {
                if (ex.getCodigo() == ErroresStorage.ERROR_GUARDADO_ARCHIVO_BD) {
                    //rollback
                    rollbackArchivoLocal(rutaArchivo, response);
                }
                throw ex;
            }

            log.info("++++ FINALIZA CARGA DE ARCHIVO LOCAL ++++");

            return response;

        } catch (AccessDeniedException e) {
            log.error("Acceso denegado al guardar archivo: ", e);
            errorHandler.handleException(e);

            throw new ApiException(
                    ErroresStorage.ERROR_PERMISOS_ARCHIVO,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_PERMISOS_ARCHIVO)
            );

        } catch (IOException e) {
            log.error("Error de entrada/salida al guardar archivo: ", e);
            errorHandler.handleException(e);

            throw new ApiException(
                    ErroresStorage.ERROR_GUARDADO_ARCHIVO,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_GUARDADO_ARCHIVO)
            );

        } catch (SecurityException e) {
            log.error("Error de seguridad al guardar archivo: ", e);
            errorHandler.handleException(e);

            throw new ApiException(
                    ErroresStorage.ERROR_PERMISOS_ARCHIVO,
                    ErroresStorage.descError.get(ErroresStorage.ERROR_PERMISOS_ARCHIVO)
            );
        }
    }

    private void rollbackArchivoLocal(Path pathArchivo, UploadFileGCSOBJ archivo) {
        try {
            boolean archivoEliminado = Files.deleteIfExists(pathArchivo);
            if (archivoEliminado) {
                log.info("Se hace rollback del archivo ya que no se pudo registrar en bd: {}", pathArchivo);
            } else {
                log.warn("El archivo ya no existía al intentar rollback: {}", pathArchivo);
            }

            eliminarCarpetasVaciasHastaBase(pathArchivo.getParent(), archivo.getCategoria());

        } catch (IOException e) {
            log.error("No se pudo hacer rollback del archivo: {}", pathArchivo, e);
            errorHandler.handleException(e);
        }
    }

    private void eliminarCarpetasVaciasHastaBase(Path carpetaActual, String categoria) {
        if (carpetaActual == null) {
            return;
        }

        while (carpetaActual != null && !esDirectorioBase(carpetaActual, categoria)) {
            try {
                if (!Files.exists(carpetaActual)) {
                    carpetaActual = carpetaActual.getParent();
                    continue;
                }

                try (Stream<Path> stream = Files.list(carpetaActual)) {
                    if (stream.findAny().isEmpty()) {
                        boolean eliminada = Files.deleteIfExists(carpetaActual);
                        if (eliminada) {
                            log.info("Carpeta eliminada por estar vacía: {}", carpetaActual);
                        } else {
                            log.debug("La carpeta ya no existía al intentar eliminarla: {}", carpetaActual);
                        }
                        carpetaActual = carpetaActual.getParent();
                    } else {
                        log.debug("La carpeta no se eliminó porque aún contiene archivos o subcarpetas: {}", carpetaActual);
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("No se pudo eliminar la carpeta: {}", carpetaActual, e);
                break;
            }
        }
    }

    private boolean esDirectorioBase(Path path, String categoria) {
        String nombre = path.getFileName().toString();
        return categoria.equalsIgnoreCase(nombre);
    }

    public void validarDocPendiente(String idpersona) {
        boolean existePendiente = seguridadDAO.existeDocPendienteAspPago(idpersona);

        if (!existePendiente) {
            throw new ApiException(
                    ErroresStorage.CLIENTE_YA_TIENE_DOCUMENTACION_COMPLETA,
                    ErroresStorage.descError.get(ErroresStorage.CLIENTE_YA_TIENE_DOCUMENTACION_COMPLETA)
            );
        }
    }

    private void validarListaArchivos(List<MultipartFile> files, String tipo) {
        if (files == null || files.isEmpty()) {
            throw new ApiException(
                    ErroresStorage.ARCHIVO_OBLIGATORIO,
                    "Debe enviar al menos un archivo"
            );
        }
    }


    private void validarArchivoObligatorio(MultipartFile file, String nombreCampo, String categoria) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(
                    ErroresStorage.ARCHIVO_OBLIGATORIO,
                    "El archivo '" + nombreCampo + "' es obligatorio"
            );
        }

        log.info("Archivo recibido: nombre = {} tamano = {} bytes",
                file.getOriginalFilename(), file.getSize());

        String contentType = Optional.ofNullable(file.getContentType())
                .map(String::toLowerCase)
                .orElse("");

        Set<String> permitidos = fileStorageProperties.getAllowedContentTypes().get(categoria);

        if (permitidos == null || !permitidos.contains(contentType)) {
            log.error("Tipo de archivo no permitido para {}::  {}",
                    categoria, contentType);

            throw new ApiException(
                    ErroresStorage.TIPO_ARCHIVO_NO_PERMITIDO,
                    ErroresStorage.getMensajeError(ErroresStorage.TIPO_ARCHIVO_NO_PERMITIDO, contentType)
            );
        }
    }

    private String normalizarSegmento(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized.trim()
                .toLowerCase()
                .replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private String normalizarIdentificador(String value, String categoria) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        if (categoria.equals("contratos")) {
            return normalized.trim()
                    .toLowerCase()
                    .replaceAll("[^a-zA-Z0-9_\\-/]", "_");
        } else {
            return normalized.trim()
                    .toLowerCase()
                    .replaceAll("[^a-zA-Z0-9_-]", "_");
        }
    }

    private String sanitizarNombreArchivo(String filename) {
        String normalized = Normalizer.normalize(filename, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static DocumentacionAspPagoOBJ crearDocAspPagoObj(
            String categoria,
            String identificador,
            String nombreArchivo,
            String rutaArchivo,
            String storageTipo,
            String estado,
            String origen
    ) {
        DocumentacionAspPagoOBJ obj = new DocumentacionAspPagoOBJ();

        obj.setCategoria(categoria);
        obj.setIdentificador(identificador);
        obj.setNombreArchivo(nombreArchivo);
        obj.setRutaArchivo(rutaArchivo);
        obj.setStorageTipo(storageTipo);
        obj.setEstado(estado);
        obj.setOrigen(origen);

        return obj;
    }
}