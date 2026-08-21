package mx.net.asp.asp_pago_onb.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaCurpNubResponse {
    private String estatus;
    private String codigoValidacion;
    private String curp;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String sexo;
    private String fechaNacimiento;
    private String paisNacimiento;
    private String estadoNacimiento;
    private Integer docProbatorio;
    private String estatusCurp;
    private String codigoMensaje;
    private String mensaje;
}
