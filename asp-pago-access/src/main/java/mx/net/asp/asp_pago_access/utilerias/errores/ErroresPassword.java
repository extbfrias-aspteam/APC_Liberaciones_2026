package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresPassword {

    public static final int SOLICITANTE_NOT_FOUND_P = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int VERSION_APP = -102;
    public static final int CORREO_NO_COINCIDE = -103;
    public static final int ERROR_PETICION_CAMBIO_PASS = -104;
    public static final int PASS_NO_COINCIDE = -105;
    public static final int PASS_NO_CUMPLE = -106;
    public static final int SOLICITANTE_NOT_FOUND = -107;
    public static final int ERROR_CAMBIO_PASS = -108;
    public static final int TOKEN_NO_VALIDO = -109;
    public static final int TOKEN_UTILIZADO = -110;
    public static final int TOKEN_EXPIRADO = -111;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(SOLICITANTE_NOT_FOUND_P, "No se encontró información del solicitante con teléfono %s.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CORREO_NO_COINCIDE, "El correo ingresado no es el que se tiene registrado para su cuenta.");
        descError.put(ERROR_PETICION_CAMBIO_PASS, "Lo sentimos, ocurrio un error en su solicitud de cambio de contraseña.");
        descError.put(PASS_NO_COINCIDE, "La contraseña y la confirmación no coinciden.");
        descError.put(PASS_NO_CUMPLE, "La nueva contraseña no cumple con las condiciones. (%s)");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró información del solicitante.");
        descError.put(ERROR_CAMBIO_PASS, "Lo sentimos, ocurrio un error al intentar realizar el cambio de contraseña.");
        descError.put(TOKEN_NO_VALIDO, "Lo sentimos, el token enviado no es válido.");
        descError.put(TOKEN_UTILIZADO, "Lo sentimos, el token enviado ya ha sido utilizado.");
        descError.put(TOKEN_EXPIRADO, "Lo sentimos, el token enviado ha expirado.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
