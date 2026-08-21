package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresPinOTP {

    public static final int SOLICITANTE_NOT_FOUND_TEL = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int VERSION_APP = -102;
    public static final int ERROR_PETICION_PIN_OTP = -103;
    public static final int SOLICITANTE_NOT_FOUND_CTA = -104;
    public static final int CLAVE_NO_VALIDA = -105;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(SOLICITANTE_NOT_FOUND_TEL, "No se encontró información del solicitante con teléfono %s.");
        descError.put(SOLICITANTE_NOT_FOUND_CTA, "No se encontró información del solicitante con cuenta %s.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(ERROR_PETICION_PIN_OTP, "Lo sentimos, ocurrio un error en la generación de su solicitud de PIN OTP.");
        descError.put(CLAVE_NO_VALIDA, "Lo sentimos, la clave proporcionada no es válida (%s).");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en pin otp.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en pin otp");
        return String.format(mensaje, param);
    }
}
