package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroContratoOBJ {
	private String cuenta;
	private String solicitanteId;
	private String cuenta_clabe;
	private String contrato;
	private Integer tipo_ahorro_id;
	private String nombre_solicitante;
	private String correo;
	private String cuenta_padre;
	private String telefono_codi;
}
