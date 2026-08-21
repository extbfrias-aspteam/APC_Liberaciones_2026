package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresCodAutorizacion {

    public static final int ID_SERVICIO_NOT_FOUND = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int VERSION_APP = -102;
    public static final int CUENTA_NOT_FOUND = -103;
    public static final int COD_AUTORIZACION_INCORRECTO = -104;
    public static final int ERROR_VALIDAR_COD_AUTORIZACION = -105;
    public static final int ERROR_ACTUALIZAR_COD_AUTORIZACION = -106;
    public static final int PIN_NOT_FOUND = -107;
    public static final int PIN_EXPIRADO = -108;
    public static final int ERROR_INSERT_REG_COD_AUT = -109;
    public static final int COD_AUT_BLOQUEADO = -110;
    public static final int ERROR_DESACTIVAR_PIN = -111;
    public static final int ERROR_UPDATE_COD_AUT = -112;
    public static final int ERROR_MAXIMO_INTENTOS = -113;
    public static final int ERROR_INTENTOS = -114;
    public static final int PIN_OTP_INCORRECTO = -115;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ID_SERVICIO_NOT_FOUND, "El id del servicio proporcionado no es válido.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la informacion de la cuenta.");
        descError.put(COD_AUTORIZACION_INCORRECTO, "El código de autorizacion es incorrecto.");
        descError.put(ERROR_VALIDAR_COD_AUTORIZACION, "Lo sentimos, ocurrio un error al validar su código de autorización.");
        descError.put(ERROR_ACTUALIZAR_COD_AUTORIZACION, "Lo sentimos, ocurrio un error al actualizar su código de autorización.");
        descError.put(PIN_NOT_FOUND, "Lo sentimos, no se encontró un código de autorización activo para el solicitante.");
        descError.put(PIN_EXPIRADO, "El código de autorización ha expirado.");
        descError.put(ERROR_INSERT_REG_COD_AUT, "Ocurrió un error al intentar guardar el registro de codigo de autorizacion.");
        descError.put(COD_AUT_BLOQUEADO, "El código de autorización se encuentra bloqueado por múltiples intentos fallidos.");
        descError.put(ERROR_DESACTIVAR_PIN, "Ocurrió un error al desactivar el código de autorización.");
        descError.put(ERROR_UPDATE_COD_AUT, "Ocurrió un error al actualizar el estado del código de autorización.");
        descError.put(ERROR_MAXIMO_INTENTOS, "Por seguridad, tu código ha sido bloqueado tras varios intentos fallidos. Comunícate con nuestro centro de atención al telefonoSoporte para recibir ayuda.");
        descError.put(ERROR_INTENTOS, "Has ingresado un código incorrecto. Por tu seguridad, te %s antes de que tu código de seguridad se bloquee.");
        descError.put(PIN_OTP_INCORRECTO, "Pin OTP incorrecto.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
