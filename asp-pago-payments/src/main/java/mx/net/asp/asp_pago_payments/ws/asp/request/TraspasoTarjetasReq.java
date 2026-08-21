package mx.net.asp.asp_pago_payments.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_payments.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraspasoTarjetasReq {
    private HeaderWS header;
    private Double importe;
    private String numeroTarjetaOrigen;
    private String numeroTarjetaDestino;
    private String observaciones;
    private String refNumerica;
    private String token;
    private String claveMovimiento;
}
