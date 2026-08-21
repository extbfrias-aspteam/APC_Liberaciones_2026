package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresEstadoCuentaInversion {

    public static final int CUENTA_NO_EXISTE = -100;
    public static final int NO_HAY_DATOS_EDO_CUENTA = -101;
    public static final int NOT_CORTE_MES = -102;
    public static final int INVALID_DATE = -103;
    public static final int AHORRO_CONTRATO_NOT_FOUND = -104;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(CUENTA_NO_EXISTE, "La cuenta no existe o no se encuentra activa, por lo que no es posible generar el estado de cuenta.");
        descError.put(NO_HAY_DATOS_EDO_CUENTA, "El estado de cuenta seleccionado aún no está disponible.\n Podrás descargarlo cuando se complete su corte mensual.");
        descError.put(NOT_CORTE_MES, "El corte del mes %s aun no esta disponible.");
        descError.put(INVALID_DATE, "El mes solicitado aun no ocurre.");
        descError.put(AHORRO_CONTRATO_NOT_FOUND, "No se encontro el ahorro contrato de la cuenta de inversion.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}
