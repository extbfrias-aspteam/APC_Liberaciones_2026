package com.mx.asp.pago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.service.IClabeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/cuenta")
public class CuentaController {

	private final IClabeService iCuentaService;

	@GetMapping("/getSaldo")
	public ResponseEntity<Response<?>> getSaldoCuenta(@RequestParam String clabe) {
		Response response = iCuentaService.getSaldoUdn(clabe);
		return ResponseEntity.ok(response);
	}

}
