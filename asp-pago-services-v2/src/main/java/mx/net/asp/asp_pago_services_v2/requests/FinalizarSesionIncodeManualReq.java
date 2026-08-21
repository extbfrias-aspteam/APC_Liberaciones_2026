package mx.net.asp.asp_pago_services_v2.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_services_v2.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarSesionIncodeManualReq {
	private HeaderWS header;
	@NotBlank(message = "El campo 'interviewId' es obligatorio")
	private String interviewId;
	@NotBlank(message = "El campo 'onboardingStatus' es obligatorio")
	private String onboardingStatus;
}