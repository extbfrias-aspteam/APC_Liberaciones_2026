package mx.net.asp.asp_pago_onb.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresAltaBeneficiario {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int CUENTA_NOT_FOUND = -102;
    public static final int BENEFICIARIO_REGISTRADO = -103;
    public static final int ERROR_REGISTRA_BENEFICIARIO = -104;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "Lo sentimos, no se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la información de la cuenta.");
        descError.put(BENEFICIARIO_REGISTRADO, "La cuenta ya tiene un beneficiario registrado.");
        descError.put(ERROR_REGISTRA_BENEFICIARIO, "Lo sentimos, ocurrio un error al intentar registrar el beneficiario.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }
}
