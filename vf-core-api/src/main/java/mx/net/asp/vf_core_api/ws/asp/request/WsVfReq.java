package mx.net.asp.vf_core_api.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsVfReq {
    private String usuarioAdmin;
    private String passwordAdmin;
    private String tokenAdmin;
    private String tipoOperacion;
    private String referencia;
    private String nip;
    private String tokenCliente;
    private Double monto;
    private String transaccionId;
    private Integer tipoDispositivo;
    private ProspectoOBJ prospecto;
    private String tokenCliente2;
    private String celular;
    private String celular2;
    private String consCheque;
}
