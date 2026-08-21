package mx.net.asp.asp_pago_jwt.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresValidaJwtToken {

    public static final int JWT_MISSING = -400;
    public static final int JWT_VALID_NOT_FOUND = -401;
    public static final int JWT_EXPIRED = -402;
    public static final int JWT_EXPIRED_NOT_FOUND = -403;
    public static final int JWT_REVOKED = -404;
    public static final int JWT_REVOKED_NOT_FOUND = -405;
    public static final int JWT_INVALID = -406;
    public static final int SOLICITANTE_NOT_FOUND_TELEFONO = -407;
    public static final int SOLICITANTE_NOT_FOUND_PERSONA_ID = -408;
    public static final int ACTUALIZA_ID_HARDWARE = -4;
    public static final int ERROR_ID_HARDWARE = -409;
    public static final int ERROR_TELEFONO = -410;
    public static final int JWT_UNAUTHORIZED = -411;
    public static final int JWT_EXPIRED_BD_EXPIRED = -412;
    public static final int JWT_VALID_REVOKED = -413;
    public static final int JWT_EXPIRED_REVOKED = -414;
    public static final int TENANT_ID_NOT_FOUND = -415;
    public static final int TENANT_ID_NOT_ACTIVE = -416;
    public static final int ID_CANAL_NOT_MATCH = -417;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(JWT_MISSING, "X-JWT-Token header missing.");
        descError.put(JWT_VALID_NOT_FOUND, "JWT VALIDO NO ENCONTRADO EN BASE DE DATOS.");
        descError.put(JWT_EXPIRED, "TOKEN EXPIRADO POR JWT ENCONTRADO COMO VIGENTE, SE EXPIRA EN BD.");
        descError.put(JWT_EXPIRED_NOT_FOUND, "JWT EXPIRADO NO ENCONTRADO EN BASE DE DATOS.");
        descError.put(JWT_REVOKED, "JWT REVOCADO EN BD, MAL FIRMADO POR CAMBIO DE LLAVE.");
        descError.put(JWT_REVOKED_NOT_FOUND, "JWT MAL FIRMADO NO ENCONTRADO EN BD COMO REVOCADO: %s.");
        descError.put(JWT_INVALID, "JWT INVALIDO: %s.");
        descError.put(SOLICITANTE_NOT_FOUND_TELEFONO, "SOLICITANTE NO ENCONTRADO CON TELEFONO: %s.");
        descError.put(SOLICITANTE_NOT_FOUND_PERSONA_ID, "SOLICITANTE NO ENCONTRADO CON ID CLIENTE: %s.");
        descError.put(ACTUALIZA_ID_HARDWARE, "Validación de dispositivo\n Le enviaremos un SMS para validar su dispositivo.");
        descError.put(ERROR_ID_HARDWARE, "Dispositivo ID del JWT no coincide con el que se tiene registrado para este cliente.|ID HARDWARE CLAIMS: %s|ID HARDWARE CLIENTE: %s");
        descError.put(ERROR_TELEFONO, "Telefono del JWT no coincide con el que se tiene registrado para este cliente.|TELEFONO CLAIMS: %s|TELEFONO CLIENTE: %s");
        descError.put(JWT_UNAUTHORIZED, "No estas autorizado para realizar esta operación.");
        descError.put(JWT_EXPIRED_BD_EXPIRED, "TOKEN EXPIRADO POR JWT, ENCONTRADO COMO EXPIRADO EN BD.");
        descError.put(JWT_VALID_REVOKED, "JWT TOKEN VALIDO PERO REVOCADO EN BASE DE DATOS.");
        descError.put(JWT_EXPIRED_REVOKED, "JWT TOKEN EXPIRADO PERO REVOCADO EN BASE DE DATOS.");
        descError.put(TENANT_ID_NOT_FOUND, "No se encontro tenant ID para el canal %s.");
        descError.put(TENANT_ID_NOT_ACTIVE, "Tenant ID inactivo para el canal %s.");
        descError.put(ID_CANAL_NOT_MATCH, "El Id Canal no es válido para a este token.");
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
