package mx.net.asp.asp_pago_payments.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_payments.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesaReq {
    private String Proceso;
    private String Tipo;
    @Valid
    @NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @Valid
    @NotNull(message = "El campo 'map' es obligatorio")
    private MapaReqOBJ map;
    private String accesoId;
}
