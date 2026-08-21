package mx.net.asp.asp_pago_documents.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.ApiException;
import mx.net.asp.asp_pago_documents.config.GcpStorageProperties;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresStorage;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class GoogleCloudStorageService {

    private final Storage storage;
    private final GcpStorageProperties gcpStorageProperties;
    private final ErrorHandler errorHandler;

    public UploadFileGCSOBJ uploadLocalFileToGCS(
            Long id_storage,
            String basePath,
            String categoria,
            String identificador,
            Path localFilePath,
            String fileName,
            String contentType
    ) {
        validarEntradaLocalFile(categoria, identificador, localFilePath);

        String categoriaNormalizada = normalizarSegmento(categoria);
        String identificadorNormalizado = normalizarIdentificador(identificador, categoria);
        String safeFileName = sanitizarNombreArchivo(
                Objects.requireNonNullElse(fileName, localFilePath.getFileName().toString())
        );

        String objectName = basePath
                + "/"
                + categoriaNormalizada
                + "/"
                + identificadorNormalizado
                + "/"
                + safeFileName;

        BlobId blobId = BlobId.of(gcpStorageProperties.getBucketName(), objectName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(contentType)
                .build();

        try {
            byte[] bytes = Files.readAllBytes(localFilePath);
            storage.create(blobInfo, bytes);
        } catch (IOException e) {
            log.error("Ocurrió un error al leer el archivo local {}", localFilePath, e);
            errorHandler.handleException(e);
            throw new ApiException(
                    ErroresStorage.ERROR_LECTURA_ARCHIVO,
                    ErroresStorage.getMensajeError(ErroresStorage.ERROR_LECTURA_ARCHIVO, safeFileName)
            );
        } catch (StorageException e) {
            log.error("Ocurrió un error al subir el archivo local a Google Cloud Storage", e);
            errorHandler.handleException(e);
            throw new ApiException(
                    ErroresStorage.ERROR_SUBIDA_STORAGE,
                    ErroresStorage.getMensajeError(ErroresStorage.ERROR_SUBIDA_STORAGE, safeFileName)
            );
        }

        log.info("Archivo {} subido correctamente a GCS:: {}", safeFileName, objectName);

        return new UploadFileGCSOBJ(
                id_storage,
                "Archivo subido correctamente",
                objectName,
                safeFileName,
                categoriaNormalizada,
                identificadorNormalizado,
                "BUCKET"
        );
    }

    private void validarEntradaLocalFile(String categoria, String identificador, Path localFilePath) {
        if (categoria == null || categoria.isBlank()) {
            throw new ApiException(
                    ErroresStorage.CATEGORIA_OBLIGATORIA,
                    ErroresStorage.getMensajeError(ErroresStorage.CATEGORIA_OBLIGATORIA)
            );
        }

        if (identificador == null || identificador.isBlank()) {
            throw new ApiException(
                    ErroresStorage.IDENTIFICADOR_OBLIGATORIO,
                    ErroresStorage.getMensajeError(ErroresStorage.IDENTIFICADOR_OBLIGATORIO)
            );
        }

        if (localFilePath == null || !Files.exists(localFilePath)) {
            throw new ApiException(
                    ErroresStorage.ARCHIVO_OBLIGATORIO,
                    ErroresStorage.getMensajeError(ErroresStorage.ARCHIVO_OBLIGATORIO)
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
}