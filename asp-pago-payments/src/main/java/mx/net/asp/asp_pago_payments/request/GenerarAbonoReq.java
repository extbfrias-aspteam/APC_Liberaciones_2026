package mx.net.asp.asp_pago_payments.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarAbonoReq {
    private Long idRetiro;
    @NotNull(message = "El campo 'monto' es obligatorio")
    @DecimalMin(value = "0.01", message = "El 'monto' debe ser mayor a 0")
    private BigDecimal monto;
    @NotBlank(message = "El campo 'concepto' es obligatorio")
    private String concepto;
    @NotBlank(message = "El campo 'cuentaDestino' es obligatorio")
    @Pattern(regexp = "^(\\d{18})$", message = "El campo 'cuentaDestino' debe tener exactamente 18 dígitos.")
    private String cuentaDestino;
    @NotBlank(message = "El campo 'cuentaOrigen' es obligatorio")
    @Pattern(regexp = "^(\\d{18})$", message = "El campo 'cuentaOrigen' debe tener exactamente 18 dígitos.")
    private String cuentaOrigen;
//    @NotBlank(message = "El campo 'producto' es obligatorio")
//    private String producto;
    @NotBlank(message = "El campo 'cveMovimiento' es obligatorio")
    private String cveMovimiento;
    @NotBlank(message = "El campo 'refNum' es obligatorio")
    private String refNum;
    @NotBlank(message = "El campo 'cveRastreo' es obligatorio")
    private String cveRastreo;
    @NotBlank(message = "El campo 'nombreOrdenante' es obligatorio")
    private String nombreOrdenante;
}
