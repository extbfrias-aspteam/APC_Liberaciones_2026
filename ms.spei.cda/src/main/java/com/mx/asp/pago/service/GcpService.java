package com.mx.asp.pago.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.enums.EstatusResponse;
import com.mx.asp.pago.record.CertificadoFiles;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GcpService implements IGcpService {

	private final Storage storage = StorageOptions.getDefaultInstance().getService();

	@Override
	public Response<?> getCertificadoFiles(String bucketName, String id) {

		if (bucketName == null || bucketName.isBlank()) {
			log.error("BucketName viene null o vacío");
			return Response.builder().estatus(EstatusResponse.error).descripcion("Bucket no configurado").build();
		}

		String prefix = "certificados_participantes/" + id + "/";

		log.info("Buscando archivos en bucket: {} con prefix: {}", bucketName, prefix);

		File cerFile = null;
		File keyFile = null;
		File txtFile = null;

		try {

			Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(prefix));

			int contador = 0;

			for (Blob blob : blobs.iterateAll()) {

				if (blob.isDirectory() || blob.getSize() == 0) {
					continue;
				}

				contador++;
				log.info("Blob encontrado: {}", blob.getName());

				String name = blob.getName().toLowerCase();

				if (name.endsWith(".cer")) {
					cerFile = blobToTempFile(blob, ".cer");
				} else if (name.endsWith(".key")) {
					keyFile = blobToTempFile(blob, ".key");
				} else if (name.endsWith(".txt")) {
					txtFile = blobToTempFile(blob, ".txt");
				}
			}

			log.info("Total blobs encontrados: {}", contador);

		} catch (StorageException e) {
			log.error("Error accediendo a GCS. Código: {} - Mensaje: {}", e.getCode(), e.getMessage(), e);

			return Response.builder().estatus(EstatusResponse.error)
					.descripcion("Error al acceder al bucket: posible problema de permisos").build();
		} catch (Exception e) {
			log.error("Error inesperado al leer bucket", e);

			return Response.builder().estatus(EstatusResponse.error).descripcion("Error interno al procesar archivos")
					.build();
		}

		if (cerFile == null || keyFile == null || txtFile == null) {

			log.warn("No se encontraron todos los documentos requeridos. cer: {}, key: {}, txt: {}", cerFile != null,
					keyFile != null, txtFile != null);

			return Response.builder().estatus(EstatusResponse.error)
					.descripcion("Documentos no encontrados en el bucket")
					.resultado(new CertificadoFiles(cerFile, keyFile, txtFile)).build();
		}

		log.info("Archivos descargados correctamente para id: {}", id);

		return Response.builder().estatus(EstatusResponse.ok).descripcion("Documentos descargados correctamente")
				.resultado(new CertificadoFiles(cerFile, keyFile, txtFile)).build();
	}

	private File blobToTempFile(Blob blob, String extension) {

		try {
			Path tempFile = Files.createTempFile("gcs-", extension);
			Files.write(tempFile, blob.getContent());

			log.info("Archivo temporal creado: {}", tempFile.toAbsolutePath());

			return tempFile.toFile();

		} catch (IOException e) {
			throw new RuntimeException("Error al crear archivo temporal para " + blob.getName(), e);
		}
	}
}