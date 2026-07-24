package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresValidaCurpCecoban {
    public static final int CONCEPTO_NOT_FOUND = -200;
    public static final int CURP_NO_COINCIDE = -100;
    public static final int ERROR_GENERICO_CECOBAN = -101;
    public static final int CURPS_NOT_MATCH = -102;
    public static final int ERROR_VALIDAR_CURP = -103;
    public static final int ERROR_AUTENTICACION_DESCONOCIDO = -104;
    public static final int ERRORES_AUTENTICACION = -105;
    public static final int ERROR_AUTENTICACION = -106;
    public static final int ERRORES_SERVICIO_CECOBAN = -107;
    public static final int ERROR_SERVICIO_CECOBAN = -108;
    public static final int ERROR_FORMATO_DETAIL_INVALIDO  = -109;
    public static final int ERROR_GENERA_CURP  = -110;
    public static final int ERROR_TEMPORAL  = -111;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CONCEPTO_NOT_FOUND, "No se logró encontrar el concepto de consulta para validar su curp.");
        descError.put(CURP_NO_COINCIDE, "Parece que alguno de tus datos está incorecto. Verifícalos con tu INE.");
        descError.put(ERROR_GENERICO_CECOBAN, "Parece que alguno de tus datos está incorecto. Verifícalos con tu INE.");
        descError.put(CURPS_NOT_MATCH, "El CURP que ha ingresado no es válido. Verifícalo con tu INE.");
        descError.put(ERROR_VALIDAR_CURP, "Lo sentimos, no se pudo validar la CURP.");
        descError.put(ERROR_AUTENTICACION_DESCONOCIDO, "Error desconocido durante el proceso de autenticación.");
        descError.put(ERRORES_AUTENTICACION, "Error de autenticación. Verifique las credenciales.");
        descError.put(ERROR_AUTENTICACION, "Error de autenticación. Verifique las credenciales o el token proporcionado.");
        descError.put(ERRORES_SERVICIO_CECOBAN, "Respuesta no exitosa del servicio de cecoban, descripción: %s");
        descError.put(ERROR_SERVICIO_CECOBAN, "Curp no encontrada. Descripción de CECOBAN: %s");
        descError.put(ERROR_FORMATO_DETAIL_INVALIDO, "Error del servicio Cecoban: el campo 'detail' tiene un formato inválido o inesperado.");
        descError.put(ERROR_GENERA_CURP, "Hubo un error al generar la curp con los datos del OCR.");
        descError.put(ERROR_TEMPORAL, "Ocurrió un error temporal. Intenta nuevamente.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresValidaCurpCecoban() {
    }
}
