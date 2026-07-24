package mx.net.asp.asp_pago_management.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresFavoritos {
    public static final int REGISTRO_CODI_NOT_FOUND = -100;
    public static final int VERSION_APP = -101;
    public static final int VERSION_APP_BEN = -102;
    public static final int CLABE_NOT_FOUND = -103;
    public static final int TARJETA_NOT_FOUND = -104;
    public static final int TELEFONO_NOT_FOUND = -105;
    public static final int FAVORITO_EXISTENTE = -106;
    public static final int INSITUCION_NO_VALIDA = -107;
    public static final int ERROR_ID_TIPO_CUENTA = -108;
    public static final int ERROR_REGISTRA_FAVORITO = -109;
    public static final int FAVORITO_NOT_FOUND = -110;
    public static final int ERROR_ELIMINA_FAVORITO = -111;
    public static final int LISTA_FAVORITOS_NOT_FOUND = -112;
    public static final int CUENTA_NOT_FOUND = -113;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(REGISTRO_CODI_NOT_FOUND, "No se encontró el registro CoDi de la cuenta.");
        descError.put(VERSION_APP, "La cuenta no se encuentra en la nueva versión de la app.");
        descError.put(VERSION_APP_BEN, "La cuenta que intenta registrar no se encuentra en la nueva versión de la app.");
        descError.put(CLABE_NOT_FOUND, "El número ingresado no es una clabe admitida, por favor verifica los datos.");
        descError.put(TELEFONO_NOT_FOUND, "El número ingresado no es un teléfono admitido, por favor verifica los datos.");
        descError.put(TARJETA_NOT_FOUND, "El número ingresado no es un número de tarjeta admitido, por favor verifica los datos.");
        descError.put(FAVORITO_EXISTENTE, "Los datos del beneficiario que intenta guardar ya existen.");
        descError.put(INSITUCION_NO_VALIDA, "Lo sentimos, la institución bancaria no es válida (%s).");
        descError.put(ERROR_ID_TIPO_CUENTA, "Favor de proporcionar un tipo de cuenta válido.");
        descError.put(ERROR_REGISTRA_FAVORITO, "Lo sentimos, ocurrio un error al intentar registrar el favorito.");
        descError.put(FAVORITO_NOT_FOUND, "Lo sentimos, no se encontró favorito con los datos proporcionados.");
        descError.put(ERROR_ELIMINA_FAVORITO, "Lo sentimos, ocurrio un error al intentar eliminar el favorito.");
        descError.put(LISTA_FAVORITOS_NOT_FOUND, "Lo sentimos, no se encontró listado de favoritos para la cuenta (%s).");
        descError.put(CUENTA_NOT_FOUND, "El número ingresado no es una cuenta admitida, por favor verifica los datos.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en consulta cp.");
        return String.format(mensaje, param);
    }

    private ErroresFavoritos() {
    }
}
