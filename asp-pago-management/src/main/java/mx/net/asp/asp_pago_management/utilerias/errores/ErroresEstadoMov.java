package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresEstadoMov {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int DATA_WITHOUT_INFORMATION = -102;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(DATA_WITHOUT_INFORMATION, "El campo data no contiene información.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresEstadoMov() {
    }
}
