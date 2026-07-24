package mx.net.asp.asp_pago_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {
    private String cuenta;
    private String clabe;
    private Integer productoId;
}
