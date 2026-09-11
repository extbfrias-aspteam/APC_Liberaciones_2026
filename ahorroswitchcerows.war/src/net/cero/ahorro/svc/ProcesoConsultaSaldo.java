package net.cero.ahorro.svc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.cero.ahorro.servicios.ServicioConsultaSaldo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cero.ahorro.servicios.ServiciosAhorroWS;
import net.cero.ahorro.servicios.ServiciosTransaccionesWS;
import net.cero.plastico.data.DatosPlasticoOBJ;
import net.cero.plastico.data.DatosPlasticoREQ;
import net.cero.spring.config.IPAuthenticationProvider;
import net.cero.spring.config.Respuesta;
import net.cero.ws.data.Errores;
import net.cero.ws.data.PlaHeaderWS;
import net.cero.ws.data.RespuestaSVC;
import net.cero.ws.data.ToolsR;
import net.soap.plasticos.servicios.ObtenerSaldoSoapServiciosSW;


@SuppressWarnings("unused")
@Controller
public class ProcesoConsultaSaldo implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(ProcesoConsultaSaldo.class);


	@Autowired
	protected IPAuthenticationProvider authenticationManager;

	private final ServicioConsultaSaldo logic;

	public ProcesoConsultaSaldo(ServicioConsultaSaldo logic) {
		this.logic = logic;
	}

	@RequestMapping(value="/consultaSaldo", method=RequestMethod.POST)
	public ResponseEntity<String> consultaSaldo(@RequestBody String json){
		SecurityContext securityContext = SecurityContextHolder.getContext();
		RespuestaSVC respuestaSvc = new RespuestaSVC();
		RespuestaSVC respuestaSaldo = new RespuestaSVC();
		Respuesta resp = new Respuesta();
		ResponseEntity<String> response;
		String jsonResponse;
		Gson gson = ToolsR.GBuilder();
		Map<String, Object> map;

		System.out.println("SISISISISISISISISISISISISISISISISISISISISISISISIISSISISISIIIS");


		resp.setCodigo(-1);
		try{
			map = new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>() {}.getType());
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
			resp.setMensaje("Proceso incorrecto");
			jsonResponse = gson.toJson(resp);
			return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
		}

		if(map.get("cuenta") == null){
			resp.setMensaje("Parametros incorrectos");
			jsonResponse = gson.toJson(resp);
			return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
		}

		Authentication authenticate = authenticationManager.authenticate(securityContext.getAuthentication());
		if(!authenticate.isAuthenticated()){
			resp.setMensaje("Servicio denegado");
			jsonResponse = gson.toJson(resp);
			return new ResponseEntity<>(jsonResponse, HttpStatus.UNAUTHORIZED);
		}

		/*------IMPRIME LAS VARIABLES DE ENTRADA ----------*/
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			log.info(String.format("%20s - %s", entry.getKey(), entry.getValue()));
		}
		
		try{
			String cuenta = ToolsR._T(map.get("cuenta"));
			RespuestaSVC respCuenta = ServiciosAhorroWS.buscarCuenta(cuenta);
			if(respCuenta.getErrores().getCodigoError() != 0){
				resp.setMensaje(respCuenta.getErrores().getDescError());
				jsonResponse = gson.toJson(resp);
				return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
			}


			String cuentaID = ToolsR._T(respCuenta.getBody().getValor("CUENTA"));
			String estatus = "NO";
			
			map.put("ID", ToolsR._T(respCuenta.getBody().getValor("CUENTA_ID")));
			map.put("cuentaID", ToolsR._T(respCuenta.getBody().getValor("CUENTA")));
			map.put("clienteID", ToolsR._T(respCuenta.getBody().getValor("PERSONA_ID")));
			
			RespuestaSVC respConcepto = ServiciosAhorroWS.buscarCuentaConcepto(cuentaID, "AHO_PLASTICO");
			if(respConcepto.getErrores().getCodigoError() == 0){
				estatus = ToolsR._T(respConcepto.getBody().getValor("VALOR"));
			}
			
			respuestaSvc = "SI".equals(estatus) ? logic.consultarSaldoProveedor(map) : logic.consultarSaldoLocal(map);

			resp.setCodigo(0);
			resp.setMensaje("OK");
			resp.setData(gson.toJson(respuestaSvc));
		}catch(DataAccessException ex){
			log.error(ex.getMessage(),ex);
			resp.setMensaje("Error: Proceso de consulta saldo incorrecto");
			jsonResponse = gson.toJson(resp);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		jsonResponse = gson.toJson(resp);
		log.info("Resultado del WS: {}",jsonResponse);
		return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
	}


	private String mensaje(String msg){
		return msg == null || "".equals(msg) ? "" : (msg+"\n");
	}

	@RequestMapping(
			value = "/consultaSaldo/consulta-saldo-dock-asp",
			method = RequestMethod.POST
	)
	public ResponseEntity<String> consultaSaldoDockAsp(@RequestBody String json) {

		Gson gson = ToolsR.GBuilder();
		Respuesta resp = new Respuesta();
		Map<String, Object> map;
		log.info("Entrando a consultaSaldoDockAsp: {} ", json);

		try {
			map = new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>(){}.getType());
		} catch (Exception e) {
			resp.setCodigo(-1);
			resp.setMensaje("Proceso incorrecto");
			return ResponseEntity.badRequest().body(gson.toJson(resp));
		}

		if (map.get("cuenta") == null) {
			resp.setCodigo(-1);
			resp.setMensaje("Parametros incorrectos");
			return ResponseEntity.badRequest().body(gson.toJson(resp));
		}

		try {
			String cuenta = ToolsR._T(map.get("cuenta"));
			RespuestaSVC respCuenta = ServiciosAhorroWS.buscarCuenta(cuenta);
			log.info("Al buscar la cuenta se encontro: {}", new Gson().toJson(respCuenta));

			if (respCuenta.getErrores().getCodigoError() != 0) {
				resp.setCodigo(-1);
				resp.setMensaje(respCuenta.getErrores().getDescError());
				return ResponseEntity.badRequest().body(gson.toJson(resp));
			}

			String cuentaID = ToolsR._T(respCuenta.getBody().getValor("CUENTA"));
			log.info("cuentaID: {}", cuentaID);
			String estatus = "NO";

			map.put("ID", ToolsR._T(respCuenta.getBody().getValor("CUENTA_ID")));
			map.put("cuentaID", cuentaID);
			map.put("clienteID", ToolsR._T(respCuenta.getBody().getValor("PERSONA_ID")));
			log.info("map hasta el momento: {}", map);

			RespuestaSVC respConcepto =
					ServiciosAhorroWS.buscarCuentaConcepto(cuentaID, "AHO_PLASTICO");

			log.info("Lo obtenido de buscarcuentaConcepto: {}", new Gson().toJson(respConcepto));

			if (respConcepto.getErrores().getCodigoError() == 0) {
				estatus = ToolsR._T(respConcepto.getBody().getValor("VALOR"));
			}

			// 🔴 CASO NO DOCK
			if (!"SI".equalsIgnoreCase(estatus)) {
				resp.setCodigo(0);
				resp.setMensaje("NO");
				resp.setData(null);
				return ResponseEntity.ok(gson.toJson(resp));
			}
			log.info("map para el proceedor: {}", map);
			Map<String, Object> mapProveedor = new HashMap<>(map);
			log.info("map para local: {}", map);
			Map<String, Object> mapLocal     = new HashMap<>(map);

			RespuestaSVC saldoProveedor = logic.consultarSaldoProveedorDock(mapProveedor);
			log.info("saldo proveedor: {}", new Gson().toJson(saldoProveedor));
			RespuestaSVC saldoLocal     = logic.consultarSaldoLocal(mapLocal);
			log.info("saldo local: {}", new Gson().toJson(saldoLocal));

			Map<String, Object> data = new HashMap<>();
			data.put("proveedor",
					saldoProveedor != null
							? extraerSaldos(saldoProveedor)
							: null
			);
			data.put("local", extraerSaldos(saldoLocal));
			log.info("data final: {}", data);

			resp.setCodigo(0);
			resp.setMensaje("OK");
			resp.setData(gson.toJson(data));

			return ResponseEntity.ok(gson.toJson(resp));

		} catch (Exception e) {
			log.error("Error en consulta-saldo-dock-asp", e);
			resp.setCodigo(-1);
			resp.setMensaje("Error interno");
			return ResponseEntity.internalServerError().body(gson.toJson(resp));
		}
	}

	private Map<String, Object> extraerSaldos(RespuestaSVC svc) {
		Map<String, Object> saldo = new HashMap<>();

		if (svc != null && svc.getBody() != null) {
			saldo.put("SALDO_REAL", svc.getBody().getValor("SALDO_REAL"));
			saldo.put("SALDO_ACTUAL", svc.getBody().getValor("SALDO_ACTUAL"));
			saldo.put("MONTO_DISPONIBLE", svc.getBody().getValor("MONTO_DISPONIBLE"));
		}

		return saldo;
	}


}
