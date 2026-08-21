package mx.net.asp.asp_pago_onb.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresValidacionCorreo {
    public static final int PARAMETRO_ACCION = -100;
    public static final int CORREO_REGISTRADO = -101;
    public static final int ENVIO_CORREO = -102;
    public static final int REGISTRO_NOT_FOUND = -103;
    public static final int CORREO_NO_COINCIDE = -104;
    public static final int VALIDACION_CORREO = -105;
    public static final int VERIFICACION_CORREO = -106;

    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(PARAMETRO_ACCION, "Parámetro Accion [%s] no reconocido.");
        descError.put(CORREO_REGISTRADO, "El correo ingresado ya se encuentra registrado.");
        descError.put(ENVIO_CORREO, "Lo sentimos, ocurrio un error en el envio del correo.");
        descError.put(REGISTRO_NOT_FOUND, "Registro no encontrado para ser validado.");
        descError.put(CORREO_NO_COINCIDE, "El correo electrónico a validar no coincide.");
        descError.put(VALIDACION_CORREO, "Lo sentimos, ocurrio un error al validar su correo.");
        descError.put(VERIFICACION_CORREO, "El correo electrónico no ha sido verificado.");
    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en transferencia");
    }

    public static String getMensajeError(int codigo, String param) {
        String mensaje = descError.getOrDefault(codigo, "Error desconocido en validacion de correo.");
        return String.format(mensaje, param);
    }
}
