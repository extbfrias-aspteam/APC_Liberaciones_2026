package mx.net.asp.asp_pago_payments.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_payments.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstablecimientosReq {
    private String IdCliente;
    private String tipodelPago;
    private String ctaDebit;
    private String nemEmp;
    private String SubEmp;
    private String numMed;
    private String fecVen;
    private String saldoPagar;
    private String codigo;
    private String codSeg;
    private String idBanc;
    private String ctaCheq;
    private String consCheq;
    private String montoCheq;
    private String tipoPago;
    private String factura;
    private String codBarra;
    private String terminalId;
    private String transaccionId;
    private String tarjetaId;
    private String traza;
    private String monto;
    private String montoCs;
    private String tipoTrans;
    private String carrier;
    private String saldoCom;
    private String cuenta;
    private HeaderWS header;
}
