package mx.net.asp.asp_pago_services_v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitanteOBJ {
    private String numero;
    private String nombre;
    private String curp;
    private String rfc;
    private String correo;
    private String telefonoCoDi;
    private String cuenta;
}
