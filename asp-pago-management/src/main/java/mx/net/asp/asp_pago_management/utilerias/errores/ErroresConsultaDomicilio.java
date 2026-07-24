package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresConsultaDomicilio {
    public static final int DOMICILIO_NOT_FOUND = -100;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(DOMICILIO_NOT_FOUND, "Lo sentimos, no se encontró domicilio del solicitante (%s).");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en consulta cp.");
        return String.format(mensaje, param);
    }

    private ErroresConsultaDomicilio() {
    }
}
