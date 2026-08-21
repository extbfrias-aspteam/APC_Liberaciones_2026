package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresDocCtaInversion {
    public static final int INVERSION_NOT_FOUND = -100;
    public static final int AHORRO_CONTRATO_NOT_FOUND = -101;
    public static final int DATOS_DOC_NOT_FOUND = -102;
    public static final int GENERA_DOC = -105;
    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(INVERSION_NOT_FOUND, "Cuenta de inversion no encontrada.");
        descError.put(AHORRO_CONTRATO_NOT_FOUND, "No se encontro el ahorro contrato de la cuenta de inversion.");
        descError.put(DATOS_DOC_NOT_FOUND, "No se encontro informacion para generar la documentación de la cuenta.");
        descError.put(GENERA_DOC, "Ocurrio un error al generar la documentación.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }
}
