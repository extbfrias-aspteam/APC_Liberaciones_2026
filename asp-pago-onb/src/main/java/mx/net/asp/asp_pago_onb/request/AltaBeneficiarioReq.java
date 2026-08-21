package mx.net.asp.asp_pago_onb.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_onb.model.HeaderWS;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AltaBeneficiarioReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    private String cuenta;
    @NotNull(message = "El objeto 'beneficiarios' es obligatorio")
    @NotEmpty(message = "La lista 'beneficiarios' no puede estar vacía")
    @Valid
    private List<BeneficiarioReqOBJ> beneficiarios;
}
