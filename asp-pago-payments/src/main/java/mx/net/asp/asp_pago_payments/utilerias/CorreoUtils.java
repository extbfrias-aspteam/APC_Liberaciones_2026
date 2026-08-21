package mx.net.asp.asp_pago_payments.utilerias;

import org.springframework.stereotype.Component;

@Component
public class CorreoUtils {
    public String generaCorreoDocumentacionCuentaSimplificada(String cuenta, String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "¡Felicidades! Ahora tienes una cuenta con ASP Integra Opciones. Adjunto encontrar&aacute;s tu contrato de ahorro con terminaci&oacute;n de n&uacute;mero de cuenta ******" + cuenta.substring(cuenta.length() - 4) + " .</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Podr&aacute;s realizar dep&oacute;sitos a tu cuenta de ahorro a trav&eacute;s de SPEI las 24 horas del d&iacute;a usando tu CLABE Interbancaria, tambi&eacute;n podr&aacute;s realizar dep&oacute;sitos en cualquier sucursal ASP Integra Opciones o a trav&eacute;s de la red de Ventanilla F&aacute;cil.</p>";
    }

    public String generaCorreoValidacion(String url, String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre  + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Para continuar con el registro de tu cuenta es necesario que des click al siguiente enlace para validar tu correo.</p>" +
                "<a href=\"" + url + "\" style=\"font-family: 'Verdana', Arial, Helvetica, sans-serif;display: block;padding: 1rem;background-color: #00b0f0;text-decoration: none; color: #fff;font-weight: bold;margin: 0 auto;width: 200px;text-align: center;font-size: 18px;margin-top: 2rem;\">Validar mi correo</a>";
    }
}