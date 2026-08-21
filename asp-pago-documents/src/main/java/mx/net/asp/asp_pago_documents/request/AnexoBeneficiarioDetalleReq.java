package mx.net.asp.asp_pago_documents.request;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexoBeneficiarioDetalleReq {
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    private String cuenta;
}
