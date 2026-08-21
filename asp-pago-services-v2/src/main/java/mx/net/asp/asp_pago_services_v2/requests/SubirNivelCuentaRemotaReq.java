package mx.net.asp.asp_pago_services_v2.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_services_v2.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubirNivelCuentaRemotaReq {
	private HeaderWS header;
	@NotBlank(message = "El campo 'cuentaah' es obligatorio")
	private String cuentaah;
	@NotNull(message = "El campo 'actividadId' es obligatorio")
	@Min(value = 1, message = "El campo 'actividadId' no debe ser 0")
	private Integer actividadId;
	@NotNull(message = "El campo 'giroId' es obligatorio")
	@Min(value = 1, message = "El campo 'giroId' no debe ser 0")
	private Integer giroId;
	@NotNull(message = "El campo 'ocupacionId' es obligatorio")
	@Min(value = 1, message = "El campo 'ocupacionId' no debe ser 0")
	private Integer ocupacionId;
	@NotNull(message = "El campo 'objetivoId' es obligatorio")
	@Min(value = 1, message = "El campo 'objetivoId' no debe ser 0")
	private Integer objetivoId;
	@NotNull(message = "El campo 'frecuenciaId' es obligatorio")
	@Min(value = 1, message = "El campo 'frecuenciaId' no debe ser 0")
	private Integer frecuenciaId;
	@NotNull(message = "El campo 'ahorroId' es obligatorio")
	@Min(value = 1, message = "El campo 'ahorroId' no debe ser 0")
	private Integer ahorroId;
	@NotNull(message = "El campo 'ingresoId' es obligatorio")
	@Min(value = 1, message = "El campo 'ingresoId' no debe ser 0")
	private Integer ingresoId;
	@NotNull(message = "El campo 'acepta_term' es obligatorio")
	private Boolean acepta_term;
	@NotBlank(message = "El campo 'latitud' es obligatorio")
	private String latitud;
	@NotBlank(message = "El campo 'longitud' es obligatorio")
	private String longitud;
}
