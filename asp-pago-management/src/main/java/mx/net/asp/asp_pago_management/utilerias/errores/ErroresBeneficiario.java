package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresBeneficiario {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int BENEFICIARIO_NOT_FOUND = -102;
    public static final int DETALLE_BENEFICIARIO_EXISTS = -103;
    public static final int DETALLE_BENEFICIARIO_COMPLETE = -104;
    public static final int REGISTRAR_DETALLE_BENEFICIARIO = -105;
    public static final int ACTUALIZAR_DETALLE_BENEFICIARIO = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(BENEFICIARIO_NOT_FOUND, "La cuenta no tiene un beneficiario registrado.");
        descError.put(DETALLE_BENEFICIARIO_EXISTS, "El detalle del beneficiario ya se encuentra registrado.");
        descError.put(DETALLE_BENEFICIARIO_COMPLETE, "El beneficiario tiene los datos completos.");
        descError.put(REGISTRAR_DETALLE_BENEFICIARIO, "Lo sentimos, ocurrió un error al intentar registrar el detalle del beneficiario.");
        descError.put(ACTUALIZAR_DETALLE_BENEFICIARIO, "Lo sentimos, ocurrió un error al intentar actualizar el estatus del detalle del beneficiario.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia.");
        return String.format(mensaje, param);
    }

    private ErroresBeneficiario() {
    }
}
