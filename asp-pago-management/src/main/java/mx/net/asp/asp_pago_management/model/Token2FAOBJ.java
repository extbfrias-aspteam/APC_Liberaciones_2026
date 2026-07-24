package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token2FAOBJ {
	private Integer status;
	private Integer minutos;
	private Long id_codigo;
}
