package mx.net.asp.asp_pago_documents.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_documents.ws.asp.WSSubeArchivosBucketOCR;
import mx.net.asp.asp_pago_documents.ws.asp.request.ValidatorOCRFileItem;
import mx.net.asp.asp_pago_documents.ws.asp.request.ValidatorOCRReq;
import mx.net.asp.asp_pago_documents.ws.asp.response.UploadBucketOCRFile;
import mx.net.asp.asp_pago_documents.ws.asp.response.UploadBucketOCRResponse;
import mx.net.asp.asp_pago_documents.ws.asp.response.ValidatorOCRResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BucketOCRService {

    private final WSSubeArchivosBucketOCR wsSubeArchivosBucket;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${request.id.bucket.ocr}")
    private String requestId;
    @Value("${request.source.bucket.ocr}")
    private String requestSource;
    @Value("${request.type.bucket.ocr}")
    private String requestType;
    @Value("${storage.option.bucket.ocr}")
    private String storageOption;

    public RespuestaDTO procesaArchivosBucket(MultipartFile[] files) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = subirArchivosBucket(requestType, storageOption, files);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            UploadBucketOCRResponse uploadBucketResponse = gson.fromJson(respuesta.getData(), UploadBucketOCRResponse.class);

            if (uploadBucketResponse == null || uploadBucketResponse.getFileList() == null || uploadBucketResponse.getFileList().isEmpty()) {
                throw new IllegalArgumentException("La respuesta del bucket no contiene archivos");
            }

            ValidatorOCRReq validatorOCRReq = construirValidatorOCRReq(
                    uploadBucketResponse,
                    requestId,
                    "ine"
            );

            respuesta = obtenerOcrBucket(validatorOCRReq);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO subirArchivosBucket(String requestType, String storageOption, MultipartFile[] files) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (files == null || files.length == 0) {
                respuesta.setCodigo(-300);
                respuesta.setMensaje("Faltan datos obligatorios.");
                respuesta.setData("[\"files\"]");
                return respuesta;
            }
            for (MultipartFile archivo : files) {
                log.info("Archivo recibido: nombre = {} tamano = {} bytes", archivo.getOriginalFilename(), archivo.getSize());
            }

            respuesta = wsSubeArchivosBucket.subeArchivosBucket(requestType, storageOption, files);
            if (respuesta.getCodigo() != 0 && respuesta.getCodigo() != 400)
                return respuesta;

            UploadBucketOCRResponse uploadBucketResponse = gson.fromJson(respuesta.getData(), UploadBucketOCRResponse.class);
            if (!"OK".equalsIgnoreCase(uploadBucketResponse.getMessage())) {
                return RespuestaUtils.asignarError(respuesta,
                        -100,
                        uploadBucketResponse.getMessage());
            }else
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(uploadBucketResponse));
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerOcrBucket(ValidatorOCRReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsSubeArchivosBucket.obtenerOcrBucket(req);
            if (respuesta.getCodigo() != 0 && respuesta.getCodigo() != 400)
                return respuesta;

            ValidatorOCRResponse validatorOCRResponse = gson.fromJson(respuesta.getData(), ValidatorOCRResponse.class);
            if (!"OK".equalsIgnoreCase(validatorOCRResponse.getMessage())) {
                return RespuestaUtils.asignarError(respuesta,
                        -100,
                        validatorOCRResponse.getMessage());
            } else
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(validatorOCRResponse));
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private ValidatorOCRReq construirValidatorOCRReq(
            UploadBucketOCRResponse uploadBucketResponse,
            String requestId,
            String documentType
    ) {
        List<ValidatorOCRFileItem> fileList = new ArrayList<>();

        for (UploadBucketOCRFile file : uploadBucketResponse.getFileList()) {
            ValidatorOCRFileItem item = new ValidatorOCRFileItem();
            item.setFileUri(file.getFileUri());
            item.setMimeType(file.getMimeType());
            fileList.add(item);
        }

        ValidatorOCRReq req = new ValidatorOCRReq();
        req.setRequestType("validator");
        req.setRequestId(requestId);
        req.setDocumentType(documentType);
        req.setRequestSource(requestSource);
        req.setFileList(fileList);

        return req;
    }
}
