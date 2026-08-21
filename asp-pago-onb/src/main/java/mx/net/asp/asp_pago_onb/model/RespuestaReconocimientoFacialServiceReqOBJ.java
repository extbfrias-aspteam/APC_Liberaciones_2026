package mx.net.asp.asp_pago_onb.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaReconocimientoFacialServiceReqOBJ {
    private String codigoValidacion;
	private String estatus;
	private String mensaje;
	private double similitud;
}
