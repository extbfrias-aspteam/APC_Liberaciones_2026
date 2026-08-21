package mx.net.asp.asp_pago_documents.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoMovimientoReq {
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    private String cuenta;
    @NotNull(message = "El campo 'fechaInicial' es obligatorio")
    @Min(value = 0, message = "El campo 'fechaInicial' debe ser un timestamp válido")
	private Long fechaInicial;
    @NotNull(message = "El campo 'fechaFinal' es obligatorio")
    @Min(value = 0, message = "El campo 'fechaFinal' debe ser un timestamp válido")
	private Long fechaFinal;
    private Boolean subirDoc;
}
