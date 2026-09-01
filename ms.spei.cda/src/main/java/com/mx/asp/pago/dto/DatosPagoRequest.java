package com.mx.asp.pago.dto;

import lombok.Data;

@Data
public class DatosPagoRequest {

	private String idParticipante;
	private String cuentaOrigen;
	private String cuentaDestino;
	private String nombreCompleto;
	private String rfc;
	private String uuid;
	

}
