package mx.net.asp.asp_pago_payments.utilerias;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.model.BitacoraProcesamientoOBJ;
import mx.net.asp.asp_pago_payments.model.RegistraBitacoraProcesamientoReqOBJ;
import mx.net.asp.asp_pago_payments.service.BitacoraProcesamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Component
public class BitacoraProcesamiento {

	@Getter
    private final HashMap<String, List<BitacoraProcesamientoOBJ>> bitProList = new HashMap<>();
	private final HashMap<String, BitacoraProcesamientoOBJ> bitProHash = new HashMap<>();

	@Autowired
	private BitacoraProcesamientoService bitacoraProcesamientoService;
	@Autowired
	Gson gson;
	@Autowired
	private ErrorHandler errorHandler;


	public void inicializaRegistroBitacora(String uuidIndividual, String cuenta, Integer operacion) {
		try {
			BitacoraProcesamientoOBJ bitPro = new BitacoraProcesamientoOBJ();
			bitPro.setCuenta(cuenta);
			bitPro.setOperacionId(operacion);
			bitPro.setFechaInicio(Instant.now().toEpochMilli());
			bitProHash.put(uuidIndividual, bitPro);
		} catch (Exception e) {
			errorHandler.handleException(e);
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
			errorHandler.handleException(e);
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
			errorHandler.handleException(e);
		}
	}

	public void countBitProList() {
		log.info("Registros en lista de bitácora: {}", bitProList.size());
	}

    public void setBitProList(HashMap<String, List<BitacoraProcesamientoOBJ>> bitProList) {
		this.bitProList.clear();
		this.bitProList.putAll(bitProList);
	}
}