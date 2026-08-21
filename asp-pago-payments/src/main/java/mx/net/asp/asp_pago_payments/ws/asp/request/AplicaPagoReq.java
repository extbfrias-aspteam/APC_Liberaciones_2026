package mx.net.asp.asp_pago_payments.ws.asp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_payments.model.HeaderWS;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AplicaPagoReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    private String referencia;
    private String cuenta;
    private Double monto;
    private Integer banco;
    private String transaccionId;
    private String tarjetaId;
    private Integer app;
    private Integer formaPago;
    private Boolean validarImportacion;
    private Integer importacionId;
    private Integer canalId;
    private String observaciones;
    private Date fecha;
    private Integer aplicaImportacion;
}
