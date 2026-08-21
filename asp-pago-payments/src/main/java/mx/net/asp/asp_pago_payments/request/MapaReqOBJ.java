package mx.net.asp.asp_pago_payments.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapaReqOBJ {
    @NotBlank(message = "El campo 'codBarra' es obligatorio")
    private String codBarra;
    @NotBlank(message = "El campo 'codigo' es obligatorio")
    private String codigo;
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    private String cuenta;
    @NotBlank(message = "El campo 'monto' es obligatorio")
    @Size(min = 12, max = 12, message = "El campo 'monto' debe tener 12 dígitos.")
    private String monto;
    @NotBlank(message = "El campo 'montoCs' es obligatorio")
    @Size(min = 4, max = 4, message = "El campo 'monto' debe tener 4 dígitos.")
    private String montoCs;
    @NotBlank(message = "El campo 'nemEmp' es obligatorio")
    private String nemEmp;
    @NotBlank(message = "El campo 'numMed' es obligatorio")
    private String numMed;
    @NotBlank(message = "El campo 'subEmp' es obligatorio")
    private String subEmp;
}