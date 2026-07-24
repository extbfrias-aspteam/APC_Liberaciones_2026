package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresValidacionTelefono {
    public static final int ERROR_SEMILLA_TOKEN = -100;
    public static final int ERROR_REGISTRA_TOKEN = -101;
    public static final int TOKEN_NOT_FOUND = -102;
    public static final int TOKEN_UTILIZADO = -103;
    public static final int TOKEN_EXPIRADO = -104;
    public static final int ERROR_ACTUALIZAR_TOKEN = -105;
    public static final int TELEFONO_FOUND_CURP_NOT_FOUND = -106;
    public static final int CURP_FOUND_TELEFONO_NOT_FOUND = -107;
    public static final int TELEFONO_NUEVA_VERSION = -108;
    public static final int CURP_NUEVA_VERSION = -109;
    public static final int TELEFONO_DIF_CURP = -110;
    public static final int CURP_TELEFONO_NUEVA_VERSION = -111;
    public static final int SOLICITANTE_NOT_FOUND = -112;
    public static final int REGISTRO_CODI_NOT_FOUND = -113;
    public static final int VERSION_APP = -114;
    public static final int ERROR_ACTUALIZA_DISP = -115;
    public static final int PARAM_NOT_FOUND = -116;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ERROR_SEMILLA_TOKEN, "Lo sentimos, ocurrio un error al generar el token.");
        descError.put(ERROR_REGISTRA_TOKEN, "Lo sentimos, ocurrio un error al registrar el token.");
        descError.put(TOKEN_NOT_FOUND, "Lo sentimos, el token enviado no es válido.");
        descError.put(TOKEN_UTILIZADO, "Lo sentimos, el token enviado ya fue utilizado.");
        descError.put(TOKEN_EXPIRADO, "Lo sentimos, el token enviado ha expirado.");
        descError.put(ERROR_ACTUALIZAR_TOKEN, "Lo sentimos, ocurrio un error al validar el token.");
        descError.put(TELEFONO_FOUND_CURP_NOT_FOUND, "El curp que ingresaste no se encuentra ligado al número telefónico.\nFavor de validar que los datos ingresados estén correctos.");
        descError.put(CURP_FOUND_TELEFONO_NOT_FOUND, "El número telefónico que ingresaste no coincide con el que se tiene registrado para este CURP.\nPor favor comunícate a Contact Center.\n800 462 7373.");
        descError.put(TELEFONO_NUEVA_VERSION, "El número telefónico ya se encuentra registrado, favor de iniciar sesión.");
        descError.put(CURP_NUEVA_VERSION, "El curp ya se encuentra registrado, favor de iniciar sesión.");
        descError.put(TELEFONO_DIF_CURP, "El número telefónico ingresado ya se encuentra registrado con otro cliente.\nPor favor comunícate a Contact Center.\n800 462 7373.");
        descError.put(CURP_TELEFONO_NUEVA_VERSION, "El curp y el teléfono ya se encuentran registrados, favor de iniciar sesión.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontro informacion del solicitante con teléfono %s.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(ERROR_ACTUALIZA_DISP, "Lo sentimos, ocurrio un error al validar su dispositivo.");
        descError.put(PARAM_NOT_FOUND, "Parámetro validaCuenta [%s] no reconocido.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en consulta cp.");
        return String.format(mensaje, param);
    }

    private ErroresValidacionTelefono() {
    }
}
