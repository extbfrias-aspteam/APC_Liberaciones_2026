package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aplicaciones implements Serializable {
	@Serial
    private static final long serialVersionUID = 1L;
	/*IDENTIFICADOR DE LA TABLA*/
	private String id;
	/*ESTATUS DE BAJA DEL REGISTRO*/
	private String estatusId;
	/*FECHA DE LA BAJA*/
	private java.util.Date fechaBaja;
	/*MOTIVO DE LA BAJA*/
	private String motivoBajaId;
	/*FECHA HORA EN QUE SE CREO EL REGISTRO*/
	private java.util.Date fechaCreacion;
	/*EMPRESA DEL REGISTRO*/
	private String empresaId;
	/*DESCRIPCION DE LA APLICACION*/
	private String descripcion;
	/*GUARDA LA CLAVE DE IDENTIFICACION*/
	private String clave;
	private String claveCore;
}





