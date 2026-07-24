package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AltaBeneficiarioReq {
    private Long id;
	private String nombre;
	private String primer_apellido;
	private String segundo_apellido;
	private String numeroInterior;
    private String numeroExterior;
    private Integer parentesco;
	private double porcentaje;
	private String correo;
    private String nombre_completo;
	private String persona_id;
	private Integer usuario_creacion;
	private String curp;
	private String rfc;
	private String numeroCelular;
    private Boolean detalleEstatus;
	private Integer idCuenta;
    @NotBlank(message = "El campo 'fecha_nac' es obligatorio")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "El campo 'fecha_nac' debe tener el formato YYYY-MM-DD")
	private String fecha_nac;
    @NotBlank(message = "El campo 'calle' es obligatorio")
	private String calle;
    @NotBlank(message = "El campo 'cp' es obligatorio")
    @Pattern(regexp = "^\\d{5}$", message = "El campo 'cp' debe contener exactamente 5 dígitos numéricos")
	private String cp;
    @NotBlank(message = "El campo 'colonia' es obligatorio")
	private String colonia;
    @NotBlank(message = "El campo 'ciudad' es obligatorio")
	private String ciudad;
    @NotBlank(message = "El campo 'domicilio' es obligatorio")
	private String domicilio;
    @NotBlank(message = "El campo 'cuenta' es obligatorio")
    @Pattern(regexp = "^\\d+$", message = "El campo 'cuenta' debe contener solo digitos numericos")
	private String cuenta;
}