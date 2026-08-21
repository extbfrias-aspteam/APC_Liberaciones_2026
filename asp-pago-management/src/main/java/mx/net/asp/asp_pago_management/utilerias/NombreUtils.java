package mx.net.asp.asp_pago_management.utilerias;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class NombreUtils {
    public String enmascararNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "";

        // Palabras como "de", "del", "la", etc. que queremos mostrar como inicial
        Set<String> conectores = Set.of("de", "del", "la", "los", "las");

        return Arrays.stream(nombreCompleto.trim().split("\\s+"))
                .map(palabra -> {
                    if (conectores.contains(palabra.toLowerCase())) {
                        return palabra.substring(0, 1).toUpperCase(); // Solo inicial
                    }
                    return palabra.substring(0, 1).toUpperCase() + "*".repeat(palabra.length() - 1);
                })
                .collect(Collectors.joining(" "));
    }
}
