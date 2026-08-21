package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroPagareOBJ {
    private Integer PagareId;
    private String cuenta;
    private Long numero;
    private Double monto;
    private Double intereses;
    private Date fechaInicio;
    private Date fechaFinal;
    private Integer creadoPor;
    private Date fechaCreacion;
    private Integer modificadoPor;
    private Date fechaModificacion;
}
