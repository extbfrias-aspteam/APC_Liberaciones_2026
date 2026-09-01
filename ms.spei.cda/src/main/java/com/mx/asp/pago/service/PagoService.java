package com.mx.asp.pago.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.asp.pago.dto.ClabeDestinoRequest;
import com.mx.asp.pago.dto.DatosPagoRequest;
import com.mx.asp.pago.dto.LoginRequest;
import com.mx.asp.pago.dto.Pago;
import com.mx.asp.pago.dto.PagoRequest;
import com.mx.asp.pago.dto.Response;
import com.mx.asp.pago.entity.PagoEntity;
import com.mx.asp.pago.enums.EstatusResponse;
import com.mx.asp.pago.record.AuthClientRecord;
import com.mx.asp.pago.record.CertificadoFiles;
import com.mx.asp.pago.repository.AuthClientLookupRepository;
import com.mx.asp.pago.repository.IPagoRepository;
import com.mx.asp.pago.util.MetodosEncriptacion;
import com.mx.asp.pago.util.PagoFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class PagoService implements IPagoService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${url.login}")
	private String urlLogin;

	@Value("${url.pago}")
	private String urlPago;

	@Value("${token.portal.pago}")
	private String tokenPortalPago;

	@Value("${name.gcp.bucket}")
	private String nomBucket;

	private final ObjectMapper objectMapper;
	private final MetodosEncriptacion metodosEncripatacion;
	private final PagoFactory pagoF;
	private final IBancoService ibanco;
	private final IGcpService igcpService;
	private final IPagoRepository iPagoRepository;
	private final AuthClientLookupRepository authClientLookupRepository;

	@SuppressWarnings("unchecked")
	@Override
	public Response<?> pago(DatosPagoRequest datosPagoRequest) {
		Optional<PagoEntity> pagoEntityOpt = Optional.empty();
		try {
			pagoEntityOpt = iPagoRepository.findByUuid(datosPagoRequest.getUuid());
		} catch (DataAccessException e) {
			log.info("Error al conectarse a la Base de Datos para validar uuid.");
			return Response.builder().estatus(EstatusResponse.error).descripcion("Error Interno")
					.resultado(e.getMessage()).build();
		}
		
		if(pagoEntityOpt.isPresent()) {
			log.info("El uuid [{}] ya fue procesado previamente.", datosPagoRequest.getUuid());
			return Response.builder().estatus(EstatusResponse.error).descripcion("El uuid ya fue procesado previamente")
					.resultado(null).build();
		}
		
		try {
			Response<AuthClientRecord> authClientResponse = findActiveAuthClientByClabe(datosPagoRequest.getCuentaOrigen());
			if (authClientResponse.getEstatus() != EstatusResponse.ok) {
				return authClientResponse;
			}

			AuthClientRecord authClient = authClientResponse.getResultado();
			String idPblu = converteIdParticipante(authClient.idPblu());

			Response<?> respuestaPago = this.setPago(datosPagoRequest, idPblu);
			if (respuestaPago.getEstatus() != EstatusResponse.ok) {
				return respuestaPago;
			}
			Response<CertificadoFiles> respuestaDcoumentos = (Response<CertificadoFiles>) igcpService
					.getCertificadoFiles(nomBucket, idPblu);
			if (respuestaDcoumentos.getEstatus() != EstatusResponse.ok) {
				return respuestaDcoumentos;
			}

			Pago pago = (Pago) respuestaPago.getResultado();
			log.info("Inicia proceso de pago.");

			Response<?> tokenResponse = generateToken(authClient.username(), authClient.password());
			if (tokenResponse.getEstatus() != EstatusResponse.ok) {
				return tokenResponse;
			}

			String token = (String) tokenResponse.getResultado();
			String jsonPago = objectMapper.writeValueAsString(pago);

			String firma = metodosEncripatacion.getFirma(jsonPago, respuestaDcoumentos.getResultado().getKey(),
					respuestaDcoumentos.getResultado().getTxt());

			Map<String, String> cifrado = metodosEncripatacion.getKey(jsonPago,
					respuestaDcoumentos.getResultado().getCer());
			if (cifrado == null) {
				return error("Error Interno", "No se pudo generar el cifrado");
			}

			String llave = cifrado.get("KEY_CIFRADA");
			String peticionCifrada = cifrado.get("PETICION_CIFRADA");

			log.info("Finaliza preparación de pago. referencia={}", pago.getRefNum());

			return apiPago(jsonPago, firma, llave, peticionCifrada, token, pago.getCveRastreo());

		} catch (ResourceAccessException e) {
			log.error("Error de conexión al procesar pago", e);
			return error("Error de conexión", e.getMessage());

		} catch (HttpStatusCodeException e) {
			log.error("Error HTTP al procesar pago", e);
			return error("Error Interno", e.getResponseBodyAsString());

		} catch (JsonProcessingException e) {
			log.error("Error al serializar pago", e);
			return error("Error Interno", e.getMessage());
		}
	}

	private Response<?> apiPago(String jsonPago, String firma, String llave, String peticionCifrada, String token,String cveRastreo) {

		try {
			log.info("Inicia consumo servicio de pago. url={}", urlPago);

			PagoRequest pagoRequest = PagoRequest.builder().pago(peticionCifrada).build();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(token);
			headers.set("Signature", firma);
			headers.set("Key", llave);
			headers.set("token_portal", tokenPortalPago);

			HttpEntity<PagoRequest> request = new HttpEntity<>(pagoRequest, headers);
			ResponseEntity<Object> response = restTemplate.postForEntity(urlPago, request, Object.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				return errorPago("Respuesta no exitosa del servicio de pago");
			}

			log.info("Pago realizado correctamente");
			
			return findPagoByClaveRastreo(cveRastreo);

		} catch (ResourceAccessException e) {
			log.error("Error de conexión al consumir servicio de pago", e);
			return errorPago("Error de conexión con servicio de pago");

		} catch (HttpStatusCodeException e) {
			log.error("Error HTTP en servicio de pago", e);

			String mensaje = e.getMessage();
			try {
				JsonNode root = objectMapper.readTree(e.getResponseBodyAsString());
				mensaje = root.path("mensaje").asText(mensaje);
			} catch (Exception ex) {
				log.warn("No se pudo parsear el body de error");
			}

			return errorPago(mensaje);
		}
	}

	@Override
	public Response<?> generateToken(String username, String password) {
		log.info("Inicia generación de token para consumo de servicio de Pago [{}]", urlLogin);

		try {
			LoginRequest login = LoginRequest.builder().username(username).password(password).build();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<LoginRequest> request = new HttpEntity<>(login, headers);

			ResponseEntity<Void> response = restTemplate.postForEntity(urlLogin, request, Void.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				return errorLogin("Respuesta no exitosa al generar token");
			}

			String authHeader = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || authHeader.isBlank()) {
				return errorLogin("Token no recibido en headers");
			}

			String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

			log.info("Token generado correctamente");

			return Response.builder().estatus(EstatusResponse.ok).descripcion("Token generado").resultado(token)
					.build();

		} catch (ResourceAccessException e) {
			log.error("Error de conexión al generar token", e);
			return errorLogin("Error de conexión con servicio de autenticación");

		} catch (HttpStatusCodeException e) {
			log.error("Error HTTP al generar token", e);
			return errorLogin("Credenciales inválidas o error de autenticación");
		}
	}

	private Response<?> setPago(DatosPagoRequest dpr, String idPblu) {
		Response<?> responseBanco = ibanco.getBancoPorClabe(dpr.getCuentaDestino());
		if (responseBanco.getEstatus() != EstatusResponse.ok) {
			return responseBanco;
		}
		ClabeDestinoRequest cdr = ClabeDestinoRequest.builder().idParticipante(idPblu)
						.clabeOrigen(dpr.getCuentaOrigen()).idBancoDest((String) responseBanco.getResultado().toString())
						.nomClienteDest(dpr.getNombreCompleto()).clabeDest(dpr.getCuentaDestino()).rfc(dpr.getRfc()).uuid(dpr.getUuid()).build();
		Pago pago = pagoF.crearPagoMinimoValidacion(cdr);

		return new Response<>().builder().estatus(EstatusResponse.ok).descripcion("Pago seteado correctamente")
					.resultado(pago).build();
	}

	private Response<AuthClientRecord> findActiveAuthClientByClabe(String clabe) {
		try {
			Optional<AuthClientRecord> authClientOpt = authClientLookupRepository.findActiveAuthClientByClabe(clabe);
			if (authClientOpt.isEmpty()) {
				return Response.<AuthClientRecord>builder().estatus(EstatusResponse.error)
						.descripcion("No se encontró participante activo con la cuenta CLABE proporcionada")
						.resultado(null).build();
			}

			AuthClientRecord authClient = authClientOpt.get();
			if (authClient.username() == null || authClient.username().isBlank() || authClient.password() == null
					|| authClient.password().isBlank()) {
				return Response.<AuthClientRecord>builder().estatus(EstatusResponse.error)
						.descripcion("No se encontraron credenciales activas para la cuenta CLABE proporcionada")
						.resultado(null).build();
			}

			return Response.<AuthClientRecord>builder().estatus(EstatusResponse.ok)
					.descripcion("Datos de auth client encontrados").resultado(authClient).build();
		} catch (DataAccessException e) {
			log.error("Error al acceder a la BD.", e);
			return Response.<AuthClientRecord>builder().estatus(EstatusResponse.error)
					.descripcion("Error al consultar auth client por CLABE en la BD.").resultado(null).build();
		}
	}

	private String converteIdParticipante(String idPblu) {
		if (idPblu == null || idPblu.isBlank()) {
			throw new IllegalArgumentException("idPblu no puede ser null o vacío");
		}

		int id;
		try {
			id = Integer.parseInt(idPblu);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("idPblu debe ser numérico", e);
		}

		return String.format("%02d", id);
	}

	private static final int BUSQUEDA_PAGO_MAX_INTENTOS = 6;
	private static final long BUSQUEDA_PAGO_ESPERA_MS = 500;

	@Override
	public Response<?> findPagoByClaveRastreo(String claveRastreo) {
		log.info("..Inicia busqueda del pago...");
		try {
			for (int intento = 1; intento <= BUSQUEDA_PAGO_MAX_INTENTOS; intento++) {
				Optional<PagoEntity> pEntity = iPagoRepository.findById(claveRastreo);
				if (pEntity.isPresent()) {
					log.info("..Fin busqueda del pago... resultado [{}] (intento {})", pEntity.get(), intento);
					return Response.builder().estatus(EstatusResponse.ok).descripcion("Datos del pago")
							.resultado(pEntity.get()).build();
				}

				if (intento < BUSQUEDA_PAGO_MAX_INTENTOS) {
					log.info("..Pago aún no visible (intento {}/{}), reintentando en {}ms...", intento,
							BUSQUEDA_PAGO_MAX_INTENTOS, BUSQUEDA_PAGO_ESPERA_MS);
					try {
						Thread.sleep(BUSQUEDA_PAGO_ESPERA_MS);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						break;
					}
				}
			}

			log.info("..Fin busqueda del pago... No se encontró pago tras {} intentos.", BUSQUEDA_PAGO_MAX_INTENTOS);
			return Response.builder().estatus(EstatusResponse.error).descripcion("No se encontro pago")
					.resultado(new ArrayList<>()).build();

		} catch (Exception e) {
			return Response.builder().estatus(EstatusResponse.error).descripcion("Error Interno")
					.resultado(e.getMessage()).build();
		}

	}

	private Response<?> error(String descripcion, String detalle) {
		return Response.builder().estatus(EstatusResponse.error).descripcion(descripcion).resultado(detalle).build();
	}

	private Response<?> errorPago(String mensaje) {
		return Response.builder().estatus(EstatusResponse.errorPago).descripcion("Error de pago").resultado(mensaje)
				.build();
	}

	private Response<?> errorLogin(String mensaje) {
		return Response.builder().estatus(EstatusResponse.errorLogin).descripcion("Error al generar token")
				.resultado(mensaje).build();
	}

}
