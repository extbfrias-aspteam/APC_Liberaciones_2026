package mx.net.asp.asp_pago_onb.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaPldReq {
    private String claveAplicativo;
    private String idPersona;
    private String tipoPersona;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private String rfc;
    private String paisResidencia;
    private Long idUsuario;
    private String claveUsuario;
}
