package mx.net.asp.asp_pago_documents.controller;

import com.google.gson.Gson;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.dto.UploadFileGCSDTO;
import mx.net.asp.asp_pago_documents.service.BucketOCRService;
import mx.net.asp.asp_pago_documents.service.FileUploadStorageFLService;
import mx.net.asp.asp_pago_documents.utilerias.ApiStorageResponseMapper;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import mx.net.asp.asp_pago_documents.ws.asp.response.ValidatorOCRResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/onb/storage")
@RequiredArgsConstructor
@Log4j2
@Validated
public class ApiOnbStorageController {

    private final BucketOCRService bucketOCRService;
    private final FileUploadStorageFLService fileUploadStorageFLService;
    private final StringFormatUtils stringFormatUtils;
    private final ErrorHandler errorHandler;

    @PostMapping(value = "/obtenerDatosOcr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> subirArchivosSFTP(
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        // Declaracion de variable para la respuesta
        RespuestaDTO respuesta = new RespuestaDTO();
        // Declaracion de variable para la respuesta 200
        ValidatorOCRResponse response;
        Gson gson = new Gson();
        try {
            respuesta = bucketOCRService.procesaArchivosBucket(files);
            if (respuesta.getCodigo() == 0) {
                response =  gson.fromJson(respuesta.getData(), ValidatorOCRResponse.class);
                log.info("respuesta /api/onb/obtenerDatosOcr {}", gson.toJson(response));
                return ResponseEntity.ok(response);
            } else {
                log.info("respuesta /api/onb/obtenerDatosOcr {}", gson.toJson(respuesta));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    @PostMapping(value = "/biometria-asppago", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadBiometria(
            @RequestParam
            @NotBlank(message = "El campo 'curp' es obligatorio")
            @Pattern(regexp = "^[A-Za-z]{4}\\d{6}[HM][A-Za-z]{5}[A-Za-z0-9]\\d$", message = "El CURP no tiene un formato válido")
            String curp,
            @RequestParam(value = "ineFront", required = false)  MultipartFile ineFront,
            @RequestParam(value = "ineRev", required = false)  MultipartFile ineRev,
            @RequestParam(value = "fotoSelfie", required = false)  MultipartFile fotoSelfie
    ) {
        List<UploadFileGCSOBJ> response = fileUploadStorageFLService
                .procesaCargaDocBiometriaClienteNuevo("biometria", curp, ineFront, ineRev, fotoSelfie, "ONBOARDING");
        //mapeamos la respuesta final
        List<UploadFileGCSDTO> responseDTO = mapResponse(response);
        log.info("response /api/onb/biometria-asppago: {}", stringFormatUtils.toJsonSafe(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }

    private List<UploadFileGCSDTO> mapResponse(List<UploadFileGCSOBJ> responseInterna) {
        return ApiStorageResponseMapper.toResponseDTOList(responseInterna);
    }
}
