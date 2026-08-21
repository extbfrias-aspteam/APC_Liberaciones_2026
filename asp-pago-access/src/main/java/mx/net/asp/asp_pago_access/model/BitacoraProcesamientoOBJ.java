package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraProcesamientoOBJ {
    private Integer id;
    private String cuenta;
    private Integer operacionId;
    private Long fechaInicio;
    private Long fechaFin;
    private Integer idProcesamiento;
}
