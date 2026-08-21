package mx.net.asp.asp_pago_documents.utilerias;

import mx.net.asp.asp_pago_documents.dto.UploadFileGCSDTO;
import mx.net.asp.asp_pago_documents.model.UploadFileGCSOBJ;

import java.util.List;

public class ApiStorageResponseMapper {

    private ApiStorageResponseMapper() {
    }

    public static UploadFileGCSDTO toResponseDTO(UploadFileGCSOBJ obj) {
        UploadFileGCSDTO dto = new UploadFileGCSDTO();
        dto.setMessage(obj.getMessage());
        dto.setFileName(obj.getFileName());
        dto.setStorage(obj.getStorage());
        return dto;
    }

    public static List<UploadFileGCSDTO> toResponseDTOList(List<UploadFileGCSOBJ> list) {
        return list.stream()
                .map(ApiStorageResponseMapper::toResponseDTO)
                .toList();
    }
}
