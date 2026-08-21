package mx.net.asp.asp_pago_management.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCuentaDestinoOBJ {
    private String nombreBeneficiario;
    private String apellidoPatBeneficiario;
    private String apellidoMatBeneficiario;
    private String personaId;
}
