package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroServiciosDigitalesOBJ {
    private Integer id;
    private String cuenta;
    private String usuario;
    private String password;
    private String folioContrato;
    private Integer estatus;
    private String nombreDocumento;
    private String alfrescoId;
    private Integer tipoArchivoId;
}
