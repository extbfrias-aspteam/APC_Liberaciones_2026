package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresDetalleCuentaDestino {
    public static final int CLABE_NOT_FOUND = -100;
    public static final int TARJETA_NOT_FOUND = -101;
    public static final int CUENTA_NOT_FOUND = -102;
    public static final int SOLICITANTE_NOT_FOUND = -103;
    public static final int ERROR_LONGITUD = -104;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CLABE_NOT_FOUND, "El número ingresado no es una clabe admitida, por favor verifica los datos.");
        descError.put(TARJETA_NOT_FOUND, "El número ingresado no es un número de tarjeta admitido, por favor verifica los datos.");
        descError.put(CUENTA_NOT_FOUND, "El número ingresado no es una cuenta admitida, por favor verifica los datos.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró información para el solicitante %s.");
        descError.put(ERROR_LONGITUD, "Longitud no válida para identificadorCuenta. Longitud: %s.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresDetalleCuentaDestino() {
    }
}
