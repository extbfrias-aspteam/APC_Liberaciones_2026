package mx.net.asp.asp_pago_jwt.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresRevocaJwtToken {

    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int SOLICITANTE_NOT_FOUND = -102;
    public static final int VERSION_APP = -103;
    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontro información de cliente con teléfono %s.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
