package mx.net.asp.asp_pago_documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.SeguridadDAO;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.EventoArchivo;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class GCSAsyncUploadService {

    private final GoogleCloudStorageService googleCloudStorageService;
    private final SeguridadDAO seguridadDAO;
    private final ErrorHandler errorHandler;

    @Value("${gcp.base.path.bucket}")
    private String basePath;

    @Async
    public CompletableFuture<Void> subirArchivoLocalGCSAsync(
            UploadFileGCSOBJ archivoLocal,
            String contentType
    ) {
        log.info("++++ INICIA CARGA DE ARCHIVO A BUCKET ++++");

        Path path = Paths.get(archivoLocal.getObjectName());

        try {
            if (!Files.exists(path)) {
                log.warn("No existe el archivo local para subida asincrona: {}", path);
                return CompletableFuture.completedFuture(null);
            }

            UploadFileGCSOBJ response = googleCloudStorageService.uploadLocalFileToGCS(
                    archivoLocal.getId_storage(),
                    basePath,
                    archivoLocal.getCategoria(),
                    archivoLocal.getIdentificador(),
                    path,
                    archivoLocal.getFileName(),
                    contentType
            );

            log.info("Archivo local subido correctamente a GCS desde {}", path);

            log.info("++++ FINALIZA CARGA DE ARCHIVO A BUCKET ++++");

            //Actualizamos en  en tabla carga archivos
            seguridadDAO.actualizarRegistroDocAspPago(response.getId_storage(), response.getObjectName(), EventoArchivo.GUARDADO_BUCKET.name(), response.getStorage());
            //registrar en tabla de historico carga archivos: cargado en bucket
            seguridadDAO.registraLogDocumentacionAspPago(response.getId_storage(), response.getObjectName(), EventoArchivo.GUARDADO_BUCKET.name(), "OK");
            eliminarPathLocal(path, response);
        } catch (Exception e) {
            //Actualizamos en  en tabla carga archivos
            seguridadDAO.actualizarRegistroDocAspPago(archivoLocal.getId_storage(), archivoLocal.getObjectName(), EventoArchivo.ERROR_SUBIDA_BUCKET.name(), archivoLocal.getStorage());
            //registrar en tabla de historico carga archivos: ERROR al cargar en bucket
            seguridadDAO.registraLogDocumentacionAspPago(archivoLocal.getId_storage(), archivoLocal.getObjectName(), EventoArchivo.ERROR_SUBIDA_BUCKET.name(), e.getMessage());
            log.error("Falló la subida asíncrona a GCS del archivo local {}", path, e);
            errorHandler.handleException(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void eliminarPathLocal(Path pathArchivo, UploadFileGCSOBJ archivo) {
        try {
            Files.deleteIfExists(pathArchivo);
            log.info("Archivo local eliminado tras subida exitosa a GCS: {}", pathArchivo);
            //registrar en tabla de historico carga archivos: eliminacion de archivo local
            seguridadDAO.registraLogDocumentacionAspPago(archivo.getId_storage(), archivo.getObjectName(), EventoArchivo.ELIMINACION_LOCAL_EXITOSA.name(), "OK");

            eliminarCarpetasVaciasHastaBase(pathArchivo.getParent(), archivo.getCategoria());
        } catch (IOException e) {
            log.error("No se pudo eliminar archivo local después de subir a GCS: {}", pathArchivo);
            seguridadDAO.registraLogDocumentacionAspPago(archivo.getId_storage(), archivo.getObjectName(), EventoArchivo.ERROR_ELIMINACION_LOCAL.name(), e.getMessage());
            //registrar en tabla de historico carga archivos: ERROR en eliminacion de archivo local
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
}