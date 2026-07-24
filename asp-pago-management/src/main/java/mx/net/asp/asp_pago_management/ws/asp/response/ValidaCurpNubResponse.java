package mx.net.asp.asp_pago_management.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaCurpNubResponse {
    private String apellidoMaterno;
    private String apellidoPaterno;
    private String codigoMensaje;
    private String codigoValidacion;
    private String curp;
    private Integer docProbatorio;
    private String estadoNacimiento;
    private String estatus;
    private String estatusCurp;
    private String fechaNacimiento;
    private String nombre;
    private String paisNacimiento;
    private String sexo;
    private String mensaje;
}
