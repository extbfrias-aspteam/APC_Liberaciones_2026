package com.mx.asp.pago.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.asp.pago.dto.DatosPagoRequest;

import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.enums.EstatusResponse;
import com.mx.asp.pago.service.PagoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pago")
public class PagoController {

	private final PagoService pagoService;

	@PostMapping("/pago_encriptado")
	public ResponseEntity<Response<?>> pago(@RequestBody DatosPagoRequest datosPagoRequest) {
		Response<?> resp = pagoService.pago(datosPagoRequest);
		if (resp.getEstatus() != EstatusResponse.ok) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(resp);
	}
}
