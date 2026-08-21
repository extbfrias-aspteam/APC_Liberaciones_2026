package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresResetPassword {

    public static final int SOLICITANTE_NOT_FOUND_P = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int VERSION_APP = -102;
    public static final int DEVICE_ID_NO_COINCIDE = -103;
    public static final int ERROR_PETICION_CAMBIO_PASS = -104;
    public static final int PASS_NO_COINCIDE = -105;
    public static final int PASS_NO_CUMPLE = -106;
    public static final int SOLICITANTE_NOT_FOUND = -107;
    public static final int ERROR_CAMBIO_PASS = -108;
    public static final int OTP_NO_VALIDO = -109;
    public static final int OTP_EXPIRADO = -110;
    public static final int SOLICITUD_OTP_BLOQUEADA = -111;
    public static final int LIMITE_SUPERADO = -112;
    public static final int RESET_SESSION_NO_VALIDO = -113;
    public static final int RESET_SESSION_EXPIRADO = -114;
    public static final int CLIENTE_SIN_CUENTAS_VIGENTES = -115;
    public static final int ERROR_CIF = -116;


    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(SOLICITANTE_NOT_FOUND_P, "No se encontró información del solicitante con teléfono %s.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(ERROR_PETICION_CAMBIO_PASS, "Lo sentimos, ocurrio un error en su solicitud de cambio de contraseña.");
        descError.put(PASS_NO_COINCIDE, "La contraseña y la confirmación no coinciden.");
        descError.put(DEVICE_ID_NO_COINCIDE, "El deviceId no coincide con el que se tiene registrado.");
        descError.put(PASS_NO_CUMPLE, "La nueva contraseña no cumple con las condiciones. (%s)");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró información del solicitante.");
        descError.put(ERROR_CAMBIO_PASS, "Lo sentimos, ocurrio un error al intentar realizar el cambio de contraseña.");
        descError.put(OTP_NO_VALIDO, "Lo sentimos, el código enviado no es válido.");
        descError.put(OTP_EXPIRADO, "Lo sentimos, el código enviado ha expirado.");
        descError.put(SOLICITUD_OTP_BLOQUEADA, "Superaste el número máximo de intentos. Por favor, intenta de nuevo en %s minutos.");
        descError.put(LIMITE_SUPERADO, "Superaste el número máximo de intentos. Por favor, intenta de nuevo en %s minutos.");
        descError.put(RESET_SESSION_NO_VALIDO, "Lo sentimos, el session token enviado no es válido.");
        descError.put(RESET_SESSION_EXPIRADO, "Lo sentimos, el session token enviado ha expirado.");
        descError.put(CLIENTE_SIN_CUENTAS_VIGENTES, "Lo sentimos, no se encontraron cuentas vigentes para el cliente.");
        descError.put(ERROR_CIF, "Ocurrió un error al desencriptar la contraseña.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
