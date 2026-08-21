package mx.net.asp.asp_pago_management.utilerias;

import org.springframework.stereotype.Component;

@Component
public class Utilerias {

	private final ErrorHandler errorHandler;

	public Utilerias(
			ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public static String limpiarCaracteresEspeciales(String texto) {
		// Expresión regular para encontrar caracteres especiales excepto espacios en blanco
		String regex = "[^a-zA-Z0-9 ]";

		// Reemplazar los caracteres especiales con una cadena vacía
		return texto.replaceAll(regex, "");
	}

	public Double redondeaCentavos(Double monto, Integer decimalesPermitidos) {
		Double montoRendondeado = monto;
		try {
			String[] splitter = monto.toString().split("\\.");

			int numDecimales = splitter[1].length();
			if (numDecimales > 2) {
				long factor = (long) Math.pow(10, decimalesPermitidos);
				montoRendondeado = montoRendondeado * factor;
				long tmp = Math.round(montoRendondeado);
				montoRendondeado = (double) tmp / factor;
			}
		}catch (Exception e){
			errorHandler.handleException(e);
		}
		return montoRendondeado;
	}
}
