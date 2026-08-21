package mx.net.asp.asp_pago_payments.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresProcesa {

    public static final int CUENTA_NOT_FOUND = -100;
    public static final int DEVOLUCION_CUENTA_ORIGEN = -101;
    public static final int CUENTA_ORIGEN_BLOQUEADA = -102;
    public static final int CUENTA_ORIGEN_CANCELADA = -103;
    public static final int ERROR_VFWS = -104;
    public static final int ERROR_ESTATUS_CUENTA = -105;
    public static final int SALDO_INSUFICIENTE = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NOT_FOUND, "No se pudo realizar el pago, no se pudo identificar la cuenta de origen.");
        descError.put(DEVOLUCION_CUENTA_ORIGEN, "No se pudo procesar el pago, por lo que el monto fue reembolsado a su cuenta de ahorro.");
        descError.put(CUENTA_ORIGEN_BLOQUEADA, "No se pudo realizar el pago, la cuenta origen esta bloqueada.");
        descError.put(CUENTA_ORIGEN_CANCELADA, "No se pudo realizar el pago, la cuenta origen esta cancelada.");
        descError.put(ERROR_VFWS, "%s.");
        descError.put(ERROR_ESTATUS_CUENTA, "Error. Transacción no realizada, favor de intentarlo en unos minutos. (%s)");
        descError.put(SALDO_INSUFICIENTE, "No se pudo realizar el pago, saldo insuficiente.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
