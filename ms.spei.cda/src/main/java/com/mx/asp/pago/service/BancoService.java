package com.mx.asp.pago.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.entity.Banco;
import com.mx.asp.pago.enums.EstatusResponse;
import com.mx.asp.pago.repository.IBancoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BancoService implements IBancoService {

	private Map<String, Banco> bancoPorClave;
	private final IBancoRepository ibancoRepository;

	@Override
	@PostConstruct
	public void cargarCatalogo() {
		List<Banco> bancos = ibancoRepository.findAll();
		bancoPorClave = bancos.stream()
				.collect(Collectors.toMap(b -> String.format("%03d", b.getIdBanco() % 1000), Function.identity()));
	}

	@Override
	public Response<?> getBancoPorClabe(String clabe) {
		Response<?> validadClabe = validarClabe(clabe);
		if (validadClabe.getEstatus() == EstatusResponse.error) {
			return validadClabe;
		}

		String claveBanco = clabe.substring(0, 3);
		Banco banco = bancoPorClave.get(claveBanco);

		if (banco == null) {
			return new Response().builder().estatus(EstatusResponse.error)
					.descripcion("No se encontro Banco Asociada a esta clabe").resultado(new ArrayList<>()).build();

		}
		return new Response().builder().estatus(EstatusResponse.ok).descripcion("Clabe correcta")
				.resultado(banco.getIdBanco()).build();
	}

	private Response<?> validarClabe(String clabe) {
		if (clabe == null || clabe.length() != 18) {
			return new Response().builder().estatus(EstatusResponse.error).descripcion("CLABE destino inválida")
					.resultado(new ArrayList<>()).build();
		}
		if (!clabe.matches("\\d+")) {
			return new Response().builder().estatus(EstatusResponse.error).descripcion("CLABE destino debe ser numérica")
					.resultado(new ArrayList<>()).build();

		}
		return new Response().builder().estatus(EstatusResponse.ok).descripcion("Validado correctament")
				.resultado(new ArrayList<>()).build();

	}
}
