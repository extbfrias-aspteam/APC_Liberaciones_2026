package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresTokenHuella {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int SOLICITANTE_NOT_FOUND = -101;
    public static final int CUENTA_NOT_FOUND = -102;
    public static final int VERSION_APP = -103;
    public static final int TELEFONO_NO_VALIDADO = -104;
    public static final int ERROR_REGISTRA_BIOMETRICO = -105;
    public static final int ERROR_ACTUALIZA_BIOMETRICO = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontro informacion del solicitante con teléfono %s.");
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la informacion de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(TELEFONO_NO_VALIDADO, "Lo sentimos, su teléfono NO está validado.");
        descError.put(ERROR_REGISTRA_BIOMETRICO, "Lo sentimos, ocurrio un error al intentar registrar su biométrico.");
        descError.put(ERROR_ACTUALIZA_BIOMETRICO, "Lo sentimos, ocurrio un error al intentar actualizar su biométrico.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresTokenHuella() {
    }
}
