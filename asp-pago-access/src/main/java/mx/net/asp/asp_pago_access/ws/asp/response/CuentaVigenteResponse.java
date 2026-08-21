package mx.net.asp.asp_pago_access.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaVigenteResponse {
    private String cuenta;
    private String clabe;
    private Integer productoId;
}
