package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectorioTelefonicoOBJ {
	private Integer idDirectorioTelefonico;
	private String idSolicitante;
	private Integer idCatTelefono;
	private Integer idCompaniaTel;
	private String telefono;
	private String extension;
	private String observaciones;
	private Integer portado;
	private Integer creadoPor;
	private Timestamp fechaCreacion;
	private Integer modificadoPor;
	private Timestamp fechaModificacion;
}
