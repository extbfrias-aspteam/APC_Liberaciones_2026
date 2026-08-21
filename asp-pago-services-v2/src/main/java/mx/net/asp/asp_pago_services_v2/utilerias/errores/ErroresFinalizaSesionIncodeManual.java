package mx.net.asp.asp_pago_services_v2.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresFinalizaSesionIncodeManual {
    public static final int CUENTA_NOT_FOUND = -100;
    public static final int PERSONA_ID_NOT_FOUND = -101;
    public static final int INFORMACION_SOLICITANTE_NOT_FOUND = -102;
    public static final int ERROR_FINALIZAR_SESION_MANUAL = -103;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NOT_FOUND, "Cuenta no encontrada.");
        descError.put(PERSONA_ID_NOT_FOUND, "No se encontro la persona Id.");
        descError.put(INFORMACION_SOLICITANTE_NOT_FOUND, "No se encontro la informacion del solicitante.");
        descError.put(ERROR_FINALIZAR_SESION_MANUAL, "No se logro finalizar la sesion manual de Incode.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresFinalizaSesionIncodeManual() {
    }
}
