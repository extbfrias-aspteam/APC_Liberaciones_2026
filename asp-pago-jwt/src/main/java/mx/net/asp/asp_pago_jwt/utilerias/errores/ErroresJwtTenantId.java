package mx.net.asp.asp_pago_jwt.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresJwtTenantId {

    public static final Map<Integer, String> descError = new HashMap<>();

    static {

    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
