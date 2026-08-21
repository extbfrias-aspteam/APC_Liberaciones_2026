package mx.net.asp.asp_pago_access.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresLogIn {

    public static final int FORMATO_TELEFONO = -100;
    public static final int REGISTRO_CODI_NOT_FOUND = -101;
    public static final int SOLICITANTE_NOT_FOUND = -102;
    public static final int VERSION_APP = -103;
    public static final int CUENTA_NOT_FOUND = -104;
    public static final int REG_CODI_BLOQUEADO = -105;
    public static final int REG_CODI_BLOQUEADO_TEMP = -106;
    public static final int TELEFONO_NO_VALIDADO = -107;
    public static final int ERROR_ACTUALIZA_ID_HARDWARE = -108;
    public static final int ACTUALIZA_ID_HARDWARE = -4;
    public static final int ERROR_SESION_ACTIVA = -110;
    public static final int SESION_ACTIVA = -111;
    public static final int CUENTA_BLOQUEADA = -112;
    public static final int CUENTA_CANCELADA = -113;
    public static final int CREDENCIALES_INCORRECTAS = -114;
    public static final int CLIENTE_SIN_CUENTAS = -115;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(FORMATO_TELEFONO, "Formato del teléfono no válido.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "Lo sentimos, no se encontró el registro de la cuenta.");
        descError.put(SOLICITANTE_NOT_FOUND, "Lo sentimos, no se encontro información de cliente con teléfono %s.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CUENTA_NOT_FOUND, "Lo sentimos, no se pudo obtener la información de la cuenta.");
        descError.put(REG_CODI_BLOQUEADO, "Por seguridad, su ingreso a ASP Pago ha sido bloqueado, por favor comunicate a Contact Center.\n800 462 7373.");
        descError.put(REG_CODI_BLOQUEADO_TEMP, "Por seguridad, su ingreso a ASP Pago ha sido bloqueado temporalmente.\n¡Reintenta en 10 minutos!.");
        descError.put(TELEFONO_NO_VALIDADO, "Lo sentimos, su teléfono NO está validado.");
        descError.put(ERROR_ACTUALIZA_ID_HARDWARE, "Lo sentimos, ocurrio un error al actualizar el id del dispositivo.");
        descError.put(ACTUALIZA_ID_HARDWARE, "Validación de dispositivo\n Le enviaremos un SMS para validar su dispositivo.");
        descError.put(ERROR_SESION_ACTIVA, "Lo sentimos, ocurrio un error al obtener información de la sesión.");
        descError.put(SESION_ACTIVA, "Hay una sesión abierta recientemente. Intenta de nuevo en 5 minutos.");
        descError.put(CUENTA_BLOQUEADA, "Tu cuenta se encuentra bloqueada, para desbloquearlo comuníquese a Contact Center.");
        descError.put(CUENTA_CANCELADA, "Esta cuenta se encuentra cancelada, comuníquese a Contact Center.");
        descError.put(CREDENCIALES_INCORRECTAS, "La contraseña que ingresaste no es correcta. Inténtalo de nuevo.");
        descError.put(CLIENTE_SIN_CUENTAS, "Lo sentimos, el cliente no cuenta con cuentas vigentes.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
