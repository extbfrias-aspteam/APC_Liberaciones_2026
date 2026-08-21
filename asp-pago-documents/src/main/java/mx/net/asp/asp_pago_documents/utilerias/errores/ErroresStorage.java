package mx.net.asp.asp_pago_documents.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresStorage {
    public static final int ARCHIVO_OBLIGATORIO = -300;
    public static final int CATEGORIA_OBLIGATORIA = -100;
    public static final int IDENTIFICADOR_OBLIGATORIO = -101;
    public static final int TIPO_ARCHIVO_NO_PERMITIDO = -102;
    public static final int ERROR_LECTURA_ARCHIVO = -103;
    public static final int ERROR_SUBIDA_STORAGE = -104;
    public static final int ERROR_GUARDADO_ARCHIVO = -105;
    public static final int ERROR_PERMISOS_ARCHIVO = -106;
    public static final int SOLICITANTE_NOT_FOUND = -107;
    public static final int ERROR_ACTUALIZAR_DOCUMENTACION_PENDIENTE = -108;
    public static final int ERROR_GUARDADO_ARCHIVO_BD = -109;
    public static final int ERROR_CONSULTAR_DOCUMENTACION_PENDIENTE = -110;
    public static final int CLIENTE_YA_TIENE_DOCUMENTACION_COMPLETA = -111;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ARCHIVO_OBLIGATORIO, "El archivo es obligatorio.");
        descError.put(CATEGORIA_OBLIGATORIA, "La categoría es obligatoria.");
        descError.put(IDENTIFICADOR_OBLIGATORIO, "El identificador es obligatorio.");
        descError.put(TIPO_ARCHIVO_NO_PERMITIDO, "Tipo de archivo no permitido %s.");
        descError.put(ERROR_LECTURA_ARCHIVO, "Ocurrió un error al leer el archivo: %s.");
        descError.put(ERROR_SUBIDA_STORAGE, "Ocurrió un error al subir el archivo %s a Google Cloud Storage.");
        descError.put(ERROR_GUARDADO_ARCHIVO, "Ocurrió un error al intentar guardar el archivo.");
        descError.put(ERROR_PERMISOS_ARCHIVO, "Ocurrió un error al intentar guardar el archivo.");
        descError.put(SOLICITANTE_NOT_FOUND, "No se encontró informacion del cliente.");
        descError.put(ERROR_ACTUALIZAR_DOCUMENTACION_PENDIENTE, "Lo sentimos, ocurrió un error al intentar actualizar su información.");
        descError.put(ERROR_GUARDADO_ARCHIVO_BD, "Lo sentimos, ocurrió un error al intentar guardar su información.");
        descError.put(ERROR_CONSULTAR_DOCUMENTACION_PENDIENTE, "Lo sentimos, ocurrió un error al consultar su información.");
        descError.put(CLIENTE_YA_TIENE_DOCUMENTACION_COMPLETA, "El cliente ya cuenta con su documentación actualizada.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido.");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en transferencia");
        return String.format(mensaje, param);
    }

    private ErroresStorage() {
    }
}
