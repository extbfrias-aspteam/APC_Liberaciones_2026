package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresCierraSesion {

    public static final int SESION_ACTIVA_NOT_FOUND = -100;
    public static final int ERROR_CIERRA_SESION = -101;
    public static final int SESION_CERRADA = -102;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(SESION_ACTIVA_NOT_FOUND, "No se encontró sesión activa.");
        descError.put(ERROR_CIERRA_SESION, "Lo sentimos, ocurrio un error al intentar cerrar la sesión.");
        descError.put(SESION_CERRADA, "La sesión se encuentra actualmente cerrada.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
