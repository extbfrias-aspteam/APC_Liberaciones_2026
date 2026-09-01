package com.mx.asp.pago.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.dto.SaldoClabeDto;
import com.mx.asp.pago.entity.CuentaEntity;
import com.mx.asp.pago.entity.UdnEntity;
import com.mx.asp.pago.enums.EstatusResponse;
import com.mx.asp.pago.repository.IClabeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClabeService implements IClabeService {

	private final IClabeRepository iClabeRepository;

	@Override
	public Response<?> getSaldoUdn(String clabe) {
		try {
			Optional<CuentaEntity> cuenta = iClabeRepository.findById(clabe);
			if (cuenta.isEmpty()) {
				return Response.builder().estatus(EstatusResponse.error)
						.descripcion("No se encontro datos para esta clabe").resultado(new ArrayList<>()).build();
			}
			UdnEntity u = cuenta.get().getUdn();

			return Response.builder().estatus(EstatusResponse.ok).descripcion("Resultado")
					.resultado(SaldoClabeDto.builder().clabe(cuenta.get().getClabe()).saldo(u.getSaldo()).build())
					.build();

		} catch (Exception e) {
			log.warn("ERROR INTERNO: [{}] ", e);
			return Response.builder().estatus(EstatusResponse.error).descripcion("Error Interno: " + e.getMessage())
					.resultado(new ArrayList<>()).build();

		}

	}
}
