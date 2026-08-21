package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraReporteEstadoMovimientoReq {
    @NotBlank(message = "El campo 'cuentaah' es obligatorio")
    private String cuentaah;
    private HeaderWS header;
    private Boolean subirDoc;
}
