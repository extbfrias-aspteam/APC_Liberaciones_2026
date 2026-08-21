package mx.net.asp.asp_pago_onb.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresValidaFotoIne {
    public static final int ERROR_NEGOCIO_SERVICIO  = -100;
    public static final int ERROR_DEBAJO_DE_UMBRAL = 1;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ERROR_NEGOCIO_SERVICIO, "%s");
        descError.put(ERROR_DEBAJO_DE_UMBRAL, "Validación facial fallida: similitud %.2f < umbral %d");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en valida foto ine");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en valida foto ine.");
        return String.format(mensaje, param);
    }

    public static String getMensajeError(int codigo, Double param1, Integer param2) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en valida foto ine.");
        return String.format(mensaje, param1, param2);
    }
}
