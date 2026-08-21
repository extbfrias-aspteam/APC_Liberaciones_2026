package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosGlobalCreditoOBJ {
    private Integer id;
    private String estadocr;
    private String productocr;
    private String contratocr;
    private Double saldoinsolutocr;
    private Double montosolicitadocr;
    private String calificacioncr;
    private Integer diasvencidocr;
    private Integer avancecr;
    private Integer disposicioncr;
    private String idpersona;
    private String cuentaClabe;
}
