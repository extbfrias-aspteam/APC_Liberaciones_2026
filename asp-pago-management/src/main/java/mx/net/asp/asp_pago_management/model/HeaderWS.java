package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderWS  {
	private String idSesion;
	private long idEmpresa;
	private long idResponsabilidad;
	protected String usuarioClave;
	private long idUsuario;
	private long idClaseCanalAtencion;
	private long idCanalAtencion;
	private long idPuntoAtencion;
	private long idUbicacion;
	private long idSucursal;
	private long idComisionista;
	private long idTransaccion;
	protected String ipHost;
	protected String nameHost;
}
