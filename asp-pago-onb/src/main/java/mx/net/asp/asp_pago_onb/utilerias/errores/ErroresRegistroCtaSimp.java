package mx.net.asp.asp_pago_onb.utilerias.errores;

import java.util.HashMap;
import java.util.Map;

public class ErroresRegistroCtaSimp {
    public static final int ONBOARDING_PENDIENTE = -100;
    public static final int TERMINOS_Y_CONDICIONES = -101;
    public static final int SOLICITANTE_BLOQUEADO = -102;
    public static final int TELEFONO_REGISTRADO = -103;
    public static final int CREACION_CUENTA = -104;
    public static final int CREACION_REGISTRO_CODI = -105;
    public static final int DATOS_ADICIONALES_CUENTA = -106;
    public static final int ACTIVAR_CUENTA = -107;
    public static final int ACTUALIZA_VERSION_APP = -108;
    public static final int GUARDA_POS_GLOBAL = -109;
    public static final int ERROR_REGISTRA_TELEFONO_CODI = -110;
    public static final int ERROR_REGISTRA_SOLICITANTE = -111;
    public static final int ERROR_DATOS_INCOMPLETOS = -112;
    public static final int ERROR_VERSION_CUENTA = -113;
    public static final int INFO_SOLICITANTE_NOT_FOUND = -114;
    public static final int ACTUALIZA_SOLICITANTE = -115;
    public static final int ACTUALIZAR_REGISTRO_CODI = -116;
    public static final int ERROR_VALIDA_CURP = -117;
    public static final int ERROR_VALIDA_CURP_NUBARIUM = -118;
    public static final int ERROR_CONCEPTOS_PRODUCTO = -119;
    public static final int ERROR_CODIGO_REFERENCIA_NOT_FOUND = -120;
    public static final int ERROR_REGISTRAR_CODIGO = -121;


    public static final Map<Integer, String> descError = new HashMap<>();

    static {
        descError.put(ONBOARDING_PENDIENTE, "Lo sentimos, no se puede realizar el onboarding, existe un registro pendiente.");
        descError.put(TERMINOS_Y_CONDICIONES, "Es necesario que acepte el uso de terminos y condiciones.");
        descError.put(SOLICITANTE_BLOQUEADO, "No fue posible realizar el registro, El solicitante esta bloqueado.");
        descError.put(TELEFONO_REGISTRADO, "El teléfono ya se encuentra registrado para otro cliente. Favor de comunicarte con Contact Center.");
        descError.put(CREACION_CUENTA, "Lo sentimos, ocurrio un error al generar la cuenta.");
        descError.put(CREACION_REGISTRO_CODI, "Lo sentimos, ocurrio un error al generar el registro CoDi.");
        descError.put(DATOS_ADICIONALES_CUENTA, "Lo sentimos, ocurrio un error al dar de alta datos adicionales de la cuenta.");
        descError.put(ACTIVAR_CUENTA, "Lo sentimos, ocurrio un error al activar su cuenta.");
        descError.put(ACTUALIZA_VERSION_APP, "Lo sentimos, ocurrio un error al actualizar la version de la app.");
        descError.put(GUARDA_POS_GLOBAL, "Lo sentimos, ocurrio un error al guardar la cuenta.");
        descError.put(ERROR_REGISTRA_TELEFONO_CODI, "Lo sentimos, ocurrio un error al registrar el número telefónico.");
        descError.put(ERROR_REGISTRA_SOLICITANTE, "Lo sentimos, ocurrio un error al registrar al solicitante.");
        descError.put(ERROR_DATOS_INCOMPLETOS, "Lo sentimos, ocurrio un error al obtener los datos para registrar al solicitante.");
        descError.put(ERROR_VERSION_CUENTA, "Lo sentimos, la cuenta que esta intentando registrar ya se encuentra actualizada en la nueva versión de la app.");
        descError.put(INFO_SOLICITANTE_NOT_FOUND, "Lo sentimos, no se encontró informacion completa del solicitante.");
        descError.put(ACTUALIZA_SOLICITANTE, "Lo sentimos, ocurrio un error al actualizar los datos del solicitante.");
        descError.put(ACTUALIZAR_REGISTRO_CODI, "Lo sentimos, ocurrio un error al actualizar el registro CoDi.");
        descError.put(ERROR_VALIDA_CURP, "Lo sentimos, ocurrio un error al intentar validar su curp.");
        descError.put(ERROR_CONCEPTOS_PRODUCTO, "Lo sentimos, ocurrio un error al dar de alta datos adicionales del producto a la cuenta.");
        descError.put(ERROR_CODIGO_REFERENCIA_NOT_FOUND, "El codigo de referencia no existe.");
        descError.put(ERROR_REGISTRAR_CODIGO, "Lo sentimos, ocurrio un error al registrar el codigo de referencia de la cuenta.");

    }

    public static String getMensajeError(int codigo) {
        return descError.getOrDefault(codigo, "Error desconocido en registro de cuenta");
    }
}
