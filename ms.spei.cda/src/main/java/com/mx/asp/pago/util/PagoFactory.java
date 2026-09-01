package com.mx.asp.pago.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.mx.asp.pago.dto.ClabeDestinoRequest;
import com.mx.asp.pago.dto.Pago;
import com.mx.asp.pago.dto.claveGenerado;
import com.mx.asp.pago.entity.ClaveRastreoEntity;
import com.mx.asp.pago.repository.IClaveRastreoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoFactory {

	private static final Logger log = LoggerFactory.getLogger(PagoFactory.class);

	private static final String EMISOR = "90659";
	private static final String TIPO_APLICACION = "1";
	private static final String DATO_INCREMENTAL_BASE = "0000";
	private static final SecureRandom secureRandom = new SecureRandom();

	private final IClaveRastreoRepository claveRastreoRepository;

	public Pago crearPagoMinimoValidacion(ClabeDestinoRequest dtoDestino) {
		String refNum = this.generateRefNum();
		Pago p = Pago.builder().bancoDestino(dtoDestino.getIdBancoDest()).clabe(dtoDestino.getClabeOrigen())
				.conceptoPago("Pago minimo por verificar datos").ctaDestino(dtoDestino.getClabeDest())
				.cveRastreo(this.obtenerClaveRastreo(dtoDestino.getIdParticipante())).idTipoCtaDestino("40").iva("0")
				.monto("0.01").nombreDestino(dtoDestino.getNomClienteDest()).otp("").refNum(refNum)
				.refCob("RefCob-" + refNum).rfcDestino(dtoDestino.getRfc()).timeStamp(this.generateTimeStamp())
				.uuid(dtoDestino.getUuid())
				.build();
		return p;
	}

	private String generateRefNum() {
	    int number = 1_000_000 + secureRandom.nextInt(9_000_000);
	    return String.valueOf(number);
	}

	private String generateTimeStamp() {
		return Instant.now().toString();
	}

	@Transactional
	public String obtenerClaveRastreo(String idPblu) {
		String idP = idPblu;
		claveGenerado clave = new claveGenerado();
		String claveRastreo = "";
		Date fechaHora = new Date();
		SimpleDateFormat formatoFechaD = new SimpleDateFormat("yyyyMMdd");
		String fechaD = formatoFechaD.format(fechaHora);
		SimpleDateFormat formatoHoraD = new SimpleDateFormat("HHmmssSS");
		String horaD = formatoHoraD.format(fechaHora);
		String horaMili = horaD.substring(0, horaD.length() - 1);
		Integer datoIncre = null;
		try {
			datoIncre = claveRastreoRepository.obtenerDatoIncremental(fechaD, idP);
		} catch (DataAccessException e) {

			log.error("Error al acceder a la BD");
		}

		log.info("Dato Incremental: " + datoIncre);
		datoIncre++;
		String datoIncrementalBd = datoIncre.toString();
		String datoIncremental = DATO_INCREMENTAL_BASE.substring(0,
				DATO_INCREMENTAL_BASE.length() - datoIncrementalBd.length()) + datoIncrementalBd;

		int numero = (int) (Math.random() * 10);
		String datoVerificador = String.valueOf(numero);

		log.info(fechaD);
		log.info("" + horaMili);

			ClaveRastreoEntity claveRastreoEntity = new ClaveRastreoEntity();
			claveRastreoEntity.setId(generateClaveRastreoEntityId());
			claveRastreoEntity.setDatoIncremental(datoIncre);
			claveRastreoEntity.setDatoVerificador(datoVerificador);
			claveRastreoEntity.setFecha(fechaD);
		claveRastreoEntity.setHora(horaMili);
		claveRastreoEntity.setIdParticipante(idP);
		claveRastreoEntity.setSpeiEmisor(EMISOR);
		claveRastreoEntity.setTipoAplicacion(TIPO_APLICACION);

			saveClaveRastreoEntity(claveRastreoEntity);

		claveRastreo = claveRastreo + fechaD + EMISOR + horaMili + TIPO_APLICACION + idP + datoIncremental
				+ datoVerificador;
		clave.setClaveRastreo(claveRastreo);
		return claveRastreo;
	}

	private Integer generateClaveRastreoEntityId() {
		// Avoids depending on DB sequence permissions for the audit table ID.
		return ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
	}

	private void saveClaveRastreoEntity(ClaveRastreoEntity claveRastreoEntity) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				if (attempt > 1) {
					claveRastreoEntity.setId(generateClaveRastreoEntityId());
				}
				claveRastreoRepository.save(claveRastreoEntity);
				return;
			} catch (DataAccessException e) {
				log.error("Error al guardar los datos de la clave de rastreo a la BD. intento={}", attempt, e);
			}
		}
	}

}
