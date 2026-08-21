package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class FavoritoOBJ {

	private Integer id;
	private String numeroCuentaCoDi;
	private String nombreBeneficiario;
	private String numeroCuentaBeneficiario;
	private Integer tipoCuentaBeneficiario;
	private Integer idInstitucionBeneficiario;
	private String nombreInstitucion;
	private Integer idEstatus;
	private String alias;
	private Instant fecha_creacion;
}