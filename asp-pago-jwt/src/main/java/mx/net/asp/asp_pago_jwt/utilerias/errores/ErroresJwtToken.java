package mx.net.asp.asp_pago_jwt.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresJwtToken {

    public static final int FORMATO_TELEFONO = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int SOLICITANTE_NOT_FOUND = -102;
    public static final int VERSION_APP = -103;
    public static final int ACTUALIZA_ID_HARDWARE = -4;
    public static final int ERROR_ID_HARDWARE = -104;
    public static final int JWT_UNAUTHORIZED = -401;
    public static final int JWT_REFRESH_VIG_NOT_FOUND_BD = -105;
    public static final int JWT_REFRESH_NOT_VIG = -406;
    public static final int JWT_REFRESH_VIG_NOT_FOUND = -407;
    public static final int JWT_EXPIRED = -106;
    public static final int UNAUTHORIZED_GENERIC = -200;
    public static final int TENANT_ID_NOT_FOUND = -408;
    public static final int TENANT_ID_DIFERENT = -409;
    public static final int TENANT_ID_NOT_ACTIVE = -410;
    public static final int ID_HARDWARE_RST_PWD = -999;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(FORMATO_TELEFONO, "Formato del teléfono no válido.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(SOLICITANTE_NOT_FOUND, "No existe una cuenta registrada con este número de telefono. Verificalo e inténtalo nuevamente.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(ERROR_ID_HARDWARE, "Error se detecto dispositivo diferente.");
        descError.put(ACTUALIZA_ID_HARDWARE, "Validación de dispositivo\n Le enviaremos un SMS para validar su dispositivo.");
        descError.put(JWT_UNAUTHORIZED, "No estas autorizado para realizar esta operación.");
        descError.put(JWT_REFRESH_VIG_NOT_FOUND_BD, "No estas autorizado para realizar esta operación.");
        descError.put(JWT_REFRESH_NOT_VIG, "Refresh Token no vigente.");
        descError.put(UNAUTHORIZED_GENERIC, "No estas autorizado para acceder a la aplicacion.");
        descError.put(JWT_EXPIRED, "TOKEN EXPIRADO POR JWT ENCONTRADO COMO VIGENTE, SE EXPIRA EN BD.");
        descError.put(JWT_REFRESH_VIG_NOT_FOUND, "TOKEN NO ENCONTRADO COMO VIGENTE EN BD.");
        descError.put(TENANT_ID_NOT_FOUND, "No se encontro tenant ID para el canal %s.");
        descError.put(TENANT_ID_DIFERENT, "El tenant ID %s para el canal %s no es igual al registrado en base de datos.");
        descError.put(TENANT_ID_NOT_ACTIVE, "Tenant ID inactivo para el canal %s.");
        descError.put(ID_HARDWARE_RST_PWD, "Generacion de token con ID Hardware diferente para cambio de password.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }

    public static String getMensajeError(int codigo, String param, String param2) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param, param2);
    }
}
