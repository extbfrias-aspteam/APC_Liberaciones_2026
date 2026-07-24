package mx.net.asp.asp_pago_management.utilerias;

import org.springframework.stereotype.Component;

@Component
public class CorreoUtils {
    public String generaCorreoAnexoDetalleBeneficiario(String cuenta, String nombre) {
        return "<<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre + "</h1>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Hemos actualizado los datos de tu beneficiario. Adjunto encontrar&aacute;s el contrato de tu cuenta de ahorro con terminaci&oacute;n ******"+ cuenta.substring(cuenta.length() - 4) +" .</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Podr&aacute;s realizar dep&oacute;sitos a tu cuenta de ahorro a trav&eacute;s de SPEI las 24 horas del d&iacute;a usando tu CLABE Interbancaria, tambi&eacute;n podr&aacute;s realizar dep&oacute;sitos en cualquier sucursal ASP Integra Opciones o a trav&eacute;s de la red de Ventanilla F&aacute;cil.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "Para hacer un retiro de efectivo puedes acudir cualquier sucursal ASP Integra Opciones. Revisa aqu&iacute; la m&aacute;s cercana <a href=\"https://www.aspintegraopciones.com/fr/contacto/ubica-sucursal.html\">https://www.aspintegraopciones.com/fr/contacto/ubica-sucursal.html</a>. Tambi&eacute;n podr&aacute;s transferir a una cuenta de " +
                "cualquier banca comercial para retirar en cajeros autom&aacute;ticos.</p>" +
                "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
                "En caso de cualquier aclaraci&oacute;n o asistencia adicional comun&iacute;cate con nosotros al 800 462 7373 de lunes a viernes de 8:00 am a 8:00 pm.</p>";
    }

    public String generaCorreoReporteEstadoMovimiento(String nombre) {
        return "<h1 style=\"color: #3f5074; font-size: 18px; font-weight: 600; font-family: 'Verdana', Arial, Helvetica, sans-serif; margin-bottom: 2rem;\">Hola " + nombre  + "</h1>" +
        "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
        "Te enviamos el estado de movimientos que solicitaste desde la ASP Pago.</p>" +
        "<p style=\"color: #6a7794; font-family: 'Verdana', Arial, Helvetica, sans-serif; font-weight: 400; font-size: 14px; line-height: 30px; margin-bottom: 1rem; text-align: justify;\">" +
        "En caso de cualquier aclaraci&oacute;n podr&aacute;s llamarnos a nuestro contact center al 800 462 7373 de lunes a viernes de 8am a 8pm.</p>";
    }
}