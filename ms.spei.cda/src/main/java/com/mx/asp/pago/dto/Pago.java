package com.mx.asp.pago.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Pago implements Serializable {

	private static final long serialVersionUID = 6008486773636738259L;

	private String bancoDestino;

	private String clabe;

	private String conceptoPago;

	private String ctaDestino;

	private String cveRastreo;

	private String idTipoCtaDestino;

	private String iva;

	private String monto;

	private String nombreDestino;

	private String otp;

	private String refCob;

	private String refNum;

	private String rfcDestino;

	private String timeStamp;

	private String uuid;

}
