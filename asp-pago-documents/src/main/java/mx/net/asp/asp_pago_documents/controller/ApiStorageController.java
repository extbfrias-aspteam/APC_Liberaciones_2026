package mx.net.asp.asp_pago_documents.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dto.UploadFileGCSDTO;
import mx.net.asp.asp_pago_documents.service.FileUploadStorageFLService;
import mx.net.asp.asp_pago_documents.utilerias.ApiStorageResponseMapper;
import mx.net.asp.asp_pago_documents.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Log4j2
@Validated
public class ApiStorageController {

    private final FileUploadStorageFLService fileUploadStorageFLService;
    private final StringFormatUtils stringFormatUtils;

    @PostMapping(value = "/biometria-asppago", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadBiometria(
            @RequestParam("idpersona")
            @NotBlank(message = "El campo 'idpersona' es obligatorio")
            @Pattern(regexp = "\\d+", message = "El campo 'idpersona' solo debe contener dígitos numéricos")
            String idpersona,
            @RequestParam(value = "ineFront", required = false)  MultipartFile ineFront,
            @RequestParam(value = "ineRev", required = false)  MultipartFile ineRev,
            @RequestParam(value = "fotoSelfie", required = false)  MultipartFile fotoSelfie
    ) {
        List<UploadFileGCSOBJ> response = fileUploadStorageFLService
                .procesaCargaDocBiometriaClienteExistente("biometria", idpersona, ineFront, ineRev, fotoSelfie);
        //mapeamos la respuesta final
        List<UploadFileGCSDTO> responseDTO = mapResponse(response);
        log.info("response /api/storage/biometria-asppago: {}", stringFormatUtils.toJsonSafe(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }

    private List<UploadFileGCSDTO> mapResponse(List<UploadFileGCSOBJ> responseInterna) {
        return ApiStorageResponseMapper.toResponseDTOList(responseInterna);
    }
}