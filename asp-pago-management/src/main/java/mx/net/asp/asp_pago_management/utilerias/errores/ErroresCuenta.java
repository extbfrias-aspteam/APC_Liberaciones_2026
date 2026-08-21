package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresCuenta {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int CUENTA_NOT_FOUND = -102;
    // public static final int CUENTA_BLOQUEADA = -103;
    // public static final int CUENTA_CANCELADA = -104;
    public static final int CREDITOS_ACTIVOS_NOT_FOUND = -103;
    public static final int TARJETAS_NOT_FOUND = -104;
    public static final int NIVEL_CUENTA_NOT_FOUND = -105;
    public static final int ERROR_CUENTA_SIN_NIVEL = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la información de la cuenta.");
        // descError.put(CUENTA_BLOQUEADA, "Tu cuenta se encuentra bloqueada, para desbloquearlo comuníquese a Contact Center.");
        // descError.put(CUENTA_CANCELADA, "Esta cuenta se encuentra cancelada, comuníquese a Contact Center.");
        descError.put(CREDITOS_ACTIVOS_NOT_FOUND, "No se encontraron créditos activos del solicitante.");
        descError.put(TARJETAS_NOT_FOUND, "No se encontraron tarjetas activas de la cuenta.");
        descError.put(NIVEL_CUENTA_NOT_FOUND, "No se encontró el nivel de la cuenta");
        descError.put(ERROR_CUENTA_SIN_NIVEL, "La cuenta no tiene nivel");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresCuenta() {
    }
}
