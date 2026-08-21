package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresEstadoMov {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int REG_CODI_BLOQUEADO = -102;
    public static final int REG_CODI_BLOQUEADO_TEMP = -103;
    public static final int POSICION_GLOBAL_NOT_FOUND = -104;
    public static final int SOLICITANTE_NOT_FOUND = -105;
    public static final int RANGO_FECHAS_NO_VALIDO = -106;
    public static final int MOVIMIENTOS_NOT_FOUND = -107;
    public static final int DATOS_DOC = -108;
    public static final int GENERA_DOC = -109;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta %s.");
        descError.put(VERSION_APP, "La cuenta %s no se encuentra en la nueva versión de la app.");
        descError.put(REG_CODI_BLOQUEADO, "El registro de la cuenta origen %s está bloqueado, debe acudir a sucursal para desbloqueo.");
        descError.put(REG_CODI_BLOQUEADO_TEMP, "El registro de la cuenta origen %s está temporalmente bloqueado.");
        descError.put(POSICION_GLOBAL_NOT_FOUND, "No se encontró el registro de posición global de la cuenta %s.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró información del solicitante %s.");
        descError.put(RANGO_FECHAS_NO_VALIDO, "La fecha inicial no puede ser mayor a la fecha final.");
        descError.put(MOVIMIENTOS_NOT_FOUND, "Lo sentimos, no se encontraron movimientos en el periodo seleccionado.");
        descError.put(DATOS_DOC, "Ocurrió un error al obtener los datos para generar la documentación.");
        descError.put(GENERA_DOC, "Ocurrió un error al generar la documentación.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }
}