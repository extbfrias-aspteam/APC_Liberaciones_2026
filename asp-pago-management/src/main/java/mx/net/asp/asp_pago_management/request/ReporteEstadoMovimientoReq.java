package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoMovimientoReq {
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    private String cuenta;
    @NotBlank(message = "El campo 'data' es obligatorio")
	private String data;
}
