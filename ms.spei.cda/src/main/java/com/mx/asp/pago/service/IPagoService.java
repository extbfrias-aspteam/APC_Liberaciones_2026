package com.mx.asp.pago.service;

import com.mx.asp.pago.dto.DatosPagoRequest;
import com.mx.asp.pago.dto.Response;

public interface IPagoService {

	Response<?> pago(DatosPagoRequest datosPagoRequest);

	Response<?> generateToken(String username, String password);
	
	Response<?> findPagoByClaveRastreo(String claveRastreo);

}
