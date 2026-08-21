package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoOBJ {
    private String tipoTarjeta;
    private String descripcion;
    private Double costo;
}
