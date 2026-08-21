package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresConsultaSaldo {
    public static final int CUENTA_NOT_FOUND = -100;
    public static final int TARJETA_NO_VALIDA = -101;
    public static final int ERROR_CONSULTA_SALDO = -102;
    public static final int ERROR_ADMIN_PLASTICOS = -103;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NOT_FOUND, "No se pudo obtener la informacion de la cuenta.");
        descError.put(TARJETA_NO_VALIDA, "La cuenta no tiene una tarjeta válida.");
        descError.put(ERROR_CONSULTA_SALDO, "Lo sentimos, ocurrio un error al consultar el saldo.");
        descError.put(ERROR_ADMIN_PLASTICOS, "%s.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en consulta cp.");
        return String.format(mensaje, param);
    }

    private ErroresConsultaSaldo() {
    }
}
