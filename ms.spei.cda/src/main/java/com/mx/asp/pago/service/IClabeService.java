package com.mx.asp.pago.service;

import com.mx.asp.pago.dto.Response;

public interface IClabeService {

	Response<?> getSaldoUdn(String clabe);

}
