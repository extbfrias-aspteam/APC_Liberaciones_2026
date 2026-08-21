package mx.net.asp.asp_pago_access.utilerias;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtils {
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
}
