package mx.net.asp.asp_pago_services_v2.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresFinalizaSesionIncode {
    public static final int ERROR_FINALIZAR_SESION = -100;
    public static final int INFORMACION_SOLICITANTE_NOT_FOUND = -101;
    public static final int ESTATUS_INVALIDO = -102;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ERROR_FINALIZAR_SESION, "No se logro finalizar la sesion de Incode.");
        descError.put(INFORMACION_SOLICITANTE_NOT_FOUND, "No se encontraron los datos del solicitante.");
        descError.put(ESTATUS_INVALIDO, "Estatus inválido para finalizar la sesión.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresFinalizaSesionIncode() {
    }
}
