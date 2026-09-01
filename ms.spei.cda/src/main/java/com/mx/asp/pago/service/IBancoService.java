package com.mx.asp.pago.service;

import com.mx.asp.pago.dto.Response;

public interface IBancoService {
	void cargarCatalogo();
	Response<?> getBancoPorClabe(String clabe);
}
