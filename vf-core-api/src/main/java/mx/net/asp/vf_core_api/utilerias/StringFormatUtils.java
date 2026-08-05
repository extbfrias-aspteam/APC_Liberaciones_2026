package mx.net.asp.vf_core_api.utilerias;

import org.springframework.stereotype.Component;

@Component
public class StringFormatUtils {
    public String limpiarEspacios(String cadena){
        return cadena.replaceAll("\\s+", "");
    }
    public String limpiarCaracteresEspeciales(String cadena){
        return cadena.replaceAll("\\s+", "");
    }
    public String limitarLongitud(String texto, int maxLength) {
        return texto.length() > maxLength ? texto.substring(0, maxLength) : texto;
    }
}
