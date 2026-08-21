package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptoPldOBJ {
	private Integer id;
	private Integer cuenta_id;
	private Integer conceptos_id;
	private String valor;
}
