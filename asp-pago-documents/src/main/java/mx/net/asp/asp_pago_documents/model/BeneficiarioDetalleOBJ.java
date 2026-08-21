package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioDetalleOBJ {
    private Long id;
	private Long idBeneficiario;
	private String cuenta;
	private String fechaNacimiento;
	private String domicilio;
	private String codigoPostal;
	private String colonia;
	private String ciudad;
	private String numeroInterior;
	private String calle;
	private String numeroExterior;
	private String curp;
	private String rfc;
	private String numeroCelular;
	private String personaId;
	private Integer porcentaje;
	private String nombreBeneficiario;
	private Integer relacionId;
	private Boolean detalleEstatus;
}
