package mx.net.asp.asp_pago_payments.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresPSpeiSimple {

    public static final int VERSION_APP_ORIGEN = -100;
    public static final int VERSION_APP_DESTINO = -101;
    public static final int SALDO_INSUFICIENTE = -102;
    public static final int REG_CUENTA_ORIGEN_BLOQUEADA = -103;
    public static final int REG_CUENTA_ORIGEN_BLOQUEADA_TEMPORAL = -104;
    public static final int OPERACION_CUENTA_DESTINO_BLOQUEADA = -105;
    public static final int CUENTA_ORIGEN_NOT_FOUND = -106;
    public static final int CUENTA_DESTINO_NOT_FOUND = -107;
    public static final int DATOS_TRANSACCION_INCOMPLETOS = -108;
    public static final int DEVOLUCION_CUENTA_ORIGEN = -109;
    public static final int LIMITE_TRANSACCIONAL_MENSUAL = -110;
    public static final int REGISTRO_CODI_NOT_FOUND = -111;
    public static final int ERROR_DEVOLUCION_CUENTA_ORIGEN = -112;
    public static final int ERROR_ID_TIPO_CUENTA = -113;
    public static final int OPERACION_PENDIENTE = -114;
    public static final int CREDITO_LIQUIDADO = -115;
    public static final int CREDITO_CONSOLIDADO = -116;
    public static final int ERROR_TESORERO = -117;
    public static final int ERROR_SPEI = -118;
    public static final int ERROR_CONSULTA_SALDO = -119;
    public static final int ERROR_CUENTAS_NO_COINCIDEN = -120;
    public static final int CUENTA_ORIGEN_BLOQUEADA = -121;
    public static final int CUENTA_ORIGEN_CANCELADA = -122;
    public static final int CUENTA_DESTINO_BLOQUEADA = -123;
    public static final int CUENTA_DESTINO_CANCELADA = -124;
    public static final int ERROR_CREDITO_WS = -125;
    public static final int POS_GLOBAL_ORDENANTE_NOT_FOUND = -126;
    public static final int SOLICITANTE_ORDENANTE_NOT_FOUND = -127;
    public static final int ERROR_ESPERA_PARA_NUEVO_ENVIO = -128;
    public static final int ERROR_ESTATUS_CUENTA = -129;
    public static final int ERROR_CLABE_EMPTY = -130;
    public static final int ERROR_TIEMPO_MINIMO_FAVORITO = -131;
    public static final int ERROR_MONTO_MAX_SIN_FAVORITO = -132;
    public static final int ERROR_FRAUDE = -133;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(VERSION_APP_ORIGEN, "No se pudo realizar el pago, la cuenta origen no se encuentra en la nueva versión de la app.");
        descError.put(VERSION_APP_DESTINO, "No se pudo realizar el pago, la cuenta destino no se encuentra en la nueva versión de la app.");
        descError.put(SALDO_INSUFICIENTE, "No se pudo realizar el pago, saldo insuficiente.");
        descError.put(REG_CUENTA_ORIGEN_BLOQUEADA, "No se pudo realizar el pago, el registro de la cuenta origen esta bloqueado, debe acudir a sucursal para desbloqueo.");
        descError.put(REG_CUENTA_ORIGEN_BLOQUEADA_TEMPORAL, "No se pudo realizar el pago, el registro de la cuenta origen de la cuenta origen esta temporalmente bloqueado.");
        descError.put(OPERACION_CUENTA_DESTINO_BLOQUEADA, "No se puede realizar el pago, la operación no se encuentra permitida para la cuenta destino.");
        descError.put(CUENTA_ORIGEN_NOT_FOUND, "No se pudo realizar el pago, no se pudo identificar la cuenta de origen.");
        descError.put(CUENTA_DESTINO_NOT_FOUND, "No se pudo realizar el pago, no se pudo identificar la cuenta beneficiaria. %s");
        descError.put(DATOS_TRANSACCION_INCOMPLETOS, "No se pudo realizar el pago, datos de la transacción incompletos.");
        descError.put(DEVOLUCION_CUENTA_ORIGEN, "No se pudo procesar el pago, por lo que el monto fue reembolsado a su cuenta de ahorro.");
        descError.put(LIMITE_TRANSACCIONAL_MENSUAL, "No se pudo realizar el pago, la cuenta beneficiaria superó el límite de depósitos mensuales.");
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se pudo realizar el pago, no se encontró el registro CoDi de la cuenta.");
        descError.put(ERROR_DEVOLUCION_CUENTA_ORIGEN, "No se pudo realizar el pago, ocurrió un error al intentar hacer el reembolso a su cuenta de ahorro, comunicate a contact center.");
        descError.put(ERROR_ID_TIPO_CUENTA, "Favor de proporcionar un tipo de cuenta válido.");
        descError.put(OPERACION_PENDIENTE, "No se pudo realizar el pago, existen operaciones pendientes, vuelve a intentarlo en 15 minutos.");
        descError.put(CREDITO_LIQUIDADO, "No se pudo realizar el pago, el credito ya se encuentra liquidado.");
        descError.put(CREDITO_CONSOLIDADO, "No se pudo realizar el pago, no se puede aplicar pagos a creditos consolidados.");
        descError.put(ERROR_TESORERO, "No se pudo realizar el pago, acción no permitida.");
        descError.put(ERROR_SPEI, "No se pudo realizar el pago, ocurrio un error al procesar el SPEI.");
        descError.put(ERROR_CONSULTA_SALDO, "No se pudo realizar el pago, ocurrio un error al consultar el saldo de la cuenta.");
        descError.put(ERROR_CUENTAS_NO_COINCIDEN, "ERROR: La cuenta ordenante no coincide.");
        descError.put(CUENTA_ORIGEN_BLOQUEADA, "No se pudo realizar el pago, la cuenta origen esta bloqueada.");
        descError.put(CUENTA_ORIGEN_CANCELADA, "No se pudo realizar el pago, la cuenta origen esta cancelada.");
        descError.put(CUENTA_DESTINO_BLOQUEADA, "No se pudo realizar el pago, la cuenta destino esta bloqueada.");
        descError.put(CUENTA_DESTINO_CANCELADA, "No se pudo realizar el pago, la cuenta destino esta cancelada.");
        descError.put(ERROR_CREDITO_WS, "Lo sentimos, %s.");
        descError.put(POS_GLOBAL_ORDENANTE_NOT_FOUND, "No se pudo procesar el pago, no se encontró la clabe del ordenante.");
        descError.put(SOLICITANTE_ORDENANTE_NOT_FOUND, "No se pudo procesar el pago, no se encontró la información del ordenante.");
        descError.put(ERROR_ESPERA_PARA_NUEVO_ENVIO, "Debe esperar %s segundos para realizar su siguiente envio de dinero.");
        descError.put(ERROR_ESTATUS_CUENTA, "Error. Transacción no realizada, favor de intentarlo en unos minutos. (%s)");
        descError.put(ERROR_CLABE_EMPTY, "No se pudo realizar el pago, no se pudo identificar la cuenta de origen. (%s)");
        descError.put(ERROR_MONTO_MAX_SIN_FAVORITO, "Solo puedes transferir hasta %s a beneficiarios no registrados como favoritos.\n" +
                "Agrégalo a favoritos para enviar montos mayores.");
        descError.put(ERROR_TIEMPO_MINIMO_FAVORITO, "Podrás transferirle una vez que se cumplan %s minutos desde su registro como favorito.");
        descError.put(ERROR_FRAUDE, "Error. Transacción no realizada, favor de intentarlo en unos minutos. (MP)." );
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
