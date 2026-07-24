package mx.net.asp.asp_pago_management.utilerias;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.model.BitacoraProcesamientoOBJ;
import mx.net.asp.asp_pago_management.model.RegistraBitacoraProcesamientoReqOBJ;
import mx.net.asp.asp_pago_management.service.BitacoraProcesamientoService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class BitacoraProcesamiento {

	private final HashMap<String, List<BitacoraProcesamientoOBJ>> bitProList = new HashMap<>();
	private final HashMap<String, BitacoraProcesamientoOBJ> bitProHash = new HashMap<>();
	private final BitacoraProcesamientoService bitacoraProcesamientoService;
	private final Gson gson;

	public BitacoraProcesamiento(
			BitacoraProcesamientoService bitacoraProcesamientoService,
			Gson gson) {
		this.bitacoraProcesamientoService = bitacoraProcesamientoService;
		this.gson = gson;
	}

	public void inicializaRegistroBitacora(String uuidIndividual, String cuenta, Integer operacion) {
		try {
			BitacoraProcesamientoOBJ bitPro = new BitacoraProcesamientoOBJ();
			bitPro.setCuenta(cuenta);
			bitPro.setOperacionId(operacion);
			bitPro.setFechaInicio(Instant.now().toEpochMilli());
			bitProHash.put(uuidIndividual, bitPro);
		} catch (Exception e) {
			log.error("Error al inicializar el registro de bitácora: ", e);
		}
	}

	public void finalizaRegistroBitacora(String uuidIndividual, String uuidGlobal) {
		try {
			BitacoraProcesamientoOBJ bitPro = bitProHash.get(uuidIndividual);
			if (bitPro != null) {
				bitPro.setFechaFin(Instant.now().toEpochMilli());
				bitProList.computeIfAbsent(uuidGlobal, k -> new ArrayList<>()).add(bitPro);
			} else {
				log.warn("No se encontró el registro de bitácora para el UUID individual: {}", uuidIndividual);
			}
		} catch (Exception e) {
			log.error("Error al finalizar el registro de bitácora: ", e);
		}
	}

	public void registraBitacoraProcesamiento(String uuidGlobal, String cuenta) {
		try {
			List<BitacoraProcesamientoOBJ> registros = bitProList.get(uuidGlobal);
			if (registros != null && !registros.isEmpty()) {
				RegistraBitacoraProcesamientoReqOBJ req = new RegistraBitacoraProcesamientoReqOBJ();
				req.setCuenta(cuenta);
				req.setData(gson.toJson(registros));
				String bitacoraProcesamientoStr = gson.toJson(req);
				bitacoraProcesamientoService.registraBitProList(bitacoraProcesamientoStr);
				bitProList.remove(uuidGlobal);
			} else {
				log.warn("No hay registros de bitácora para el UUID global: {}", uuidGlobal);
			}
		} catch (Exception e) {
			log.error("Error al registrar la bitácora de procesamiento: ", e);
		}
	}

	public void countBitProList() {
		log.info("Registros en lista de bitácora: {}", bitProList.size());
	}

	public Map<String, List<BitacoraProcesamientoOBJ>> getBitProList() {
		return bitProList;
	}

	public void setBitProList(Map<String, List<BitacoraProcesamientoOBJ>> bitProList) {
		this.bitProList.clear();
		this.bitProList.putAll(bitProList);
	}
}