package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresImgCtaSimplificada {
    public static final int CUENTA_NOT_FOUND = -100;
    public static final int ERROR_INSERT_INF_INE_FRONTAL = -101;
    public static final int ERROR_INSERT_INF_INE_REVERSO = -102;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la informacion de la cuenta.");
        descError.put(ERROR_INSERT_INF_INE_FRONTAL, "No se pudo registrar la información del INE Frontal. Proceso detenido.");
        descError.put(ERROR_INSERT_INF_INE_REVERSO, "No se pudo registrar la información del INE Reverso.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }
}
