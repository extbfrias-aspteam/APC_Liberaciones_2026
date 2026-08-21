package mx.net.asp.asp_pago_access.utilerias;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CorreoUtils {
    //Validaciones para contraseña ASP Pago
    public boolean tieneCaracteresConsecutivos(String contrasena) {
        // Expresión regular para detectar tres caracteres consecutivos iguales
        Pattern patron = Pattern.compile("(.)\\1{2}");
        return patron.matcher(contrasena).find();
    }
    public boolean contieneNombreCliente(String contrasena, String nombreCliente) {
        // Convertir el nombre del cliente a minúsculas y dividirlo en partes
        String[] partesNombre = nombreCliente.toLowerCase().split(" ");
        // Convertir la contraseña a minúsculas
        String contrasenaSimplificada = contrasena.toLowerCase();
        // Verificar si alguna parte del nombre aparece en la contraseña
        for (String parte : partesNombre) {

            if (!parte.isEmpty() && contrasenaSimplificada.contains(parte)) {
                return true;
            }
        }
        return false;
    }

    public String generaCorreoPeticionCambioPass(String url, String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Te lleg&oacute; este correo ya que se realiz&oacute; una petici&oacute;n para cambio de contrase&ntilde;a en tu ASP Pago.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Para continuar con el proceso haz click en el siguiente enlace, el cual tiene una vigencia de 10 minutos:</p>" +
                "<a href=\"" + url + "\" style=\"font-family: 'Verdana', Arial, Helvetica, sans-serif;display: block;padding: 1rem;background-color: #00b0f0;text-decoration: none; color: #fff;font-weight: bold;margin: 0 auto;width: 70%;text-align: center;font-size: 18px;margin-top: 2rem;margin-bottom: 2rem;\">" +
                "Cambiar contrase&ntilde;a</a>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Si no realizaste esta solicitud, comun&iacute;cate con nosotros al 800 462 7373 o al 612 157 7730.</p>";
    }

    public String generaCorreoCambioPass(String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Se realiz&oacute; con &eacute;xito el cambio de tu contrase&ntilde;a. En caso de que no hayas solicitado este cambio te pedimos comunicarte al 800 462 7373 o al 612 157 7730.</p>";
    }

    public String generaCorreoPeticionPinOTP(String pinOtp, String nombre, String minutosOTP) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Recibimos tu solicitud para recuperar o cambiar tu <b>contrase&ntilde;a</b> en la app <b>ASP Pago</b>.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Para continuar el proceso, te compartimos tu <b>c&oacute;digo temporal de un solo uso (OTP)</b>:</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "<b>"+ pinOtp +"</b></p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Este c&oacute;digo tiene una vigencia de <b>"+ minutosOTP +" minutos</b> y solo puede utilizarse una vez.<br>" +
                "Por tu seguridad, <b>no compartas este c&oacute;digo con nadie</b>. El equipo de ASP nunca te lo solicitar&aacute; por llamada, correo o mensaje.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Si no realizaste esta solicitud, con&aacute;tactanos de inmediato al 800 462 7373 o al 612 157 7730.</p>";
    }

    public String generaCorreoPeticionOTPPwd(String pinOtp, String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Recibimos tu solicitud para recuperar o cambiar tu <b>contraseña</b> en la app <b>ASP Pago</b>.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Para continuar el proceso, te compartimos tu <b>c&oacute;digo temporal de un solo uso (OTP)</b>:</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "<b>"+ pinOtp +"</b></p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Este c&oacute;digo tiene una vigencia de <b>5 minutos</b> y solo puede utilizarse una vez.<br>" +
                "Por tu seguridad, <b>no compartas este c&oacute;digo con nadie</b>. El equipo de ASP nunca te lo solicitar&aacute; por llamada, correo o mensaje.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Si no realizaste esta solicitud, con&aacute;tactanos de inmediato al 800 462 7373 o al 612 157 7730.</p>";
    }
}
