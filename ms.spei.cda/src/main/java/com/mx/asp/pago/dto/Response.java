package com.mx.asp.pago.dto;

import com.mx.asp.pago.enums.EstatusResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response <T> {
	private EstatusResponse estatus;
	private String descripcion;
	private T resultado;
}
