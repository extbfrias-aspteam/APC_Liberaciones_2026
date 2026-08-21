package mx.net.asp.asp_pago_onb.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaFotoIneReq {
    @NotBlank(message = "El campo 'credencial' es obligatorio")
    private String credencial;
    @NotBlank(message = "El campo 'captura' es obligatorio")
	private String captura;
    @NotBlank(message = "El campo 'tipo' es obligatorio")
	private String tipo;
    @NotBlank(message = "El campo 'curp' es obligatorio")
    private String curp;
	private String limiteInferior;
}
