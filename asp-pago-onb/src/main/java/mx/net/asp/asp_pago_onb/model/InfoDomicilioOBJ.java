package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoDomicilioOBJ {
    private String colonia;
    private String cp;
    private String localidad;
    private String municipio;
    private String estado;
}
