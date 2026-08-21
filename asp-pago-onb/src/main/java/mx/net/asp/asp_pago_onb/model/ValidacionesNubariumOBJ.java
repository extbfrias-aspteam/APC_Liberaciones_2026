package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionesNubariumOBJ {
    private Integer id;
    private String tipoConsulta;
    private String solicitante;
    private String dato;
    private String resultado;
    private Integer porcentajeCoincidencia;
    private String codigoValidacion;
}
