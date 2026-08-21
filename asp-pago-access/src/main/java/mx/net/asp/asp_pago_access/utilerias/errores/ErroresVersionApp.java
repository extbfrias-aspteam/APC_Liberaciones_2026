package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresVersionApp {

    public static final int VERSION_NUEVA = -100;
    public static final int ERROR_VALIDA_VERSION = -101;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(VERSION_NUEVA, "Hay una nueva version disponible, actualiza tu aplicación a la última versión. (%s)");
        descError.put(ERROR_VALIDA_VERSION, "Lo sentimos, ocurrio un error al intentar validar la versión de la aplicación.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
