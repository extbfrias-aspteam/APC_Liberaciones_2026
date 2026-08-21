package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_management.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarFavoritoReq {
	private HeaderWS header;
	@NotNull(message = "El campo 'id' es obligatorio")
	@Min(value = 1, message = "El campo 'id' no debe ser 0")
	private Integer id;
}
