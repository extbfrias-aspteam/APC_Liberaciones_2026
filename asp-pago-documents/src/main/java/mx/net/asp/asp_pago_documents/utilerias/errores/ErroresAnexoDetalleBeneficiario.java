package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresAnexoDetalleBeneficiario {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int CUENTA_NOT_FOUND = -102;
    public static final int BENEFICIARIO_NOT_FOUND = -103;
    public static final int SOLICITANTE_NOT_FOUND = -104;
    public static final int DATOS_DOC = -105;
    public static final int GENERA_DOC = -106;
    public static final int BENEFICIARIO_DETALLE_NOT_FOUND = -107;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la información de la cuenta.");
        descError.put(BENEFICIARIO_NOT_FOUND, "La cuenta no tiene beneficiario registrado.");
        descError.put(BENEFICIARIO_DETALLE_NOT_FOUND, "No se ha llenado el detalle del beneficiario.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró información del solicitante.");
        descError.put(DATOS_DOC, "Ocurrió un error al obtener los datos para generar la documentación.");
        descError.put(GENERA_DOC, "Ocurrió un error al generar la documentación.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}