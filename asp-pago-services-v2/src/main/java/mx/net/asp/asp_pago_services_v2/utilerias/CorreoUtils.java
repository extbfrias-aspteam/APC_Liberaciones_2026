package mx.net.asp.asp_pago_services_v2.utilerias;

import org.springframework.stereotype.Component;

@Component
public class CorreoUtils {
    public String generarCorreoRevisionManual(String nombre) {
        return "<p>Se ha generado una nueva solicitud para subir de nivel una Cuenta fácil que requiere " +
               "revisión manual. El cliente no obtuvo una aprobación automática y necesita que verifiques su " +
               "documentación desde el portal de Incode.</p>" +
    
               "<p><strong>Cliente:</strong> " + nombre + "</p>" +
    
               "<p>Por favor, revisa la solicitud aquí: " +
               "<a href=\"https://dashboard.incode.com/log-in\" target=\"_blank\">" +
               "https://dashboard.incode.com/log-in</a></p>" +
    
               "<p>Gracias.</p>" +
    
               "<p>Este correo es únicamente informativo y forma parte de las notificaciones internas automáticas " +
               "de ASP Integra Opciones. Por favor, no reenviar fuera de la organización.</p>";
    }
    

    public String generarCorreoRechazoSubidaNivel(String nombre) {
        return  "<p>Se informa que un cliente no fue aprobado en su proceso para subir de nivel su Cuenta fácil.</p>" +

                "<p><strong>Cliente:</strong>" + nombre + "</p>" +

                "<p>El resultado de su validación en Incode fue <strong style=\"color: red;\">no aprobado</strong>.</p>" +

                "<p>Este cliente ya puede ser consultado en la plataforma para su seguimiento o registro de intentos.</p>" +

                "<p>Este correo es únicamente informativo y forma parte de las notificaciones internas automáticas de ASP Integra Opciones. Por favor, no reenviar fuera de la organización.</p>";
    }
    
    public String generarCorreoAprobacionSubidaNivel(String nombre) {
        return "<p>Hola " + nombre + ",</p>" +
    
               "<p>Te confirmamos que tu solicitud para subir de nivel ha sido aprobada con éxito.</p>" +
    
               "<p>Ahora, tu cuenta ha sido actualizada a <strong>Cuenta fácil Avanzada</strong>, lo que te permitirá " +
               "disfrutar de un mayor límite transaccional y de los beneficios que ofrece este nivel.</p>" +
    
               "<p>Puedes ingresar a tu App ASP Pago para ver reflejado este cambio.</p>";
    }
    
    
}