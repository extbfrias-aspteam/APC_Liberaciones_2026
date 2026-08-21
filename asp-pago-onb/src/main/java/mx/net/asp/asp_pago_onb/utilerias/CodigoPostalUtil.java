package mx.net.asp.asp_pago_onb.utilerias;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CodigoPostalUtil {

	private static final Map<Range, Long> CP_RANGES = Map.ofEntries(
			Map.entry(new Range(20000, 20997), 1L),         // Aguascalientes
			Map.entry(new Range(21000, 22997), 1353L),     // Baja California
			Map.entry(new Range(23000, 23997), 3715L),     // Baja California Sur
			Map.entry(new Range(24000, 24936), 4703L),     // Campeche
			Map.entry(new Range(25540, 27466), 8977L),     // Coahuila
			Map.entry(new Range(28000, 28989), 9783L),     // Colima
			Map.entry(new Range(29000, 30997), 10617L),    // Chiapas
			Map.entry(new Range(31000, 33997), 18268L),    // Chihuahua
			Map.entry(new Range(1000, 16900), 28206L),     // CDMX
			Map.entry(new Range(34000, 35987), 29242L),    // Durango
			Map.entry(new Range(36000, 38997), 36244L),    // Guanajuato
			Map.entry(new Range(39000, 41997), 158879L),   // Guerrero
			Map.entry(new Range(42000, 43998), 51941L),    // Hidalgo
			Map.entry(new Range(44100, 49994), 58033L),    // Jalisco
			Map.entry(new Range(50000, 57950), 63648L),    // México
			Map.entry(new Range(58000, 61998), 71776L),    // Michoacán
			Map.entry(new Range(62000, 62996), 82044L),    // Morelos
			Map.entry(new Range(63000, 63996), 83762L),    // Nayarit
			Map.entry(new Range(64000, 67996), 85755L),    // Nuevo León
			Map.entry(new Range(68000, 71998), 90582L),    // Oaxaca
			Map.entry(new Range(72000, 75997), 96616L),    // Puebla
			Map.entry(new Range(76000, 76998), 101945L),   // Querétaro
			Map.entry(new Range(77000, 77997), 105128L),   // Quintana Roo
			Map.entry(new Range(78000, 79997), 106356L),   // San Luis Potosí
			Map.entry(new Range(80000, 82996), 112336L),   // Sinaloa
			Map.entry(new Range(83000, 85994), 116520L),   // Sonora
			Map.entry(new Range(86000, 86998), 125606L),   // Tabasco
			Map.entry(new Range(87000, 89970), 128250L),   // Tamaulipas
			Map.entry(new Range(90000, 90990), 131526L),   // Tlaxcala
			Map.entry(new Range(91000, 96998), 132987L),   // Veracruz
			Map.entry(new Range(97000, 97990), 146071L),   // Yucatán
			Map.entry(new Range(98000, 99998), 147730L)    // Zacatecas
	);

	// Clase auxiliar para representar un rango de CP
	public record Range(int start, int end) {
		boolean contains(int value) {
			return value >= start && value <= end;
		}
	}

	public static Long obtenerClavePorCP(String cp) {
		int codigoPostal = Integer.parseInt(cp.trim());
		// Busca el rango que contiene el CP
		return CP_RANGES.entrySet().stream()
				.filter(entry -> entry.getKey().contains(codigoPostal))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(1L); // Valor por defecto
	}
}
