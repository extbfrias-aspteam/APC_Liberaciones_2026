package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresServiciosTerceros {
    public static final int SERVICIOS_NOT_FOUND = -100;
    public static final int RECARGAS_NOT_FOUND = -101;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(SERVICIOS_NOT_FOUND, "Lo sentimos, no se encontró listado de servicios de terceros.");
        descError.put(RECARGAS_NOT_FOUND, "Lo sentimos, no se encontró listado de recargas telefónicas.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    private ErroresServiciosTerceros() {
    }
}
