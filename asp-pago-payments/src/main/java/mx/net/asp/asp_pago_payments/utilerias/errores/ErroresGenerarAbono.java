package mx.net.asp.asp_pago_payments.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresGenerarAbono {

    public static final int CUENTA_DESTINO_NOT_FOUND = -100;
    public static final int LIMITE_TRANSACCIONAL_MENSUAL = -101;
    public static final int OPERACION_PENDIENTE = -102;
    public static final int TIPO_CREDITO = -103;
    public static final int CUENTA_DESTINO_BLOQUEADA = -104;
    public static final int CUENTA_DESTINO_CANCELADA = -105;
    public static final int ERROR_CLABE = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_DESTINO_NOT_FOUND, "No se pudo realizar el pago, no se pudo identificar la cuenta beneficiaria.");
        descError.put(LIMITE_TRANSACCIONAL_MENSUAL, "No se pudo realizar el pago, la cuenta beneficiaria superó el límite de depósitos mensuales.");
        descError.put(OPERACION_PENDIENTE, "No se pudo realizar el pago, existen operaciones pendientes, vuelve a intentarlo en 15 minutos.");
        descError.put(TIPO_CREDITO, "No se pudo realizar el pago, la operacion no esta permitida para el tipo de credito.");
        descError.put(CUENTA_DESTINO_BLOQUEADA, "No se pudo realizar el pago, la cuenta destino esta bloqueada.");
        descError.put(CUENTA_DESTINO_CANCELADA, "No se pudo realizar el pago, la cuenta destino esta cancelada.");
        descError.put(ERROR_CLABE, "Favor de proporcionar una clabe de 18 dígitos.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }
}
