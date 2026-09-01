package com.mx.asp.pago.service;

import com.mx.asp.pago.dto.Response;

public interface IGcpService {

	Response<?> getCertificadoFiles(String bucketName, String id);
}
