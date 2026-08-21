package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraspasoEiYuOBJ {
    private Long id;
    private Long idRetiro;
    private Long idDeposito;
    private String cuentaOrigen;
    private String nombreOrdenante;
    private String cuentaDestino;
    private String nombreBeneficiario;
    private Double monto;
    private String conceptoPago;
    private String referenciaNumerica;
    private Integer bancoOrigen;
    private String fhOperacion;
    private String cveRastreo;
    private String estatus;
    private String idTipoPago;
    private String observaciones;
    private Integer intentos;
    private String tipoCuentaAh;
    private Double longitud;
    private Double latitud;
}
