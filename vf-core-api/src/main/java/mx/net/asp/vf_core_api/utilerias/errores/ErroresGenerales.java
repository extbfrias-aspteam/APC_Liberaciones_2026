package mx.net.asp.vf_core_api.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresGenerales {
    public static final int ERROR_INTERNO = -500;
    public static final int ERROR_WS = -199;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ERROR_INTERNO, "Error interno.");
        descError.put(ERROR_WS, "Lo sentimos, el servicio no se encuentra disponible, inténtelo más tarde.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }
}
