package com.mx.asp.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ClabeDestinoRequest {

	private String idParticipante;
	private String clabeOrigen;
	private String clabeDest;
	private String nomClienteDest;
	private String idBancoDest;
	private String rfc;
	private String uuid;

}
