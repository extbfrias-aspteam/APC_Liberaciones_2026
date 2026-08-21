package mx.net.asp.asp_pago_services_v2.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresSubeCuentaNivel {
    public static final int CUENTA_NOT_FOUND = -100;
    public static final int TIPO_CUENTA_INVALIDO = -101;
    public static final int REGISTRO_CODI_NOT_FOUND = -102;
    public static final int VERSION_APP = -103;
    public static final int REG_CODI_BLOQUEADO = -104;
    public static final int NIVEL_CUENTA = -105;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NOT_FOUND, "Lo sentimos, no se pudo obtener la información de la cuenta %s.");
        descError.put(TIPO_CUENTA_INVALIDO, "Lo sentimos, el producto de la cuenta no permite subir de nivel.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "Lo sentimos, no se encontró el registro de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(REG_CODI_BLOQUEADO, "Su regisrto se encuentra bloqueado, por favor comunicate a Contact Center.\n800 462 7373.");
        descError.put(NIVEL_CUENTA, "La cuenta ya es una cuenta de nivel 4.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresSubeCuentaNivel() {
    }
}
