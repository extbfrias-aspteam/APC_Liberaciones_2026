package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidaFotoIneOBJ {
    private String credencial;
	private String captura;
	private String tipo;
	private String limiteInferior;
}
