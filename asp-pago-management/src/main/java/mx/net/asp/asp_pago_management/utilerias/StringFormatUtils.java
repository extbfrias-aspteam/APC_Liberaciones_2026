package mx.net.asp.asp_pago_management.utilerias;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class StringFormatUtils {

    private final ObjectMapper objectMapper;

    public StringFormatUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String limpiarEspacios(String cadena){
        return cadena.replaceAll("\\s+", "");
    }
    public String limpiarCaracteresEspeciales(String cadena){
        // Expresión regular para encontrar caracteres especiales excepto espacios en blanco
        String regex = "[^a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ.,-]";
        return cadena.replaceAll(regex, "");
    }


    public String limitarLongitud(String texto, int maxLength) {
        return texto.length() > maxLength ? texto.substring(0, maxLength) : texto;
    }

    public String toJsonSafe(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("No se pudo serializar el log", e);
            return "json_serialization_error";
        }
    }
}
