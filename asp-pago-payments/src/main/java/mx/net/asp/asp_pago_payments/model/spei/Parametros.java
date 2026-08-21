
/*
*
* Copyright (c) 2019 OPCIONES EMPRESARIALES DEL NORESTE S.A. DE C.V. S.F.P
* Avenida Delfines s/n Entre Marlin y Tiburon, La Paz, BCS Mexico CP 23090.
* 
* TABLA DE CONFIGURACION DE PARAMETROS
*
*/

package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parametros implements Serializable {
	@Serial
    private static final long serialVersionUID = 1L;
	/*IDENTIFICADOR DE LA TABLA*/
	private Long id;
	/*ESTATUS DE BAJA DEL REGISTRO*/
	private Integer estatusId;
	/*FECHA DE LA BAJA*/
	private Date fechaBaja;
	/*MOTIVO DE LA BAJA*/
	private Integer motivoBajaId;
	/*FECHA HORA EN QUE SE CREO EL REGISTRO*/
	private Date fechaCreacion;
	/*EMPRESA DEL REGISTRO*/
	private Integer empresaId;
	/*CLAVE DE INDENTIFICACION DEL PARAMETRO*/
	private String clave;
	/*VALOR DEL PARAMETRO*/
	private String valor;
}

